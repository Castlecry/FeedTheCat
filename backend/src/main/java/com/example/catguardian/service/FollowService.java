package com.example.catguardian.service;

import com.example.catguardian.entity.Follow;
import com.example.catguardian.entity.User;

import java.util.List;

public interface FollowService {
    
    void follow(Long followerId, Long followingId);
    
    void unfollow(Long followerId, Long followingId);
    
    boolean isFollowing(Long followerId, Long followingId);
    
    List<User> getFollowing(Long userId);
    
    List<User> getFollowers(Long userId);
}
