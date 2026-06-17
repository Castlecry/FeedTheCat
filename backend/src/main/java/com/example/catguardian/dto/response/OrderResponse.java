package com.example.catguardian.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    
    private Long id;
    
    private String orderNo;
    
    private Long clientId;
    
    private String clientName;
    
    private Long serviceId;
    
    private String serviceName;
    
    private Integer status;
    
    private String statusDescription;
    
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    private Integer visitFrequency;
    
    private String feedingRequirements;
    
    private String litterCleanStandard;
    
    private String specialCare;
    
    private Integer entryMethod;
    
    private String keyStorageInfo;
    
    private String emergencyContact;
    
    private String address;
    
    private BigDecimal totalAmount;
    
    private BigDecimal actualPayment;
    
    private BigDecimal refundAmount;
    
    private BigDecimal commissionRate;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
