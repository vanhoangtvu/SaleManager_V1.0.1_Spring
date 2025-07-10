package com.example.salem2025.service.impl;

import com.example.salem2025.model.dto.OrdersDTO;
import com.example.salem2025.repository.OrdersRepository;
import com.example.salem2025.repository.entity.OrdersEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrdersServiceImpl {

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<OrdersDTO> getAllOrders() {
        return ordersRepository.findAll().stream()
                .map(order -> modelMapper.map(order, OrdersDTO.class))
                .collect(Collectors.toList());
    }

    public OrdersDTO saveOrder(OrdersDTO dto) {
        OrdersEntity entity = modelMapper.map(dto, OrdersEntity.class);
        entity = ordersRepository.save(entity);
        return modelMapper.map(entity, OrdersDTO.class);
    }
}
