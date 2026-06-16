package com.example.catguardian.dto.response;

import lombok.Builder;
import lombok.Data;

/**
 * AI猫咪语录响应DTO
 */
@Data
@Builder
public class AiQuoteResponse {
    
    private String quote;
}
