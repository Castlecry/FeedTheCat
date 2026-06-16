package com.example.catguardian.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 提交领养申请请求DTO
 */
@Data
public class CreateAdoptionRequest {
    
    @NotNull(message = "流浪猫ID不能为空")
    private Long catId;
    
    @NotBlank(message = "居住地址不能为空")
    private String livingAddress;
    
    @NotNull(message = "住房类型不能为空")
    private Integer housingType;
    
    @NotNull(message = "家人是否同意不能为空")
    private Integer familyAgree;
    
    private String petExperience;
    
    private Integer hasAbandoned;
}
