package com.example.catguardian.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TnrApplicationResponse {
    
    private Long id;
    
    private Long userId;
    
    private Long hospitalId;
    
    private String catName;
    
    private String location;
    
    private String description;
    
    private String photos;
    
    private Integer status;
    
    private String rejectReason;
    
    private LocalDateTime operationTime;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}