package com.example.catguardian.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 流浪猫信息实体类
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "stray_cats")
public class StrayCat {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "feeder_id", nullable = false)
    private Long feederId;
    
    @Column(length = 50)
    private String name;
    
    @Column(length = 50)
    private String breed;
    
    @Column(length = 20)
    private String age;
    
    private Integer gender;
    
    @Column(name = "health_status", columnDefinition = "TEXT", nullable = false)
    private String healthStatus;
    
    @Builder.Default
    private Integer sterilized = 0;
    
    @Builder.Default
    private Integer vaccinated = 0;
    
    @Column(length = 200)
    private String location;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private String photos;
    
    @Builder.Default
    private Integer status = 0;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feeder_id", insertable = false, updatable = false)
    private User feeder;
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
