package com.example.catguardian.dto.response;

import lombok.Builder;
import lombok.Data;

/**
 * AI饲养建议响应DTO
 */
@Data
@Builder
public class AiAdviceResponse {

    /**
     * 用户的问题
     */
    private String question;

    /**
     * 猫咪品种（如果用户提供了）
     */
    private String catBreed;

    /**
     * 猫咪年龄（如果用户提供了）
     */
    private String catAge;

    /**
     * AI生成的饲养建议
     */
    private String advice;
}
