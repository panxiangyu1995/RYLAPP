package com.ryl.miniprogram.service;

import com.ryl.miniprogram.entity.Task;

public interface WeChatNotificationService {

    void sendEngineerAssignedNotification(Task task);

    void sendQuoteGeneratedNotification(Task task);

    void sendPriceConfirmedNotification(Task task);

    void sendServiceCompletedNotification(Task task);

    void sendEvaluationReceivedNotification(Task task);
} 