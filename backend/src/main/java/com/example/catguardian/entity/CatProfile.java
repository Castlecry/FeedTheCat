package com.example.catguardian.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cat_profiles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CatProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "name", nullable = false, length = 50)
    private String name;
    
    @Column(name = "breed", length = 50)
    private String breed;
    
    @Column(name = "age", length = 20)
    private String age;
    
    @Column(name = "gender")
    private Integer gender;
    
    @Column(name = "health_status", columnDefinition = "TEXT")
    private String healthStatus;
    
    @Column(name = "dietary_habits", columnDefinition = "TEXT")
    private String dietaryHabits;
    
    @Column(name = "taboos", columnDefinition = "TEXT")
    private String taboos;
    
    @Column(name = "sterilized")
    private Integer sterilized;
    
    @Column(name = "vaccinated")
    private Integer vaccinated;
    
    @Column(name = "next_vaccine_date")
    private LocalDate nextVaccineDate;
    
    @Column(name = "last_deworm_date")
    private LocalDate lastDewormDate;
    
    @Column(name = "next_deworm_date")
    private LocalDate nextDewormDate;
    
    @Column(name = "insurance_info", columnDefinition = "TEXT")
    private String insuranceInfo;
    
    @Column(name = "medical_records", columnDefinition = "TEXT")
    private String medicalRecords;
    
    @Column(name = "avatar", length = 255)
    private String avatar;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}