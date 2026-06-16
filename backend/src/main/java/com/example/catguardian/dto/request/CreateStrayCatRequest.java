package com.example.catguardian.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 发布流浪猫信息请求DTO
 */
@Data
public class CreateStrayCatRequest {
    
    private String name;
    
    private String breed;
    
    private String age;
    
    private Integer gender;
    
    @NotBlank(message = "健康状况不能为空")
    private String healthStatus;
    
    private Integer sterilized;
    
    private Integer vaccinated;
    
    private String location;
    
    private String description;
    
    private List<String> photos;
}
