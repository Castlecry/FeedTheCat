package com.example.catguardian.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class HospitalDiscountResponse {
    
    private Long id;
    
    private Long hospitalId;
    
    private String title;
    
    private String description;
    
    private Integer discountType;
    
    private BigDecimal discountValue;
    
    private BigDecimal minAmount;
    
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    private Integer status;
    
    private LocalDateTime createdAt;
}