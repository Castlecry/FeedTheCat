package com.example.catguardian.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class HospitalResponse {
    
    private Long id;
    
    private String name;
    
    private String address;
    
    private String phone;
    
    private String businessHours;
    
    private String description;
    
    private String photos;
    
    private Integer status;
    
    private LocalDateTime createdAt;
}