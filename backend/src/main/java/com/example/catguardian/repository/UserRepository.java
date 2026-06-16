package com.example.catguardian.repository;

import com.example.catguardian.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户数据访问接口
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByPhone(String phone);
    
    boolean existsByPhone(String phone);
    
    boolean existsByIdCard(String idCard);
}
