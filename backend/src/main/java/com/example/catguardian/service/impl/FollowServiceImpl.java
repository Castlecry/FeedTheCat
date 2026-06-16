package com.example.catguardian.service.impl;

import com.example.catguardian.entity.Follow;
import com.example.catguardian.entity.User;
import com.example.catguardian.exception.BusinessException;
import com.example.catguardian.repository.FollowRepository;
import com.example.catguardian.repository.UserRepository;
import com.example.catguardian.service.FollowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FollowServiceImpl implements FollowService {
    
    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    
    @Override
    @Transactional
    public void follow(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            throw BusinessException.badRequest("不能关注自己");
        }
        
        if (followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            throw BusinessException.badRequest("已关注该用户");
        }
        
        userRepository.findById(followingId)
                .orElseThrow(() -> BusinessException.notFound("用户不存在"));
        
        Follow follow = Follow.builder()
                .followerId(followerId)
                .followingId(followingId)
                .build();
        
        followRepository.save(follow);
        log.info("用户 {} 关注了用户 {}", followerId, followingId);
    }
    
    @Override
    @Transactional
    public void unfollow(Long followerId, Long followingId) {
        if (!followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            throw BusinessException.badRequest("未关注该用户");
        }
        
        followRepository.deleteByFollowerIdAndFollowingId(followerId, followingId);
        log.info("用户 {} 取消关注了用户 {}", followerId, followingId);
    }
    
    @Override
    public boolean isFollowing(Long followerId, Long followingId) {
        return followRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
    }
    
    @Override
    public List<User> getFollowing(Long userId) {
        return followRepository.findByFollowerIdOrderByCreatedAtDesc(userId).stream()
                .map(f -> userRepository.findById(f.getFollowingId()))
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<User> getFollowers(Long userId) {
        return followRepository.findByFollowingIdOrderByCreatedAtDesc(userId).stream()
                .map(f -> userRepository.findById(f.getFollowerId()))
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .collect(Collectors.toList());
    }
}
