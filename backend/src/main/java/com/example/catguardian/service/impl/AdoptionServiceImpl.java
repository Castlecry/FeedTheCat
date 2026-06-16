package com.example.catguardian.service.impl;

import com.example.catguardian.dto.request.CreateAdoptionRequest;
import com.example.catguardian.dto.request.CreateAdoptionTrackingRequest;
import com.example.catguardian.dto.request.CreateStrayCatRequest;
import com.example.catguardian.dto.request.StrayCatFilterRequest;
import com.example.catguardian.dto.response.AdoptionApplicationResponse;
import com.example.catguardian.dto.response.AdoptionTrackingResponse;
import com.example.catguardian.dto.response.StrayCatResponse;
import com.example.catguardian.entity.AdoptionApplication;
import com.example.catguardian.entity.AdoptionBlacklist;
import com.example.catguardian.entity.AdoptionTracking;
import com.example.catguardian.entity.StrayCat;
import com.example.catguardian.entity.User;
import com.example.catguardian.enums.AdoptionStatus;
import com.example.catguardian.exception.BusinessException;
import com.example.catguardian.repository.AdoptionApplicationRepository;
import com.example.catguardian.repository.AdoptionBlacklistRepository;
import com.example.catguardian.repository.AdoptionTrackingRepository;
import com.example.catguardian.repository.StrayCatRepository;
import com.example.catguardian.repository.UserRepository;
import com.example.catguardian.service.AdoptionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 领养服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdoptionServiceImpl implements AdoptionService {
    
    private final StrayCatRepository strayCatRepository;
    private final AdoptionApplicationRepository adoptionApplicationRepository;
    private final AdoptionTrackingRepository adoptionTrackingRepository;
    private final AdoptionBlacklistRepository adoptionBlacklistRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    
    @Override
    @Transactional
    public StrayCatResponse publishStrayCat(Long userId, CreateStrayCatRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> BusinessException.notFound("用户不存在"));
        
        String photosJson = null;
        if (request.getPhotos() != null && !request.getPhotos().isEmpty()) {
            try {
                photosJson = objectMapper.writeValueAsString(request.getPhotos());
            } catch (JsonProcessingException e) {
                throw BusinessException.badRequest("照片数据格式错误");
            }
        }
        
        StrayCat strayCat = StrayCat.builder()
                .feederId(userId)
                .name(request.getName())
                .breed(request.getBreed())
                .age(request.getAge())
                .gender(request.getGender())
                .healthStatus(request.getHealthStatus())
                .sterilized(request.getSterilized() != null ? request.getSterilized() : 0)
                .vaccinated(request.getVaccinated() != null ? request.getVaccinated() : 0)
                .location(request.getLocation())
                .description(request.getDescription())
                .photos(photosJson)
                .status(0)
                .build();
        
        StrayCat savedCat = strayCatRepository.save(strayCat);
        log.info("流浪猫信息发布成功: {}", savedCat.getId());
        
        return convertToStrayCatResponse(savedCat);
    }
    
    @Override
    public StrayCatResponse getStrayCatById(Long id) {
        StrayCat strayCat = strayCatRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("流浪猫不存在"));
        return convertToStrayCatResponse(strayCat);
    }
    
    @Override
    public List<StrayCatResponse> getAvailableCats() {
        return strayCatRepository.findByStatus(1).stream()
                .map(this::convertToStrayCatResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<StrayCatResponse> filterCats(StrayCatFilterRequest request) {
        return strayCatRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            predicates.add(criteriaBuilder.equal(root.get("status"), 1));
            
            if (request.getBreed() != null && !request.getBreed().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("breed"), "%" + request.getBreed() + "%"));
            }
            
            if (request.getAge() != null && !request.getAge().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("age"), "%" + request.getAge() + "%"));
            }
            
            if (request.getGender() != null) {
                predicates.add(criteriaBuilder.equal(root.get("gender"), request.getGender()));
            }
            
            if (request.getSterilized() != null) {
                predicates.add(criteriaBuilder.equal(root.get("sterilized"), request.getSterilized()));
            }
            
            if (request.getVaccinated() != null) {
                predicates.add(criteriaBuilder.equal(root.get("vaccinated"), request.getVaccinated()));
            }
            
            if (request.getLocation() != null && !request.getLocation().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("location"), "%" + request.getLocation() + "%"));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }).stream()
                .map(this::convertToStrayCatResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<StrayCatResponse> getCatsByFeeder(Long feederId) {
        return strayCatRepository.findByFeederId(feederId).stream()
                .map(this::convertToStrayCatResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public StrayCatResponse reviewStrayCat(Long id, Integer status) {
        StrayCat strayCat = strayCatRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("流浪猫不存在"));
        
        strayCat.setStatus(status);
        StrayCat savedCat = strayCatRepository.save(strayCat);
        
        log.info("流浪猫审核完成: {} -> {}", id, status);
        return convertToStrayCatResponse(savedCat);
    }
    
    @Override
    @Transactional
    public AdoptionApplicationResponse applyAdoption(Long userId, CreateAdoptionRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> BusinessException.notFound("用户不存在"));
        
        // 检查用户是否在领养黑名单中
        if (isInBlacklist(userId)) {
            throw BusinessException.forbidden("您已被列入领养黑名单，无法提交领养申请");
        }
        
        StrayCat strayCat = strayCatRepository.findById(request.getCatId())
                .orElseThrow(() -> BusinessException.notFound("流浪猫不存在"));
        
        if (strayCat.getStatus() != 1) {
            throw BusinessException.badRequest("该猫咪当前不可领养");
        }
        
        AdoptionApplication application = AdoptionApplication.builder()
                .catId(request.getCatId())
                .applicantId(userId)
                .livingAddress(request.getLivingAddress())
                .housingType(request.getHousingType())
                .familyAgree(request.getFamilyAgree())
                .petExperience(request.getPetExperience())
                .hasAbandoned(request.getHasAbandoned() != null ? request.getHasAbandoned() : 0)
                .status(AdoptionStatus.PENDING_REVIEW.getCode())
                .build();
        
        AdoptionApplication savedApplication = adoptionApplicationRepository.save(application);
        log.info("领养申请提交成功: {}", savedApplication.getId());
        
        return convertToAdoptionResponse(savedApplication);
    }
    
    @Override
    public AdoptionApplicationResponse getAdoptionApplicationById(Long id) {
        AdoptionApplication application = adoptionApplicationRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("领养申请不存在"));
        return convertToAdoptionResponse(application);
    }
    
    @Override
    public List<AdoptionApplicationResponse> getApplicationsByApplicant(Long applicantId) {
        return adoptionApplicationRepository.findByApplicantId(applicantId).stream()
                .map(this::convertToAdoptionResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<AdoptionApplicationResponse> getApplicationsByCat(Long catId) {
        return adoptionApplicationRepository.findByCatId(catId).stream()
                .map(this::convertToAdoptionResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public AdoptionApplicationResponse reviewApplication(Long id, String note) {
        AdoptionApplication application = adoptionApplicationRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("领养申请不存在"));
        
        application.setStatus(AdoptionStatus.PENDING_FEEDER_REVIEW.getCode());
        application.setPlatformNote(note);
        
        AdoptionApplication savedApplication = adoptionApplicationRepository.save(application);
        log.info("平台初审完成: {}", id);
        
        return convertToAdoptionResponse(savedApplication);
    }
    
    @Override
    @Transactional
    public AdoptionApplicationResponse feederReview(Long id, Integer status, String note, Long feederId) {
        AdoptionApplication application = adoptionApplicationRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("领养申请不存在"));
        
        StrayCat strayCat = strayCatRepository.findById(application.getCatId())
                .orElseThrow(() -> BusinessException.notFound("猫咪不存在"));
        
        if (!strayCat.getFeederId().equals(feederId)) {
            throw BusinessException.forbidden("无权审核此申请");
        }
        
        application.setStatus(status);
        application.setFeederNote(note);
        
        if (status == AdoptionStatus.APPROVED.getCode()) {
            strayCat.setStatus(2);
            strayCatRepository.save(strayCat);
        }
        
        AdoptionApplication savedApplication = adoptionApplicationRepository.save(application);
        log.info("送养人复审完成: {} -> {}", id, status);
        
        return convertToAdoptionResponse(savedApplication);
    }
    
    private StrayCatResponse convertToStrayCatResponse(StrayCat cat) {
        String feederName = userRepository.findById(cat.getFeederId())
                .map(User::getName)
                .orElse("");
        
        List<String> photos = null;
        if (cat.getPhotos() != null && !cat.getPhotos().isEmpty()) {
            try {
                photos = Arrays.asList(objectMapper.readValue(cat.getPhotos(), String[].class));
            } catch (JsonProcessingException e) {
                photos = List.of();
            }
        }
        
        String statusDesc = switch (cat.getStatus()) {
            case 0 -> "待审核";
            case 1 -> "待领养";
            case 2 -> "已领养";
            default -> "未知";
        };
        
        return StrayCatResponse.builder()
                .id(cat.getId())
                .feederId(cat.getFeederId())
                .feederName(feederName)
                .name(cat.getName())
                .breed(cat.getBreed())
                .age(cat.getAge())
                .gender(cat.getGender())
                .healthStatus(cat.getHealthStatus())
                .sterilized(cat.getSterilized())
                .vaccinated(cat.getVaccinated())
                .location(cat.getLocation())
                .description(cat.getDescription())
                .photos(photos)
                .status(cat.getStatus())
                .statusDescription(statusDesc)
                .createdAt(cat.getCreatedAt())
                .updatedAt(cat.getUpdatedAt())
                .build();
    }
    
    private AdoptionApplicationResponse convertToAdoptionResponse(AdoptionApplication application) {
        String catName = strayCatRepository.findById(application.getCatId())
                .map(StrayCat::getName)
                .orElse("");
        
        String applicantName = userRepository.findById(application.getApplicantId())
                .map(User::getName)
                .orElse("");
        
        String housingTypeDesc = application.getHousingType() == 0 ? "合租" : "独立住房";
        
        return AdoptionApplicationResponse.builder()
                .id(application.getId())
                .catId(application.getCatId())
                .catName(catName)
                .applicantId(application.getApplicantId())
                .applicantName(applicantName)
                .livingAddress(application.getLivingAddress())
                .housingType(application.getHousingType())
                .housingTypeDescription(housingTypeDesc)
                .familyAgree(application.getFamilyAgree())
                .petExperience(application.getPetExperience())
                .hasAbandoned(application.getHasAbandoned())
                .status(application.getStatus())
                .statusDescription(AdoptionStatus.fromCode(application.getStatus()).getDescription())
                .platformNote(application.getPlatformNote())
                .feederNote(application.getFeederNote())
                .createdAt(application.getCreatedAt())
                .updatedAt(application.getUpdatedAt())
                .build();
    }
    
    @Override
    @Transactional
    public AdoptionTrackingResponse addTrackingRecord(Long applicationId, Long userId, CreateAdoptionTrackingRequest request) {
        AdoptionApplication application = adoptionApplicationRepository.findById(applicationId)
                .orElseThrow(() -> BusinessException.notFound("领养申请不存在"));
        
        // 验证用户权限（申请人或送养人或管理员）
        if (!application.getApplicantId().equals(userId)) {
            StrayCat strayCat = strayCatRepository.findById(application.getCatId())
                    .orElseThrow(() -> BusinessException.notFound("猫咪不存在"));
            if (!strayCat.getFeederId().equals(userId)) {
                throw BusinessException.forbidden("无权添加跟踪记录");
            }
        }
        
        String photosJson = null;
        if (request.getPhotos() != null && !request.getPhotos().isEmpty()) {
            try {
                photosJson = objectMapper.writeValueAsString(request.getPhotos());
            } catch (JsonProcessingException e) {
                log.error("照片序列化失败", e);
            }
        }
        
        AdoptionTracking tracking = AdoptionTracking.builder()
                .applicationId(applicationId)
                .trackingTime(request.getTrackingTime() != null ? request.getTrackingTime() : LocalDateTime.now())
                .status(request.getStatus())
                .notes(request.getNotes())
                .photos(photosJson)
                .build();
        
        AdoptionTracking saved = adoptionTrackingRepository.save(tracking);
        log.info("领养跟踪记录添加成功: {}", saved.getId());
        
        return convertToTrackingResponse(saved);
    }
    
    @Override
    public List<AdoptionTrackingResponse> getTrackingRecords(Long applicationId) {
        return adoptionTrackingRepository.findByApplicationIdOrderByTrackingTimeDesc(applicationId).stream()
                .map(this::convertToTrackingResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean isInBlacklist(Long userId) {
        return adoptionBlacklistRepository.existsByUserId(userId);
    }
    
    @Override
    @Transactional
    public void addToBlacklist(Long userId, String reason) {
        if (isInBlacklist(userId)) {
            throw BusinessException.badRequest("用户已在黑名单中");
        }
        
        AdoptionBlacklist blacklist = AdoptionBlacklist.builder()
                .userId(userId)
                .reason(reason)
                .createdAt(LocalDateTime.now())
                .build();
        
        adoptionBlacklistRepository.save(blacklist);
        log.info("用户加入黑名单: {}", userId);
    }
    
    @Override
    @Transactional
    public void removeFromBlacklist(Long userId) {
        adoptionBlacklistRepository.deleteByUserId(userId);
        log.info("用户移除黑名单: {}", userId);
    }
    
    @Override
    public List<Object[]> getBlacklist() {
        return adoptionBlacklistRepository.findAllWithUserInfo();
    }
    
    private AdoptionTrackingResponse convertToTrackingResponse(AdoptionTracking tracking) {
        List<String> photos = null;
        if (tracking.getPhotos() != null && !tracking.getPhotos().isEmpty()) {
            try {
                photos = Arrays.asList(objectMapper.readValue(tracking.getPhotos(), String[].class));
            } catch (JsonProcessingException e) {
                photos = List.of();
            }
        }
        
        String statusDesc = switch (tracking.getStatus()) {
            case 0 -> "待回访";
            case 1 -> "正常";
            case 2 -> "异常";
            case 3 -> "已完成";
            default -> "未知";
        };
        
        return AdoptionTrackingResponse.builder()
                .id(tracking.getId())
                .applicationId(tracking.getApplicationId())
                .trackingTime(tracking.getTrackingTime())
                .status(tracking.getStatus())
                .statusDescription(statusDesc)
                .notes(tracking.getNotes())
                .photos(photos)
                .createdAt(tracking.getCreatedAt())
                .build();
    }
}
