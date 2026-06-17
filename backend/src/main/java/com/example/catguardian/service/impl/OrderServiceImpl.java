package com.example.catguardian.service.impl;

import com.example.catguardian.dto.request.CreateOrderEvaluationRequest;
import com.example.catguardian.dto.request.CreateOrderRequest;
import com.example.catguardian.dto.request.CreateServiceRecordRequest;
import com.example.catguardian.dto.response.OrderEvaluationResponse;
import com.example.catguardian.dto.response.OrderResponse;
import com.example.catguardian.dto.response.ServiceRecordResponse;
import com.example.catguardian.entity.Order;
import com.example.catguardian.entity.OrderEvaluation;
import com.example.catguardian.entity.ServiceRecord;
import com.example.catguardian.entity.User;
import com.example.catguardian.enums.OrderStatus;
import com.example.catguardian.enums.UserRole;
import com.example.catguardian.exception.BusinessException;
import com.example.catguardian.repository.OrderEvaluationRepository;
import com.example.catguardian.repository.OrderRepository;
import com.example.catguardian.repository.ServiceRecordRepository;
import com.example.catguardian.repository.UserRepository;
import com.example.catguardian.service.OrderService;
import com.example.catguardian.service.PointsService;
import com.example.catguardian.utils.OrderNoGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final PointsService pointsService;
    private final ServiceRecordRepository serviceRecordRepository;
    private final OrderEvaluationRepository orderEvaluationRepository;
    private final OrderNoGenerator orderNoGenerator;
    
    @Override
    @Transactional
    public OrderResponse createOrder(Long userId, CreateOrderRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> BusinessException.notFound("用户不存在"));
        
        String orderNo;
        do {
            orderNo = orderNoGenerator.generateOrderNo();
        } while (orderRepository.existsByOrderNo(orderNo));
        
        Order order = Order.builder()
                .orderNo(orderNo)
                .clientId(userId)
                .status(OrderStatus.PENDING.getCode())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .visitFrequency(request.getVisitFrequency())
                .feedingRequirements(request.getFeedingRequirements())
                .litterCleanStandard(request.getLitterCleanStandard())
                .specialCare(request.getSpecialCare())
                .entryMethod(request.getEntryMethod())
                .keyStorageInfo(request.getKeyStorageInfo())
                .emergencyContact(request.getEmergencyContact())
                .address(request.getAddress())
                .totalAmount(request.getTotalAmount())
                .commissionRate(new BigDecimal("0.1"))
                .build();
        
        Order savedOrder = orderRepository.save(order);
        log.info("订单创建成功: {}", savedOrder.getId());
        
        return convertToResponse(savedOrder);
    }
    
    @Override
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("订单不存在"));
        return convertToResponse(order);
    }
    
    @Override
    public OrderResponse getOrderByNo(String orderNo) {
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> BusinessException.notFound("订单不存在"));
        return convertToResponse(order);
    }
    
    @Override
    public List<OrderResponse> getOrdersByClient(Long clientId) {
        return orderRepository.findByClientId(clientId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<OrderResponse> getOrdersByServiceProvider(Long serviceId) {
        return orderRepository.findByServiceId(serviceId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<OrderResponse> getPendingOrders() {
        return orderRepository.findByStatus(OrderStatus.PENDING.getCode()).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public OrderResponse acceptOrder(Long orderId, Long serviceId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> BusinessException.notFound("订单不存在"));
        
        if (order.getStatus() != OrderStatus.PENDING.getCode()) {
            throw BusinessException.badRequest("订单状态不允许接单");
        }
        
        User serviceProvider = userRepository.findById(serviceId)
                .orElseThrow(() -> BusinessException.notFound("服务方不存在"));
        
        if (serviceProvider.getRole() != UserRole.SERVICE_PROVIDER.getCode()) {
            throw BusinessException.forbidden("非服务方用户无法接单");
        }
        
        order.setServiceId(serviceId);
        order.setStatus(OrderStatus.ACCEPTED.getCode());
        
        Order savedOrder = orderRepository.save(order);
        log.info("订单接单成功: {}", savedOrder.getId());
        
        return convertToResponse(savedOrder);
    }
    
    @Override
    @Transactional
    public OrderResponse cancelOrder(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> BusinessException.notFound("订单不存在"));
        
        if (!order.getClientId().equals(userId)) {
            throw BusinessException.forbidden("无权取消此订单");
        }
        
        if (order.getStatus() == OrderStatus.COMPLETED.getCode()) {
            throw BusinessException.badRequest("已完成的订单无法取消");
        }
        
        // 根据订单状态和时间计算扣款比例
        BigDecimal deductionRate = BigDecimal.ZERO;
        String deductionReason = "";
        
        if (order.getStatus() == OrderStatus.PENDING.getCode()) {
            // 待接单状态：全额退款
            deductionRate = BigDecimal.ZERO;
            deductionReason = "待接单取消，全额退款";
        } else if (order.getStatus() == OrderStatus.ACCEPTED.getCode()) {
            // 已接单状态：扣除5%
            deductionRate = new BigDecimal("0.05");
            deductionReason = "已接单取消，扣除5%费用";
        } else if (order.getStatus() == OrderStatus.IN_PROGRESS.getCode()) {
            // 服务中状态：检查是否是服务当天
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime serviceStartTime = order.getStartTime();
            
            if (now.toLocalDate().equals(serviceStartTime.toLocalDate())) {
                // 服务当天取消：扣除10%
                deductionRate = new BigDecimal("0.10");
                deductionReason = "服务当天取消，扣除10%费用";
            } else {
                // 非服务当天但已开始服务：扣除5%
                deductionRate = new BigDecimal("0.05");
                deductionReason = "服务中取消，扣除5%费用";
            }
        }
        
        // 计算扣款金额（转换为积分，1元=10积分）
        if (deductionRate.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal deductionAmount = order.getTotalAmount()
                    .multiply(deductionRate)
                    .multiply(new BigDecimal("10")) // 转换为积分
                    .setScale(0, RoundingMode.HALF_UP);
            
            pointsService.deductPoints(userId, deductionAmount.intValue(), 
                    "order_cancel_deduction", orderId);
            
            log.info("订单取消扣款: 用户={}, 订单={}, 扣款金额={}积分, 原因={}", 
                    userId, orderId, deductionAmount.intValue(), deductionReason);
        }
        
        // 计算退款金额
        BigDecimal refundAmount = order.getTotalAmount()
                .multiply(BigDecimal.ONE.subtract(deductionRate));
        
        order.setStatus(OrderStatus.CANCELLED.getCode());
        order.setActualPayment(order.getTotalAmount());
        order.setRefundAmount(refundAmount);
        
        Order savedOrder = orderRepository.save(order);
        log.info("订单取消成功: {}", savedOrder.getId());
        
        return convertToResponse(savedOrder);
    }
    
    @Override
    @Transactional
    public OrderResponse startService(Long orderId, Long serviceId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> BusinessException.notFound("订单不存在"));
        
        if (order.getStatus() != OrderStatus.ACCEPTED.getCode()) {
            throw BusinessException.badRequest("订单状态不允许开始服务");
        }
        
        if (!order.getServiceId().equals(serviceId)) {
            throw BusinessException.forbidden("无权操作此订单");
        }
        
        order.setStatus(OrderStatus.IN_PROGRESS.getCode());
        
        Order savedOrder = orderRepository.save(order);
        log.info("服务开始: {}", savedOrder.getId());
        
        return convertToResponse(savedOrder);
    }
    
    @Override
    @Transactional
    public OrderResponse completeOrder(Long orderId, Long serviceId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> BusinessException.notFound("订单不存在"));
        
        if (order.getStatus() != OrderStatus.IN_PROGRESS.getCode()) {
            throw BusinessException.badRequest("订单状态不允许完成");
        }
        
        if (!order.getServiceId().equals(serviceId)) {
            throw BusinessException.forbidden("无权操作此订单");
        }
        
        order.setStatus(OrderStatus.COMPLETED.getCode());
        
        Order savedOrder = orderRepository.save(order);
        log.info("订单完成: {}", savedOrder.getId());
        
        return convertToResponse(savedOrder);
    }
    
    @Override
    @Transactional
    public OrderResponse updateOrder(Long id, Order order) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("订单不存在"));
        
        if (order.getStatus() != null) {
            existingOrder.setStatus(order.getStatus());
        }
        if (order.getServiceId() != null) {
            existingOrder.setServiceId(order.getServiceId());
        }
        
        Order savedOrder = orderRepository.save(existingOrder);
        return convertToResponse(savedOrder);
    }
    
    private OrderResponse convertToResponse(Order order) {
        String clientName = userRepository.findById(order.getClientId())
                .map(User::getName)
                .orElse("");
        
        String serviceName = order.getServiceId() != null ?
                userRepository.findById(order.getServiceId())
                        .map(User::getName)
                        .orElse("") : "";
        
        return OrderResponse.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .clientId(order.getClientId())
                .clientName(clientName)
                .serviceId(order.getServiceId())
                .serviceName(serviceName)
                .status(order.getStatus())
                .statusDescription(OrderStatus.fromCode(order.getStatus()).getDescription())
                .startTime(order.getStartTime())
                .endTime(order.getEndTime())
                .visitFrequency(order.getVisitFrequency())
                .feedingRequirements(order.getFeedingRequirements())
                .litterCleanStandard(order.getLitterCleanStandard())
                .specialCare(order.getSpecialCare())
                .entryMethod(order.getEntryMethod())
                .keyStorageInfo(order.getKeyStorageInfo())
                .emergencyContact(order.getEmergencyContact())
                .address(order.getAddress())
                .totalAmount(order.getTotalAmount())
                .actualPayment(order.getActualPayment())
                .refundAmount(order.getRefundAmount())
                .commissionRate(order.getCommissionRate())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
    
    @Override
    public List<ServiceRecordResponse> getServiceRecords(Long orderId) {
        return serviceRecordRepository.findByOrderIdOrderByServiceTimeDesc(orderId).stream()
                .map(this::convertToRecordResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public ServiceRecordResponse createServiceRecord(Long orderId, Long serviceId, CreateServiceRecordRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> BusinessException.notFound("订单不存在"));
        
        if (!serviceId.equals(order.getServiceId())) {
            throw BusinessException.forbidden("您不是此订单的服务方");
        }
        
        ServiceRecord record = ServiceRecord.builder()
                .orderId(orderId)
                .serviceTime(request.getServiceTime())
                .videoUrl(request.getVideoUrl())
                .lockPhotoUrl(request.getLockPhotoUrl())
                .notes(request.getNotes())
                .build();
        
        ServiceRecord saved = serviceRecordRepository.save(record);
        log.info("服务记录创建成功: {}", saved.getId());
        return convertToRecordResponse(saved);
    }
    
    @Override
    @Transactional
    public OrderEvaluationResponse evaluateOrder(Long orderId, Long clientId, CreateOrderEvaluationRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> BusinessException.notFound("订单不存在"));
        
        if (!clientId.equals(order.getClientId())) {
            throw BusinessException.forbidden("您不是此订单的委托方");
        }
        
        if (OrderStatus.COMPLETED.getCode() != order.getStatus()) {
            throw BusinessException.badRequest("订单未完成，无法评价");
        }
        
        if (orderEvaluationRepository.existsByOrderId(orderId)) {
            throw BusinessException.badRequest("该订单已评价");
        }
        
        OrderEvaluation evaluation = OrderEvaluation.builder()
                .orderId(orderId)
                .clientId(clientId)
                .serviceId(order.getServiceId())
                .rating(request.getRating())
                .comment(request.getComment())
                .build();
        
        OrderEvaluation saved = orderEvaluationRepository.save(evaluation);
        log.info("订单评价创建成功: {}", saved.getId());
        
        return OrderEvaluationResponse.builder()
                .id(saved.getId())
                .orderId(saved.getOrderId())
                .rating(saved.getRating())
                .comment(saved.getComment())
                .createdAt(saved.getCreatedAt())
                .build();
    }
    
    private ServiceRecordResponse convertToRecordResponse(ServiceRecord record) {
        return ServiceRecordResponse.builder()
                .id(record.getId())
                .orderId(record.getOrderId())
                .serviceTime(record.getServiceTime())
                .videoUrl(record.getVideoUrl())
                .lockPhotoUrl(record.getLockPhotoUrl())
                .notes(record.getNotes())
                .createdAt(record.getCreatedAt())
                .build();
    }
}
