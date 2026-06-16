package com.example.catguardian.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 领养申请实体类
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "adoption_applications")
public class AdoptionApplication {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "cat_id", nullable = false)
    private Long catId;
    
    @Column(name = "applicant_id", nullable = false)
    private Long applicantId;
    
    @Column(name = "living_address", nullable = false, length = 500)
    private String livingAddress;
    
    @Column(name = "housing_type", nullable = false)
    private Integer housingType;
    
    @Column(name = "family_agree", nullable = false)
    private Integer familyAgree;
    
    @Column(name = "pet_experience", columnDefinition = "TEXT")
    private String petExperience;
    
    @Column(name = "has_abandoned")
    @Builder.Default
    private Integer hasAbandoned = 0;
    
    @Builder.Default
    private Integer status = 0;
    
    @Column(name = "platform_note", columnDefinition = "TEXT")
    private String platformNote;
    
    @Column(name = "feeder_note", columnDefinition = "TEXT")
    private String feederNote;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cat_id", insertable = false, updatable = false)
    private StrayCat strayCat;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", insertable = false, updatable = false)
    private User applicant;
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
