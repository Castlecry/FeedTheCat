package com.example.catguardian.enums;

/**
 * 用户角色枚举
 */
public enum UserRole {
    /**
     * 普通用户
     */
    NORMAL(0, "普通用户"),
    
    /**
     * 委托方（喵居托付官）
     */
    CLIENT(1, "委托方"),
    
    /**
     * 服务方（喵星守护使）
     */
    SERVICE_PROVIDER(2, "服务方"),
    
    /**
     * 送养人
     */
    FEEDER(3, "送养人"),
    
    /**
     * 领养人
     */
    ADOPTER(4, "领养人");
    
    private final int code;
    private final String description;
    
    UserRole(int code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static UserRole fromCode(int code) {
        for (UserRole role : values()) {
            if (role.code == code) {
                return role;
            }
        }
        return NORMAL;
    }
}
