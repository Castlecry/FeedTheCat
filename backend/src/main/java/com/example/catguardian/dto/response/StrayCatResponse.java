package com.example.catguardian.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 流浪猫信息响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StrayCatResponse {
    
    private Long id;
    
    private Long feederId;
    
    private String feederName;
    
    private String name;
    
    private String breed;
    
    private String age;
    
    private Integer gender;
    
    private String healthStatus;
    
    private Integer sterilized;
    
    private Integer vaccinated;
    
    private String location;
    
    private String description;
    
    private List<String> photos;
    
    private Integer status;
    
    private String statusDescription;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
