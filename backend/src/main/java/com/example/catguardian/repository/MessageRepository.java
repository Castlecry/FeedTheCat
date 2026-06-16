package com.example.catguardian.repository;

import com.example.catguardian.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderIdAndReceiverIdOrderByCreatedAtAsc(Long senderId, Long receiverId);
    List<Message> findByReceiverIdOrderByCreatedAtDesc(Long receiverId);
    
    @Modifying
    @Query("UPDATE Message m SET m.readStatus = 1 WHERE m.receiverId = :receiverId AND m.senderId = :senderId")
    void markAsRead(Long receiverId, Long senderId);
    
    @Query("SELECT m.senderId FROM Message m WHERE m.receiverId = :receiverId GROUP BY m.senderId ORDER BY MAX(m.createdAt) DESC")
    List<Long> findDistinctSenders(Long receiverId);
}
