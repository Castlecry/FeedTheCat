package com.example.catguardian.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateCatProfileRequest {
    
    @NotBlank(message = "猫咪名字不能为空")
    private String name;
    
    private String breed;
    
    private String age;
    
    private Integer gender;
    
    private String healthStatus;
    
    private String dietaryHabits;
    
    private String taboos;
    
    private Integer sterilized;
    
    private Integer vaccinated;
    
    private LocalDate nextVaccineDate;
    
    private LocalDate lastDewormDate;
    
    private LocalDate nextDewormDate;
    
    private String insuranceInfo;
    
    private String medicalRecords;
    
    private String avatar;
}