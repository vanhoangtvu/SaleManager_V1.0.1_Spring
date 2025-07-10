package com.example.salem2025.service;

import com.example.salem2025.model.dto.ProductDTO;
import com.example.salem2025.repository.ProductRepository;
import com.example.salem2025.repository.entity.ProductEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public abstract class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<ProductDTO> getAll() {
        return productRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public ProductDTO getById(Long id) {
        ProductEntity product = productRepository.findById(id).orElse(null);
        return (product != null) ? toDTO(product) : null;
    }

    public ProductDTO create(ProductDTO dto) {
        ProductEntity entity = toEntity(dto);
        return toDTO(productRepository.save(entity));
    }

    public ProductDTO update(Long id, ProductDTO dto) {
        ProductEntity existing = productRepository.findById(id).orElse(null);
        if (existing == null) return null;

        existing.setName(dto.getName());
        existing.setPrice(dto.getPrice());
        existing.setQuantity(dto.getQuantity());
        existing.setDescription(dto.getDescription());

        return toDTO(productRepository.save(existing));
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    // Convert methods
    private ProductDTO toDTO(ProductEntity entity) {
        ProductDTO dto = new ProductDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setPrice(entity.getPrice());
        dto.setQuantity(entity.getQuantity());
        dto.setDescription(entity.getDescription());
        return dto;
    }

    private ProductEntity toEntity(ProductDTO dto) {
        ProductEntity entity = new ProductEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setPrice(dto.getPrice());
        entity.setQuantity(dto.getQuantity());
        entity.setDescription(dto.getDescription());
        return entity;
    }

    public List<ProductDTO> getAllProducts() {
        return getAll();
    }

    public ProductDTO createProduct(ProductDTO dto) {
        return create(dto);
    }

    public abstract ProductDTO saveProduct(ProductDTO dto);
}
