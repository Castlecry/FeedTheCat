package com.example.catguardian.repository;

import com.example.catguardian.entity.TnrApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TnrApplicationRepository extends JpaRepository<TnrApplication, Long> {
    List<TnrApplication> findByStatusOrderByCreatedAtDesc(Integer status);
    List<TnrApplication> findByUserIdOrderByCreatedAtDesc(Long userId);
}