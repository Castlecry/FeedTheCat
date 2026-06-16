package com.example.catguardian.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "exchange_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "points", nullable = false)
    private Integer points;
    
    @Column(name = "stock")
    @Builder.Default
    private Integer stock = 0;
    
    @Column(name = "type", length = 20)
    private String type;
    
    @Column(name = "image", length = 255)
    private String image;
    
    @Column(name = "status")
    @Builder.Default
    private Integer status = 1;
    
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
