package com.example.salem2025.Api.v1;

import com.example.salem2025.model.dto.ProductDTO;
import com.example.salem2025.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/products")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Product Management (Admin Only)", description = "API quản lý sản phẩm - CHỈ ADMIN")
@SecurityRequirement(name = "bearerAuth")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    @Operation(summary = "Tạo sản phẩm mới", 
               description = "CHỈ ADMIN mới có thể tạo sản phẩm mới")
    public ProductDTO createProduct(@RequestBody ProductDTO dto) {
        System.out.println("=== CREATE PRODUCT CONTROLLER CALLED ===");
        System.out.println("DTO received: " + dto.getName());
        
        dto.setId(null);
        
        ProductDTO result = productService.createProduct(dto);
        System.out.println("Product created successfully");
        return result;
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật sản phẩm", 
               description = "CHỈ ADMIN mới có thể cập nhật sản phẩm")
    public ResponseEntity<ProductDTO> updateProduct(
            @Parameter(description = "ID của sản phẩm cần cập nhật", example = "1")
            @PathVariable Long id, 
            @RequestBody ProductDTO dto) {
        ProductDTO updated = productService.update(id, dto);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa sản phẩm", 
               description = "CHỈ ADMIN mới có thể xóa sản phẩm")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "ID của sản phẩm cần xóa", example = "1")
            @PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
