package com.example.salem2025.service.impl;

import com.example.salem2025.model.dto.ProductDTO;
import com.example.salem2025.repository.ProductRepository;
import com.example.salem2025.repository.entity.ProductEntity;
import com.example.salem2025.service.ProductService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl extends ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public ProductDTO saveProduct(ProductDTO dto) {
        ProductEntity entity = modelMapper.map(dto, ProductEntity.class);
        entity = productRepository.save(entity);
        return modelMapper.map(entity, ProductDTO.class);
    }
}
