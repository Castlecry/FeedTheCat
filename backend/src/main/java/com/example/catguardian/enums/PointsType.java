package com.example.catguardian.enums;

/**
 * 积分类型枚举
 */
public enum PointsType {
    /**
     * 获取积分
     */
    EARN(0, "获取"),
    
    /**
     * 消费积分
     */
    SPEND(1, "消费");
    
    private final int code;
    private final String description;
    
    PointsType(int code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static PointsType fromCode(int code) {
        for (PointsType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return EARN;
    }
}
