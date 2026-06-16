package com.example.catguardian.repository;

import com.example.catguardian.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);
    void deleteByFollowerIdAndFollowingId(Long followerId, Long followingId);
    List<Follow> findByFollowerIdOrderByCreatedAtDesc(Long followerId);
    List<Follow> findByFollowingIdOrderByCreatedAtDesc(Long followingId);
}
