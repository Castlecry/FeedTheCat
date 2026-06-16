package com.example.catguardian.repository;

import com.example.catguardian.entity.AdoptionApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 领养申请数据访问接口
 */
@Repository
public interface AdoptionApplicationRepository extends JpaRepository<AdoptionApplication, Long> {
    
    List<AdoptionApplication> findByApplicantId(Long applicantId);
    
    List<AdoptionApplication> findByCatId(Long catId);
    
    List<AdoptionApplication> findByStatus(Integer status);
    
    Page<AdoptionApplication> findByStatus(Integer status, Pageable pageable);
    
    List<AdoptionApplication> findByCatIdAndStatus(Long catId, Integer status);
}
