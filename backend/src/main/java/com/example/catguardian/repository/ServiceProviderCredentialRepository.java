package com.example.catguardian.repository;

import com.example.catguardian.entity.ServiceProviderCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 服务方资质数据访问接口
 */
@Repository
public interface ServiceProviderCredentialRepository extends JpaRepository<ServiceProviderCredential, Long> {
    
    Optional<ServiceProviderCredential> findByUserId(Long userId);
    
    List<ServiceProviderCredential> findByStatus(Integer status);
    
    boolean existsByUserId(Long userId);
}