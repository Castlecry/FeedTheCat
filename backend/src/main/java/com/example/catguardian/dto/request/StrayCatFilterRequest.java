package com.example.catguardian.dto.request;

import lombok.Data;

/**
 * 流浪猫筛选请求DTO
 */
@Data
public class StrayCatFilterRequest {
    
    private String breed;
    
    private String age;
    
    private Integer gender;
    
    private Integer sterilized;
    
    private Integer vaccinated;
    
    private String location;
}
