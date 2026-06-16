package com.example.catguardian.service;

import com.example.catguardian.dto.request.CreateOrderEvaluationRequest;
import com.example.catguardian.dto.request.CreateOrderRequest;
import com.example.catguardian.dto.request.CreateServiceRecordRequest;
import com.example.catguardian.dto.response.OrderEvaluationResponse;
import com.example.catguardian.dto.response.OrderResponse;
import com.example.catguardian.dto.response.ServiceRecordResponse;
import com.example.catguardian.entity.Order;

import java.util.List;

/**
 * 订单服务接口
 */
public interface OrderService {
    
    /**
     * 创建订单
     */
    OrderResponse createOrder(Long userId, CreateOrderRequest request);
    
    /**
     * 获取订单详情
     */
    OrderResponse getOrderById(Long id);
    
    /**
     * 获取订单详情（根据订单编号）
     */
    OrderResponse getOrderByNo(String orderNo);
    
    /**
     * 获取用户订单列表
     */
    List<OrderResponse> getOrdersByClient(Long clientId);
    
    /**
     * 获取服务方订单列表
     */
    List<OrderResponse> getOrdersByServiceProvider(Long serviceId);
    
    /**
     * 获取待接单订单列表
     */
    List<OrderResponse> getPendingOrders();
    
    /**
     * 服务方接单
     */
    OrderResponse acceptOrder(Long orderId, Long serviceId);
    
    /**
     * 取消订单
     */
    OrderResponse cancelOrder(Long orderId, Long userId);
    
    /**
     * 开始服务
     */
    OrderResponse startService(Long orderId, Long serviceId);
    
    /**
     * 完成服务
     */
    OrderResponse completeOrder(Long orderId, Long serviceId);
    
    /**
     * 更新订单
     */
    OrderResponse updateOrder(Long id, Order order);
    
    /**
     * 获取服务记录
     */
    List<ServiceRecordResponse> getServiceRecords(Long orderId);
    
    /**
     * 创建服务记录
     */
    ServiceRecordResponse createServiceRecord(Long orderId, Long serviceId, CreateServiceRecordRequest request);
    
    /**
     * 评价订单
     */
    OrderEvaluationResponse evaluateOrder(Long orderId, Long clientId, CreateOrderEvaluationRequest request);
}
