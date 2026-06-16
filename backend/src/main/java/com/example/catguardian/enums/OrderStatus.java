package com.example.catguardian.enums;

/**
 * 订单状态枚举
 */
public enum OrderStatus {
    /**
     * 待接单
     */
    PENDING(0, "待接单"),
    
    /**
     * 已接单
     */
    ACCEPTED(1, "已接单"),
    
    /**
     * 服务中
     */
    IN_PROGRESS(2, "服务中"),
    
    /**
     * 已完成
     */
    COMPLETED(3, "已完成"),
    
    /**
     * 已取消
     */
    CANCELLED(4, "已取消");
    
    private final int code;
    private final String description;
    
    OrderStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static OrderStatus fromCode(int code) {
        for (OrderStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return PENDING;
    }
}
