package com.example.catguardian.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class OrderEvaluationResponse {
    
    private Long id;
    
    private Long orderId;
    
    private Long clientId;
    
    private Long serviceId;
    
    private Integer rating;
    
    private String comment;
    
    private LocalDateTime createdAt;
}