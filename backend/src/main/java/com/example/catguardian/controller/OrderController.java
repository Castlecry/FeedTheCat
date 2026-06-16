package com.example.catguardian.controller;

import com.example.catguardian.dto.request.CreateOrderEvaluationRequest;
import com.example.catguardian.dto.request.CreateOrderRequest;
import com.example.catguardian.dto.request.CreateServiceRecordRequest;
import com.example.catguardian.dto.response.ApiResponse;
import com.example.catguardian.dto.response.OrderEvaluationResponse;
import com.example.catguardian.dto.response.OrderResponse;
import com.example.catguardian.dto.response.ServiceRecordResponse;
import com.example.catguardian.entity.User;
import com.example.catguardian.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单控制器
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            Authentication authentication,
            @Valid @RequestBody CreateOrderRequest request) {
        User user = (User) authentication.getPrincipal();
        OrderResponse response = orderService.createOrder(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("订单创建成功", response));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable Long id) {
        OrderResponse response = orderService.getOrderById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/order-no/{orderNo}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderByNo(@PathVariable String orderNo) {
        OrderResponse response = orderService.getOrderByNo(orderNo);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/client")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getClientOrders(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<OrderResponse> orders = orderService.getOrdersByClient(user.getId());
        return ResponseEntity.ok(ApiResponse.success(orders));
    }
    
    @GetMapping("/provider")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getProviderOrders(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<OrderResponse> orders = orderService.getOrdersByServiceProvider(user.getId());
        return ResponseEntity.ok(ApiResponse.success(orders));
    }
    
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getPendingOrders() {
        List<OrderResponse> orders = orderService.getPendingOrders();
        return ResponseEntity.ok(ApiResponse.success(orders));
    }
    
    @PostMapping("/{id}/accept")
    public ResponseEntity<ApiResponse<OrderResponse>> acceptOrder(
            @PathVariable Long id,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        OrderResponse response = orderService.acceptOrder(id, user.getId());
        return ResponseEntity.ok(ApiResponse.success("接单成功", response));
    }
    
    @PostMapping("/{id}/start")
    public ResponseEntity<ApiResponse<OrderResponse>> startService(
            @PathVariable Long id,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        OrderResponse response = orderService.startService(id, user.getId());
        return ResponseEntity.ok(ApiResponse.success("服务已开始", response));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @PathVariable Long id,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        OrderResponse response = orderService.cancelOrder(id, user.getId());
        return ResponseEntity.ok(ApiResponse.success("订单已取消", response));
    }
    
    @PostMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<OrderResponse>> completeOrder(
            @PathVariable Long id,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        OrderResponse response = orderService.completeOrder(id, user.getId());
        return ResponseEntity.ok(ApiResponse.success("服务已完成", response));
    }
    
    @GetMapping("/{id}/records")
    public ResponseEntity<ApiResponse<List<ServiceRecordResponse>>> getServiceRecords(@PathVariable Long id) {
        List<ServiceRecordResponse> records = orderService.getServiceRecords(id);
        return ResponseEntity.ok(ApiResponse.success(records));
    }
    
    @PostMapping("/{id}/records")
    public ResponseEntity<ApiResponse<ServiceRecordResponse>> createServiceRecord(
            @PathVariable Long id,
            Authentication authentication,
            @RequestBody CreateServiceRecordRequest request) {
        User user = (User) authentication.getPrincipal();
        ServiceRecordResponse response = orderService.createServiceRecord(id, user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("服务记录创建成功", response));
    }
    
    @PostMapping("/{id}/evaluate")
    public ResponseEntity<ApiResponse<OrderEvaluationResponse>> evaluateOrder(
            @PathVariable Long id,
            Authentication authentication,
            @Valid @RequestBody CreateOrderEvaluationRequest request) {
        User user = (User) authentication.getPrincipal();
        OrderEvaluationResponse response = orderService.evaluateOrder(id, user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("评价成功", response));
    }
}
