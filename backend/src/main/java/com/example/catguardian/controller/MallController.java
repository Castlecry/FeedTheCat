package com.example.catguardian.controller;

import com.example.catguardian.dto.response.ApiResponse;
import com.example.catguardian.dto.response.ProductResponse;
import com.example.catguardian.entity.Product;
import com.example.catguardian.exception.BusinessException;
import com.example.catguardian.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/mall")
@RequiredArgsConstructor
public class MallController {
    
    private final ProductRepository productRepository;
    
    @GetMapping("/products")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getProducts(
            @RequestParam(required = false) Integer category) {
        List<ProductResponse> products;
        if (category != null) {
            products = productRepository.findByCategoryAndStatusOrderByCreatedAtDesc(category, 1).stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
        } else {
            products = productRepository.findByStatusOrderByCreatedAtDesc(1).stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
        }
        return ResponseEntity.ok(ApiResponse.success(products));
    }
    
    @GetMapping("/products/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProduct(@PathVariable Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("商品不存在"));
        return ResponseEntity.ok(ApiResponse.success(convertToResponse(product)));
    }
    
    private ProductResponse convertToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .category(product.getCategory())
                .price(product.getPrice())
                .originalPrice(product.getOriginalPrice())
                .description(product.getDescription())
                .images(product.getImages())
                .stock(product.getStock())
                .sales(product.getSales())
                .status(product.getStatus())
                .createdAt(product.getCreatedAt())
                .build();
    }
}