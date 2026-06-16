package com.example.catguardian.service;

import com.example.catguardian.dto.request.CreateCommentRequest;
import com.example.catguardian.dto.request.CreateCommunityPostRequest;
import com.example.catguardian.dto.request.CreateCommunityTaskRequest;
import com.example.catguardian.dto.response.CommentResponse;
import com.example.catguardian.dto.response.CommunityPostResponse;
import com.example.catguardian.dto.response.CommunityTaskResponse;

import java.util.List;

public interface CommunityService {
    
    CommunityPostResponse createPost(Long userId, CreateCommunityPostRequest request);
    
    CommunityPostResponse getPost(Long id, Long userId);
    
    List<CommunityPostResponse> getPosts(Integer type, Long userId);
    
    List<CommunityPostResponse> getMyPosts(Long userId);
    
    CommunityPostResponse updatePost(Long id, CreateCommunityPostRequest request);
    
    void deletePost(Long id);
    
    CommentResponse createComment(Long postId, Long userId, CreateCommentRequest request);
    
    List<CommentResponse> getComments(Long postId);
    
    void likePost(Long postId, Long userId);
    
    CommunityTaskResponse createTask(Long userId, CreateCommunityTaskRequest request);
    
    List<CommunityTaskResponse> getTasks();
    
    void claimTask(Long taskId, Long userId);
}