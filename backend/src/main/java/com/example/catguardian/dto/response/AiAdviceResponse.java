package com.example.catguardian.dto.response;

import lombok.Builder;
import lombok.Data;

/**
 * AI饲养建议响应DTO
 */
@Data
@Builder
public class AiAdviceResponse {
    
    private String topic;
    
    private String advice;
}
