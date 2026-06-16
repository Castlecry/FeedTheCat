package com.example.catguardian.service;

import com.example.catguardian.dto.request.CreateTnrApplicationRequest;
import com.example.catguardian.dto.response.TnrApplicationResponse;

import java.util.List;

public interface TnrService {
    
    TnrApplicationResponse createApplication(Long userId, CreateTnrApplicationRequest request);
    
    List<TnrApplicationResponse> getApplications();
    
    TnrApplicationResponse getApplication(Long id);
    
    void approveApplication(Long id);
    
    void rejectApplication(Long id);
    
    void rejectApplication(Long id, String reason);
    
    TnrApplicationResponse uploadOperationPhotos(Long id, String photos);
}