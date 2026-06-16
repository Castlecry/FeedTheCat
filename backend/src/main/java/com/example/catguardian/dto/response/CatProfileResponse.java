package com.example.catguardian.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class CatProfileResponse {
    
    private Long id;
    
    private Long userId;
    
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
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private Boolean vaccineReminder;
    
    private Boolean dewormReminder;
}