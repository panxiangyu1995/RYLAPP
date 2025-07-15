package com.ryl.common.constant;

/**
 * 任务状态常量类
 */
public final class TaskStatusConstants {

    private TaskStatusConstants() {
        // 私有构造函数，防止实例化
    }

    /**
     * 草稿
     */
    public static final String DRAFT = "draft";

    /**
     * 待处理
     */
    public static final String PENDING = "pending";

    /**
     * 待工程师确认
     */
    public static final String PENDING_CONFIRMATION = "待确认";

    /**
     * 进行中
     */
    public static final String IN_PROGRESS = "in-progress";

    /**
     * 等待配件
     */
    public static final String WAITING_PARTS = "waiting-parts";

    /**
     * 客户确认中
     */
    public static final String CLIENT_REVIEW = "client-review";

    /**
     * 已完成
     */
    public static final String COMPLETED = "completed";

    /**
     * 已取消
     */
    public static final String CANCELLED = "cancelled";
} 