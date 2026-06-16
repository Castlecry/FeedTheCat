package com.example.catguardian.service.impl;

import com.example.catguardian.dto.request.CreateCatProfileRequest;
import com.example.catguardian.dto.response.CatProfileResponse;
import com.example.catguardian.entity.CatProfile;
import com.example.catguardian.exception.BusinessException;
import com.example.catguardian.repository.CatProfileRepository;
import com.example.catguardian.repository.UserRepository;
import com.example.catguardian.service.CatProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CatProfileServiceImpl implements CatProfileService {
    
    private final CatProfileRepository catProfileRepository;
    private final UserRepository userRepository;
    
    @Override
    @Transactional
    public CatProfileResponse createProfile(Long userId, CreateCatProfileRequest request) {
        userRepository.findById(userId)
                .orElseThrow(() -> BusinessException.notFound("用户不存在"));
        
        CatProfile profile = CatProfile.builder()
                .userId(userId)
                .name(request.getName())
                .breed(request.getBreed())
                .age(request.getAge())
                .gender(request.getGender())
                .healthStatus(request.getHealthStatus())
                .dietaryHabits(request.getDietaryHabits())
                .taboos(request.getTaboos())
                .sterilized(request.getSterilized() != null ? request.getSterilized() : 0)
                .vaccinated(request.getVaccinated() != null ? request.getVaccinated() : 0)
                .nextVaccineDate(request.getNextVaccineDate())
                .lastDewormDate(request.getLastDewormDate())
                .nextDewormDate(request.getNextDewormDate())
                .insuranceInfo(request.getInsuranceInfo())
                .medicalRecords(request.getMedicalRecords())
                .avatar(request.getAvatar())
                .build();
        
        CatProfile saved = catProfileRepository.save(profile);
        log.info("猫咪档案创建成功: {}", saved.getId());
        return convertToResponse(saved);
    }
    
    @Override
    public CatProfileResponse getProfile(Long id) {
        CatProfile profile = catProfileRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("猫咪档案不存在"));
        return convertToResponse(profile);
    }
    
    @Override
    public List<CatProfileResponse> getProfilesByUser(Long userId) {
        return catProfileRepository.findByUserId(userId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public CatProfileResponse updateProfile(Long id, CreateCatProfileRequest request) {
        CatProfile profile = catProfileRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("猫咪档案不存在"));
        
        profile.setName(request.getName());
        profile.setBreed(request.getBreed());
        profile.setAge(request.getAge());
        profile.setGender(request.getGender());
        profile.setHealthStatus(request.getHealthStatus());
        profile.setDietaryHabits(request.getDietaryHabits());
        profile.setTaboos(request.getTaboos());
        if (request.getSterilized() != null) profile.setSterilized(request.getSterilized());
        if (request.getVaccinated() != null) profile.setVaccinated(request.getVaccinated());
        profile.setNextVaccineDate(request.getNextVaccineDate());
        profile.setLastDewormDate(request.getLastDewormDate());
        profile.setNextDewormDate(request.getNextDewormDate());
        profile.setInsuranceInfo(request.getInsuranceInfo());
        profile.setMedicalRecords(request.getMedicalRecords());
        profile.setAvatar(request.getAvatar());
        
        CatProfile saved = catProfileRepository.save(profile);
        return convertToResponse(saved);
    }
    
    @Override
    @Transactional
    public void deleteProfile(Long id) {
        CatProfile profile = catProfileRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("猫咪档案不存在"));
        catProfileRepository.delete(profile);
        log.info("猫咪档案删除成功: {}", id);
    }
    
    private CatProfileResponse convertToResponse(CatProfile profile) {
        LocalDate today = LocalDate.now();
        Boolean vaccineReminder = profile.getNextVaccineDate() != null && 
                !profile.getNextVaccineDate().isAfter(today.plusDays(7));
        Boolean dewormReminder = profile.getNextDewormDate() != null && 
                !profile.getNextDewormDate().isAfter(today.plusDays(7));
        
        return CatProfileResponse.builder()
                .id(profile.getId())
                .userId(profile.getUserId())
                .name(profile.getName())
                .breed(profile.getBreed())
                .age(profile.getAge())
                .gender(profile.getGender())
                .healthStatus(profile.getHealthStatus())
                .dietaryHabits(profile.getDietaryHabits())
                .taboos(profile.getTaboos())
                .sterilized(profile.getSterilized())
                .vaccinated(profile.getVaccinated())
                .nextVaccineDate(profile.getNextVaccineDate())
                .lastDewormDate(profile.getLastDewormDate())
                .nextDewormDate(profile.getNextDewormDate())
                .insuranceInfo(profile.getInsuranceInfo())
                .medicalRecords(profile.getMedicalRecords())
                .avatar(profile.getAvatar())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .vaccineReminder(vaccineReminder)
                .dewormReminder(dewormReminder)
                .build();
    }
}