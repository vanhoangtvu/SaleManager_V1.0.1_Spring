package com.example.salem2025.service;

import com.example.salem2025.repository.OrdersRepository;
import com.example.salem2025.repository.CustomerRepository;
import com.example.salem2025.repository.ProductRepository;
import com.example.salem2025.repository.entity.*;
import com.example.salem2025.model.dto.CreateOrderDTO;
import com.example.salem2025.model.dto.UpdateOrderStatusDTO;
import com.example.salem2025.model.dto.OrdersDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class OrdersService {

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserAccountService userAccountService;

    public List<OrdersDTO> getAllOrders() {
        return ordersRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<OrdersDTO> getOrdersByUser(UserAccountEntity user) {
        return ordersRepository.findByUserAccountOrderByOrderDateDesc(user).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<OrdersDTO> getOrdersByUserAndStatus(UserAccountEntity user, String status) {
        return ordersRepository.findByUserAccountAndStatus(user, status.toUpperCase()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<OrdersDTO> getOrdersByStatus(String status) {
        return ordersRepository.findByStatus(status.toUpperCase()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public OrdersDTO getOrderById(Long id) {
        OrdersEntity order = ordersRepository.findById(id).orElse(null);
        return order != null ? convertToDTO(order) : null;
    }

    @Transactional
    public OrdersDTO createOrderForUser(CreateOrderDTO orderDTO, UserAccountEntity user) throws Exception {
        // Validate that user profile is complete
        if (!userAccountService.isProfileComplete(user)) {
            throw new Exception("Vui lòng cập nhật địa chỉ và số điện thoại trước khi đặt hàng");
        }

        // Validate order items
        if (orderDTO.getItems() == null || orderDTO.getItems().isEmpty()) {
            throw new Exception("Đơn hàng phải có ít nhất 1 sản phẩm");
        }

        // Create customer entity from user profile
        CustomerEntity customer = new CustomerEntity();
        customer.setName(user.getFullName() != null ? user.getFullName() : user.getUsername());
        customer.setPhone(user.getPhone());
        customer.setEmail(user.getEmail());
        customer.setAddress(user.getAddress());
        
        // Save customer first
        customer = customerRepository.save(customer);

        // Create order
        OrdersEntity order = new OrdersEntity();
        order.setUserAccount(user);
        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING"); // Default status
        order.setNotes(orderDTO.getNotes()); // Set notes from request

        // Create order details
        List<OrderDetailEntity> orderDetails = new ArrayList<>();
        for (CreateOrderDTO.OrderItemDTO item : orderDTO.getItems()) {
            // Validate product exists
            ProductEntity product = productRepository.findById(item.getProductId())
                .orElseThrow(() -> new Exception("Sản phẩm ID " + item.getProductId() + " không tồn tại"));

            // Validate quantity
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new Exception("Số lượng phải lớn hơn 0");
            }

            // Check if enough stock
            if (product.getQuantity() < item.getQuantity()) {
                throw new Exception("Sản phẩm '" + product.getName() + "' không đủ hàng. Còn lại: " + product.getQuantity());
            }

            // Create order detail
            OrderDetailEntity detail = new OrderDetailEntity();
            detail.setOrder(order);
            detail.setProduct(product);
            detail.setQuantity(item.getQuantity());
            detail.setUnitPrice(product.getPrice());

            orderDetails.add(detail);
        }

        order.setOrderDetails(orderDetails);

        // Save order (cascade will save order details)
        OrdersEntity savedOrder = ordersRepository.save(order);

        // Update product quantities
        for (CreateOrderDTO.OrderItemDTO item : orderDTO.getItems()) {
            ProductEntity product = productRepository.findById(item.getProductId()).get();
            product.setQuantity(product.getQuantity() - item.getQuantity());
            productRepository.save(product);
        }

        return convertToDTO(savedOrder);
    }

    public OrdersDTO updateOrderStatus(Long orderId, UpdateOrderStatusDTO statusDTO) throws Exception {
        OrdersEntity order = ordersRepository.findById(orderId)
            .orElseThrow(() -> new Exception("Đơn hàng không tồn tại"));

        // Validate status
        String newStatus = statusDTO.getStatus().toUpperCase();
        if (!isValidStatus(newStatus)) {
            throw new Exception("Trạng thái không hợp lệ. Cho phép: PENDING, CONFIRMED, SHIPPING, DELIVERED, CANCELLED");
        }

        order.setStatus(newStatus);
        OrdersEntity savedOrder = ordersRepository.save(order);
        return convertToDTO(savedOrder);
    }

    // Method for users to cancel their own orders
    @Transactional
    public OrdersDTO cancelOrderByUser(Long orderId, UserAccountEntity user) throws Exception {
        OrdersEntity order = ordersRepository.findById(orderId)
            .orElseThrow(() -> new Exception("Đơn hàng không tồn tại"));

        // Check if user owns this order
        if (!order.getUserAccount().getId().equals(user.getId())) {
            throw new Exception("Bạn không có quyền hủy đơn hàng này");
        }

        // Check if order can be cancelled (only PENDING orders can be cancelled by user)
        if (!"PENDING".equals(order.getStatus())) {
            throw new Exception("Chỉ có thể hủy đơn hàng ở trạng thái 'PENDING'");
        }

        // Update status to CANCELLED
        order.setStatus("CANCELLED");
        
        // Restore product quantities
        for (OrderDetailEntity detail : order.getOrderDetails()) {
            ProductEntity product = detail.getProduct();
            product.setQuantity(product.getQuantity() + detail.getQuantity());
            productRepository.save(product);
        }

        OrdersEntity savedOrder = ordersRepository.save(order);
        return convertToDTO(savedOrder);
    }

    private boolean isValidStatus(String status) {
        return status.equals("PENDING") || status.equals("CONFIRMED") || 
               status.equals("SHIPPING") || status.equals("DELIVERED") || 
               status.equals("CANCELLED");
    }

    public boolean deleteOrder(Long id) {
        if (!ordersRepository.existsById(id)) {
            return false;
        }
        ordersRepository.deleteById(id);
        return true;
    }

    public boolean canUserAccessOrder(OrdersEntity order, UserAccountEntity user, String userRole) {
        // Admin can access all orders
        if ("ADMIN".equals(userRole)) {
            return true;
        }
        // Users can only access their own orders
        return order.getUserAccount() != null && order.getUserAccount().getId().equals(user.getId());
    }

    public boolean canUserAccessOrder(Long orderId, UserAccountEntity user, String userRole) {
        // Admin can access all orders
        if ("ADMIN".equals(userRole)) {
            return true;
        }
        // Get the order entity to check ownership
        OrdersEntity order = ordersRepository.findById(orderId).orElse(null);
        if (order == null) {
            return false;
        }
        return order.getUserAccount() != null && order.getUserAccount().getId().equals(user.getId());
    }

    // DTO conversion methods
    private OrdersDTO convertToDTO(OrdersEntity order) {
        OrdersDTO dto = new OrdersDTO();
        dto.setId(order.getId());
        dto.setOrderDate(order.getOrderDate());
        dto.setStatus(order.getStatus());
        dto.setNotes(order.getNotes());

        // Convert customer
        if (order.getCustomer() != null) {
            CustomerEntity customer = order.getCustomer();
            OrdersDTO.CustomerDTO customerDTO = new OrdersDTO.CustomerDTO(
                customer.getId(),
                customer.getName(),
                customer.getPhone(),
                customer.getEmail(),
                customer.getAddress()
            );
            dto.setCustomer(customerDTO);
        }

        // Convert user account
        if (order.getUserAccount() != null) {
            UserAccountEntity user = order.getUserAccount();
            OrdersDTO.UserAccountSummaryDTO userDTO = new OrdersDTO.UserAccountSummaryDTO(
                user.getId(),
                user.getUsername(),
                user.getRole(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getAddress()
            );
            dto.setUserAccount(userDTO);
        }

        // Convert order details
        if (order.getOrderDetails() != null) {
            List<OrdersDTO.OrderDetailDTO> detailDTOs = order.getOrderDetails().stream()
                .map(detail -> new OrdersDTO.OrderDetailDTO(
                    detail.getId(),
                    detail.getProduct().getId(),
                    detail.getProduct().getName(),
                    detail.getQuantity(),
                    detail.getUnitPrice(),
                    detail.getQuantity() * detail.getUnitPrice()
                ))
                .collect(Collectors.toList());
            dto.setOrderDetails(detailDTOs);
        }

        return dto;
    }
}
