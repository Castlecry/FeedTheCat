package com.example.catguardian.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 创建订单请求DTO
 */
@Data
public class CreateOrderRequest {
    
    @NotNull(message = "服务开始时间不能为空")
    private LocalDateTime startTime;
    
    @NotNull(message = "服务结束时间不能为空")
    private LocalDateTime endTime;
    
    @NotNull(message = "上门频次不能为空")
    @Positive(message = "上门频次必须大于0")
    private Integer visitFrequency;
    
    private String feedingRequirements;
    
    private String litterCleanStandard;
    
    private String specialCare;
    
    @NotNull(message = "入户方式不能为空")
    private Integer entryMethod;
    
    private String keyStorageInfo;
    
    private String emergencyContact;
    
    @NotBlank(message = "服务地址不能为空")
    private String address;
    
    @NotNull(message = "订单总额不能为空")
    @Positive(message = "订单总额必须大于0")
    private BigDecimal totalAmount;
    
    private List<Long> catIds;
}
