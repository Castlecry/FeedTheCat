package com.example.catguardian.controller;

import com.example.catguardian.dto.response.ApiResponse;
import com.example.catguardian.entity.ExchangeItem;
import com.example.catguardian.entity.ExchangeRecord;
import com.example.catguardian.entity.User;
import com.example.catguardian.service.ExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exchange")
@RequiredArgsConstructor
public class ExchangeController {
    
    private final ExchangeService exchangeService;
    
    @GetMapping("/items")
    public ResponseEntity<ApiResponse<List<ExchangeItem>>> getExchangeItems(
            @RequestParam(required = false) String type) {
        List<ExchangeItem> items;
        if (type != null && !type.isEmpty()) {
            items = exchangeService.getExchangeItemsByType(type);
        } else {
            items = exchangeService.getExchangeItems();
        }
        return ResponseEntity.ok(ApiResponse.success(items));
    }
    
    @GetMapping("/items/{id}")
    public ResponseEntity<ApiResponse<ExchangeItem>> getExchangeItem(@PathVariable Long id) {
        ExchangeItem item = exchangeService.getExchangeItem(id);
        return ResponseEntity.ok(ApiResponse.success(item));
    }
    
    @PostMapping("/exchange")
    public ResponseEntity<ApiResponse<ExchangeRecord>> exchange(
            Authentication authentication,
            @RequestBody java.util.Map<String, Long> request) {
        User user = (User) authentication.getPrincipal();
        Long itemId = request.get("itemId");
        ExchangeRecord record = exchangeService.exchange(user.getId(), itemId);
        return ResponseEntity.ok(ApiResponse.success("兑换成功", record));
    }
    
    @GetMapping("/records")
    public ResponseEntity<ApiResponse<List<ExchangeRecord>>> getExchangeRecords(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<ExchangeRecord> records = exchangeService.getExchangeRecords(user.getId());
        return ResponseEntity.ok(ApiResponse.success(records));
    }
}
