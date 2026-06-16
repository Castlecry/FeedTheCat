package com.example.catguardian.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ServiceProviderApplyRequest {
    
    @NotBlank(message = "身份证正面照不能为空")
    private String idCardFront;
    
    @NotBlank(message = "身份证反面照不能为空")
    private String idCardBack;
    
    private String criminalRecord;
    
    private String trainingCertificate;
    
    private Integer hasSignedAgreement;
}