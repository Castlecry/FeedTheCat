package com.example.catguardian.repository;

import com.example.catguardian.entity.AdoptionBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdoptionBlacklistRepository extends JpaRepository<AdoptionBlacklist, Long> {
    Optional<AdoptionBlacklist> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
    void deleteByUserId(Long userId);
    
    @Query("SELECT ab, u.name, u.phone FROM AdoptionBlacklist ab JOIN User u ON ab.userId = u.id")
    List<Object[]> findAllWithUserInfo();
}