package com.example.catguardian.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PointsBalanceResponse {
    
    private Long userId;
    
    private Integer balance;
    
    private Integer totalEarned;
    
    private Integer totalSpent;
}