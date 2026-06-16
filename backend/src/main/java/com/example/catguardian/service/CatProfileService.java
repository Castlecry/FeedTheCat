package com.example.catguardian.service;

import com.example.catguardian.dto.request.CreateCatProfileRequest;
import com.example.catguardian.dto.response.CatProfileResponse;

import java.util.List;

public interface CatProfileService {
    
    CatProfileResponse createProfile(Long userId, CreateCatProfileRequest request);
    
    CatProfileResponse getProfile(Long id);
    
    List<CatProfileResponse> getProfilesByUser(Long userId);
    
    CatProfileResponse updateProfile(Long id, CreateCatProfileRequest request);
    
    void deleteProfile(Long id);
}