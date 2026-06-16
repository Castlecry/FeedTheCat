package com.example.catguardian.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * TNR申请实体类
 */
@Entity
@Table(name = "tnr_applications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TnrApplication {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "hospital_id")
    private Long hospitalId;
    
    @Column(name = "cat_name", length = 50)
    private String catName;
    
    @Column(name = "location", nullable = false, length = 200)
    private String location;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "photos", columnDefinition = "TEXT")
    private String photos;
    
    @Column(name = "status")
    @Builder.Default
    private Integer status = 0;
    
    @Column(name = "reject_reason", columnDefinition = "TEXT")
    private String rejectReason;
    
    @Column(name = "operation_time")
    private LocalDateTime operationTime;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}