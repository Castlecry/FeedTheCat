package com.example.catguardian.repository;

import com.example.catguardian.entity.CatProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CatProfileRepository extends JpaRepository<CatProfile, Long> {
    List<CatProfile> findByUserId(Long userId);
}