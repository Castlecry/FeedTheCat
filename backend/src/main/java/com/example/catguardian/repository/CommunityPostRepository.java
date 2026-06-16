package com.example.catguardian.repository;

import com.example.catguardian.entity.CommunityPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {
    List<CommunityPost> findByStatusOrderByCreatedAtDesc(Integer status);
    List<CommunityPost> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<CommunityPost> findByTypeAndStatusOrderByCreatedAtDesc(Integer type, Integer status);
}