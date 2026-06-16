package com.example.catguardian.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 服务记录实体类
 */
@Entity
@Table(name = "service_records")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "order_id", nullable = false)
    private Long orderId;
    
    @Column(name = "service_time", nullable = false)
    private LocalDateTime serviceTime;
    
    @Column(name = "video_url", length = 255)
    private String videoUrl;
    
    @Column(name = "lock_photo_url", length = 255)
    private String lockPhotoUrl;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (serviceTime == null) {
            serviceTime = LocalDateTime.now();
        }
    }
}