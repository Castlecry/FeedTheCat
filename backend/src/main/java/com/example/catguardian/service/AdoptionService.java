package com.example.catguardian.service;

import com.example.catguardian.dto.request.CreateAdoptionRequest;
import com.example.catguardian.dto.request.CreateAdoptionTrackingRequest;
import com.example.catguardian.dto.request.CreateStrayCatRequest;
import com.example.catguardian.dto.request.StrayCatFilterRequest;
import com.example.catguardian.dto.response.AdoptionApplicationResponse;
import com.example.catguardian.dto.response.AdoptionTrackingResponse;
import com.example.catguardian.dto.response.StrayCatResponse;

import java.util.List;

/**
 * 领养服务接口
 */
public interface AdoptionService {
    
    /**
     * 发布流浪猫信息
     */
    StrayCatResponse publishStrayCat(Long userId, CreateStrayCatRequest request);
    
    /**
     * 获取流浪猫详情
     */
    StrayCatResponse getStrayCatById(Long id);
    
    /**
     * 获取待领养猫咪列表（支持筛选）
     */
    List<StrayCatResponse> getAvailableCats();
    
    /**
     * 筛选待领养猫咪
     */
    List<StrayCatResponse> filterCats(StrayCatFilterRequest request);
    
    /**
     * 获取用户发布的流浪猫列表
     */
    List<StrayCatResponse> getCatsByFeeder(Long feederId);
    
    /**
     * 审核流浪猫信息
     */
    StrayCatResponse reviewStrayCat(Long id, Integer status);
    
    /**
     * 提交领养申请
     */
    AdoptionApplicationResponse applyAdoption(Long userId, CreateAdoptionRequest request);
    
    /**
     * 获取领养申请详情
     */
    AdoptionApplicationResponse getAdoptionApplicationById(Long id);
    
    /**
     * 获取用户领养申请列表
     */
    List<AdoptionApplicationResponse> getApplicationsByApplicant(Long applicantId);
    
    /**
     * 获取猫咪的领养申请列表
     */
    List<AdoptionApplicationResponse> getApplicationsByCat(Long catId);
    
    /**
     * 平台初审领养申请
     */
    AdoptionApplicationResponse reviewApplication(Long id, String note);
    
    /**
     * 送养人复审领养申请
     */
    AdoptionApplicationResponse feederReview(Long id, Integer status, String note, Long feederId);
    
    /**
     * 添加领养跟踪记录
     */
    AdoptionTrackingResponse addTrackingRecord(Long applicationId, Long userId, CreateAdoptionTrackingRequest request);
    
    /**
     * 获取领养跟踪记录列表
     */
    List<AdoptionTrackingResponse> getTrackingRecords(Long applicationId);
    
    /**
     * 检查用户是否在领养黑名单中
     */
    boolean isInBlacklist(Long userId);
    
    /**
     * 将用户加入领养黑名单
     */
    void addToBlacklist(Long userId, String reason);
    
    /**
     * 从领养黑名单中移除用户
     */
    void removeFromBlacklist(Long userId);
    
    /**
     * 查询黑名单记录
     */
    List<Object[]> getBlacklist();
}