package com.example.salem2025.Api.v1;

import com.example.salem2025.repository.entity.OrdersEntity;
import com.example.salem2025.repository.OrdersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrdersController {

    @Autowired
    private OrdersRepository ordersRepository;

    @GetMapping
    public List<OrdersEntity> getAllOrders() {
        return ordersRepository.findAll();
    }

    @PostMapping
    public OrdersEntity createOrder(@RequestBody OrdersEntity order) {
        return ordersRepository.save(order);
    }
}