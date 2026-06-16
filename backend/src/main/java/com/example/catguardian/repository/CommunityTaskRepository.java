package com.example.catguardian.repository;

import com.example.catguardian.entity.CommunityTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityTaskRepository extends JpaRepository<CommunityTask, Long> {
    List<CommunityTask> findByStatusOrderByCreatedAtDesc(Integer status);
    List<CommunityTask> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<CommunityTask> findByAssigneeIdOrderByCreatedAtDesc(Long assigneeId);
}