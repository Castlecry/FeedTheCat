package com.example.catguardian.dto.request;

import lombok.Data;

/**
 * AI饲养建议请求DTO
 */
@Data
public class AiAdviceRequest {
    
    /**
     * 建议主题：diet, health, behavior, environment, play
     */
    private String topic;
}
