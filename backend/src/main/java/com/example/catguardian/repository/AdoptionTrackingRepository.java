package com.example.catguardian.repository;

import com.example.catguardian.entity.AdoptionTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdoptionTrackingRepository extends JpaRepository<AdoptionTracking, Long> {
    List<AdoptionTracking> findByApplicationIdOrderByTrackingTimeDesc(Long applicationId);
}