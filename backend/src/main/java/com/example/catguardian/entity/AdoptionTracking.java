package com.example.catguardian.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "adoption_tracking")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdoptionTracking {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "application_id", nullable = false)
    private Long applicationId;
    
    @Column(name = "tracking_time", nullable = false)
    private LocalDateTime trackingTime;
    
    @Column(name = "status", nullable = false)
    private Integer status;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "photos", columnDefinition = "TEXT")
    private String photos;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (trackingTime == null) {
            trackingTime = LocalDateTime.now();
        }
    }
}