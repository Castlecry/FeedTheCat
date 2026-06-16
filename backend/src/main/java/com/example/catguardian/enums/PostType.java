package com.example.catguardian.enums;

/**
 * 帖子类型枚举
 */
public enum PostType {
    /**
     * 日常分享
     */
    DAILY(0, "日常分享"),
    
    /**
     * 求助
     */
    HELP(1, "求助"),
    
    /**
     * 闲置转让
     */
    SECOND_HAND(2, "闲置转让");
    
    private final int code;
    private final String description;
    
    PostType(int code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static PostType fromCode(int code) {
        for (PostType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return DAILY;
    }
}
