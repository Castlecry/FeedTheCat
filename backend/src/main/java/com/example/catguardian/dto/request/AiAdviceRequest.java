package com.example.catguardian.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * AI饲养建议请求DTO
 *
 * 用户以自然语言描述问题，AI根据猫咪信息生成个性化建议。
 * 后续接入真实AI API时，这些字段会组合成prompt发送给大模型。
 */
@Data
public class AiAdviceRequest {

    /**
     * 用户的问题描述（必填）
     * 例如："猫咪不吃东西怎么办？"、"猫咪总是抓沙发怎么训练？"
     */
    @NotBlank(message = "请输入您的问题")
    private String question;

    /**
     * 猫咪品种（选填，帮助AI给出更精准的建议）
     * 例如："橘猫"、"英短"、"布偶猫"
     */
    private String catBreed;

    /**
     * 猫咪年龄（选填）
     * 例如："2岁"、"6个月"、"3岁"
     */
    private String catAge;
}
