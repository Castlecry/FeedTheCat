package com.example.catguardian.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 20)
    private String phone;
    
    @Column(nullable = false, length = 255)
    private String password;
    
    @Column(nullable = false, length = 50)
    private String name;
    
    @Column(name = "id_card", length = 18)
    private String idCard;
    
    @Column(name = "real_name", length = 50)
    private String realName;
    
    @Column(name = "face_image", length = 255)
    private String faceImage;
    
    @Column(name = "auth_status")
    @Builder.Default
    private Integer authStatus = 0;
    
    @Column(name = "auth_reject_reason", columnDefinition = "TEXT")
    private String authRejectReason;
    
    @Column(name = "auth_time")
    private LocalDateTime authTime;
    
    @Column(length = 255)
    private String avatar;
    
    @Column(length = 500)
    private String address;
    
    @Column(nullable = false)
    @Builder.Default
    private Integer role = 0;
    
    @Column(name = "credit_score")
    @Builder.Default
    private Integer creditScore = 100;
    
    @Column(nullable = false)
    @Builder.Default
    private Integer status = 1;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
