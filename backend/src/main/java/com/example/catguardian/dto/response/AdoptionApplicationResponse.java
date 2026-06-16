package com.example.catguardian.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 领养申请响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdoptionApplicationResponse {
    
    private Long id;
    
    private Long catId;
    
    private String catName;
    
    private Long applicantId;
    
    private String applicantName;
    
    private String livingAddress;
    
    private Integer housingType;
    
    private String housingTypeDescription;
    
    private Integer familyAgree;
    
    private String petExperience;
    
    private Integer hasAbandoned;
    
    private Integer status;
    
    private String statusDescription;
    
    private String platformNote;
    
    private String feederNote;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
