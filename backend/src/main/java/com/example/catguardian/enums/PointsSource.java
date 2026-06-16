package com.example.catguardian.enums;

/**
 * 积分来源枚举
 */
public enum PointsSource {
    ORDER_PUBLISH("order_publish", "发布委托需求"),
    SERVICE_COMPLETE("service_complete", "完成服务"),
    ORDER_EVALUATE("order_evaluate", "完成服务评价"),
    STRAY_CAT_PUBLISH("stray_cat_publish", "发布流浪猫信息"),
    ADOPTION_COMPLETE("adoption_complete", "完成领养"),
    TRACKING_SUBMIT("tracking_submit", "提交跟踪反馈"),
    COMMUNITY_POST("community_post", "发布社区帖子"),
    DAILY_CHECKIN("daily_checkin", "每日签到"),
    INVITE_FRIEND("invite_friend", "邀请好友"),
    POINTS_DEDUCT("points_deduct", "积分抵扣"),
    EXCHANGE_Ai("exchange_ai", "兑换AI次数");
    
    private final String code;
    private final String description;
    
    PointsSource(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static PointsSource fromCode(String code) {
        for (PointsSource source : values()) {
            if (source.code.equals(code)) {
                return source;
            }
        }
        return ORDER_PUBLISH;
    }
}
