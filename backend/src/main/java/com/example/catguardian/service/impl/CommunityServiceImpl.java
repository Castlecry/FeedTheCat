package com.example.catguardian.service.impl;

import com.example.catguardian.dto.request.CreateCommentRequest;
import com.example.catguardian.dto.request.CreateCommunityPostRequest;
import com.example.catguardian.dto.request.CreateCommunityTaskRequest;
import com.example.catguardian.dto.response.CommentResponse;
import com.example.catguardian.dto.response.CommunityPostResponse;
import com.example.catguardian.dto.response.CommunityTaskResponse;
import com.example.catguardian.entity.*;
import com.example.catguardian.exception.BusinessException;
import com.example.catguardian.repository.*;
import com.example.catguardian.service.CommunityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommunityServiceImpl implements CommunityService {
    
    private final CommunityPostRepository communityPostRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final CommunityTaskRepository communityTaskRepository;
    private final UserRepository userRepository;
    
    @Override
    @Transactional
    public CommunityPostResponse createPost(Long userId, CreateCommunityPostRequest request) {
        CommunityPost post = CommunityPost.builder()
                .userId(userId)
                .title(request.getTitle())
                .content(request.getContent())
                .images(request.getImages())
                .type(request.getType() != null ? request.getType() : 0)
                .status(1)
                .build();
        
        CommunityPost saved = communityPostRepository.save(post);
        log.info("社区帖子创建成功: {}", saved.getId());
        return convertToResponse(saved, userId);
    }
    
    @Override
    public CommunityPostResponse getPost(Long id, Long userId) {
        CommunityPost post = communityPostRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("帖子不存在"));
        
        post.setViewCount(post.getViewCount() + 1);
        communityPostRepository.save(post);
        
        return convertToResponse(post, userId);
    }
    
    @Override
    public List<CommunityPostResponse> getPosts(Integer type, Long userId) {
        List<CommunityPost> posts;
        if (type != null) {
            posts = communityPostRepository.findByTypeAndStatusOrderByCreatedAtDesc(type, 1);
        } else {
            posts = communityPostRepository.findByStatusOrderByCreatedAtDesc(1);
        }
        return posts.stream()
                .map(post -> convertToResponse(post, userId))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<CommunityPostResponse> getMyPosts(Long userId) {
        return communityPostRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(post -> convertToResponse(post, userId))
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public CommunityPostResponse updatePost(Long id, CreateCommunityPostRequest request) {
        CommunityPost post = communityPostRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("帖子不存在"));
        
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setImages(request.getImages());
        if (request.getType() != null) post.setType(request.getType());
        
        CommunityPost saved = communityPostRepository.save(post);
        return convertToResponse(saved, saved.getUserId());
    }
    
    @Override
    @Transactional
    public void deletePost(Long id) {
        CommunityPost post = communityPostRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("帖子不存在"));
        post.setStatus(2);
        communityPostRepository.save(post);
        log.info("社区帖子删除成功: {}", id);
    }
    
    @Override
    @Transactional
    public CommentResponse createComment(Long postId, Long userId, CreateCommentRequest request) {
        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(() -> BusinessException.notFound("帖子不存在"));
        
        Comment comment = Comment.builder()
                .postId(postId)
                .userId(userId)
                .content(request.getContent())
                .parentId(request.getParentId())
                .build();
        
        Comment saved = commentRepository.save(comment);
        
        post.setCommentCount(post.getCommentCount() + 1);
        communityPostRepository.save(post);
        
        return convertToCommentResponse(saved);
    }
    
    @Override
    public List<CommentResponse> getComments(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId).stream()
                .map(this::convertToCommentResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void likePost(Long postId, Long userId) {
        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(() -> BusinessException.notFound("帖子不存在"));
        
        Optional<Like> existingLike = likeRepository.findByPostIdAndUserId(postId, userId);
        
        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
        } else {
            Like like = Like.builder()
                    .postId(postId)
                    .userId(userId)
                    .build();
            likeRepository.save(like);
            post.setLikeCount(post.getLikeCount() + 1);
        }
        
        communityPostRepository.save(post);
    }
    
    @Override
    @Transactional
    public CommunityTaskResponse createTask(Long userId, CreateCommunityTaskRequest request) {
        CommunityTask task = CommunityTask.builder()
                .userId(userId)
                .title(request.getTitle())
                .content(request.getContent())
                .type(request.getType() != null ? request.getType() : 0)
                .location(request.getLocation())
                .rewardPoints(request.getRewardPoints() != null ? request.getRewardPoints() : 0)
                .status(0)
                .build();
        
        CommunityTask saved = communityTaskRepository.save(task);
        log.info("社区任务创建成功: {}", saved.getId());
        
        return convertToTaskResponse(saved);
    }
    
    @Override
    public List<CommunityTaskResponse> getTasks() {
        return communityTaskRepository.findByStatusOrderByCreatedAtDesc(0).stream()
                .map(this::convertToTaskResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void claimTask(Long taskId, Long userId) {
        CommunityTask task = communityTaskRepository.findById(taskId)
                .orElseThrow(() -> BusinessException.notFound("任务不存在"));
        
        if (task.getStatus() != 0) {
            throw BusinessException.badRequest("任务已被认领或已完成");
        }
        
        task.setStatus(1);
        task.setAssigneeId(userId);
        communityTaskRepository.save(task);
        log.info("任务 {} 被用户 {} 认领", taskId, userId);
    }
    
    private CommunityPostResponse convertToResponse(CommunityPost post, Long currentUserId) {
        User user = userRepository.findById(post.getUserId()).orElse(null);
        boolean isLiked = likeRepository.findByPostIdAndUserId(post.getId(), currentUserId).isPresent();
        
        return CommunityPostResponse.builder()
                .id(post.getId())
                .userId(post.getUserId())
                .userName(user != null ? user.getName() : "")
                .userAvatar(user != null ? user.getAvatar() : "")
                .title(post.getTitle())
                .content(post.getContent())
                .images(post.getImages())
                .type(post.getType())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .status(post.getStatus())
                .isLiked(isLiked)
                .createdAt(post.getCreatedAt())
                .build();
    }
    
    private CommentResponse convertToCommentResponse(Comment comment) {
        User user = userRepository.findById(comment.getUserId()).orElse(null);
        
        return CommentResponse.builder()
                .id(comment.getId())
                .postId(comment.getPostId())
                .userId(comment.getUserId())
                .userName(user != null ? user.getName() : "")
                .userAvatar(user != null ? user.getAvatar() : "")
                .content(comment.getContent())
                .parentId(comment.getParentId())
                .createdAt(comment.getCreatedAt())
                .build();
    }
    
    private CommunityTaskResponse convertToTaskResponse(CommunityTask task) {
        User user = userRepository.findById(task.getUserId()).orElse(null);
        User assignee = task.getAssigneeId() != null ? userRepository.findById(task.getAssigneeId()).orElse(null) : null;
        
        String typeDesc = switch (task.getType()) {
            case 0 -> "求助";
            case 1 -> "寻猫启事";
            case 2 -> "领养求助";
            case 3 -> "物资捐赠";
            default -> "其他";
        };
        
        String statusDesc = switch (task.getStatus()) {
            case 0 -> "待认领";
            case 1 -> "进行中";
            case 2 -> "已完成";
            default -> "未知";
        };
        
        return CommunityTaskResponse.builder()
                .id(task.getId())
                .userId(task.getUserId())
                .userName(user != null ? user.getName() : "")
                .userAvatar(user != null ? user.getAvatar() : "")
                .title(task.getTitle())
                .content(task.getContent())
                .type(task.getType())
                .typeDescription(typeDesc)
                .location(task.getLocation())
                .rewardPoints(task.getRewardPoints())
                .status(task.getStatus())
                .statusDescription(statusDesc)
                .assigneeId(task.getAssigneeId())
                .assigneeName(assignee != null ? assignee.getName() : "")
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}