package com.example.catguardian.enums;

/**
 * 领养状态枚举
 */
public enum AdoptionStatus {
    /**
     * 待初审
     */
    PENDING_REVIEW(0, "待初审"),
    
    /**
     * 待复审
     */
    PENDING_FEEDER_REVIEW(1, "待复审"),
    
    /**
     * 已通过
     */
    APPROVED(2, "已通过"),
    
    /**
     * 已拒绝
     */
    REJECTED(3, "已拒绝");
    
    private final int code;
    private final String description;
    
    AdoptionStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static AdoptionStatus fromCode(int code) {
        for (AdoptionStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return PENDING_REVIEW;
    }
}
