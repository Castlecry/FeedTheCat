package com.example.catguardian.repository;

import com.example.catguardian.entity.UserPoints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户积分余额数据访问接口
 */
@Repository
public interface UserPointsRepository extends JpaRepository<UserPoints, Long> {
    
    Optional<UserPoints> findByUserId(Long userId);
    
    boolean existsByUserId(Long userId);
}
