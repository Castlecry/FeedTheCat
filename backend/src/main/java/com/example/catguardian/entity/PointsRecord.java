package com.example.catguardian.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "points_records")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointsRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "type", nullable = false)
    private Integer type;
    
    @Column(name = "source", nullable = false, length = 50)
    private String source;
    
    @Column(name = "amount", nullable = false)
    private Integer amount;
    
    @Column(name = "balance_before", nullable = false)
    private Integer balanceBefore;
    
    @Column(name = "balance_after", nullable = false)
    private Integer balanceAfter;
    
    @Column(name = "related_id")
    private Long relatedId;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}