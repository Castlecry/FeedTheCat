package com.example.catguardian.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * AI头像生成请求DTO
 *
 * 用户描述想要的头像效果，AI根据描述和风格生成猫咪头像。
 * 后续接入真实AI图片生成API时，prompt和style会组合成图片生成指令。
 */
@Data
public class AiAvatarRequest {

    /**
     * 用户对头像的描述（必填）
     * 例如："一只戴着皇冠的橘猫"、"布偶猫穿着宇航服"
     */
    @NotBlank(message = "请输入头像描述")
    private String prompt;

    /**
     * 头像风格（选填，默认cartoon）
     * 可选值：cute(可爱风)、realistic(写实风)、cartoon(卡通风)、pixel(像素风)、watercolor(水彩风)
     */
    private String style;
}
