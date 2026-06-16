package com.example.catguardian.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ProductResponse {
    
    private Long id;
    
    private String name;
    
    private Integer category;
    
    private BigDecimal price;
    
    private BigDecimal originalPrice;
    
    private String description;
    
    private String images;
    
    private Integer stock;
    
    private Integer sales;
    
    private Integer status;
    
    private LocalDateTime createdAt;
}