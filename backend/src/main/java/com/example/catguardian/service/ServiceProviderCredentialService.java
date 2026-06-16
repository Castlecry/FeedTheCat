package com.example.catguardian.service;

import com.example.catguardian.dto.request.ServiceProviderApplyRequest;
import com.example.catguardian.entity.ServiceProviderCredential;

import java.util.List;

public interface ServiceProviderCredentialService {
    
    ServiceProviderCredential apply(Long userId, ServiceProviderApplyRequest request);
    
    ServiceProviderCredential getByUserId(Long userId);
    
    ServiceProviderCredential getById(Long id);
    
    List<ServiceProviderCredential> getPendingApplications();
    
    ServiceProviderCredential approve(Long id);
    
    ServiceProviderCredential reject(Long id, String reason);
}