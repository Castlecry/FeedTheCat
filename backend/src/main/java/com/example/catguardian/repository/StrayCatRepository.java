package com.example.catguardian.repository;

import com.example.catguardian.entity.StrayCat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 流浪猫信息数据访问接口
 */
@Repository
public interface StrayCatRepository extends JpaRepository<StrayCat, Long>, JpaSpecificationExecutor<StrayCat> {
    
    List<StrayCat> findByFeederId(Long feederId);
    
    List<StrayCat> findByStatus(Integer status);
    
    Page<StrayCat> findByStatus(Integer status, Pageable pageable);
}
