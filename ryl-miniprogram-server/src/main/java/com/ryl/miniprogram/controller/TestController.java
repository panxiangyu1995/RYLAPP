package com.ryl.miniprogram.controller;

import com.ryl.miniprogram.dto.TestNotificationRequest;
import com.ryl.miniprogram.entity.Task;
import com.ryl.miniprogram.entity.Customer;
import com.ryl.miniprogram.service.WeChatNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Date;

@Profile("dev")
@RestController
@RequestMapping("/api/test")
@Slf4j
public class TestController {

    @Autowired
    private WeChatNotificationService weChatNotificationService;

    @Value("${wx.miniapp.test-openid}")
    private String testOpenid;

    @PostMapping("/send-notification")
    public String sendTestNotification(@RequestBody TestNotificationRequest request) {
        String notificationType = request.getNotificationType();

        if (testOpenid == null || testOpenid.isEmpty() || "YOUR_TEST_OPENID_HERE".equals(testOpenid)) {
            return "Error: Test openid is not configured in application-dev.yml.";
        }
        if (notificationType == null || notificationType.isEmpty()) {
            return "Error: notificationType is required.";
        }

        log.info("接收到测试通知请求: openid={}, type={}", testOpenid, notificationType);

        // 创建模拟的Task和Customer对象
        Task mockTask = new Task();
        mockTask.setId(999L);
        mockTask.setTaskId("TEST-TASK-001");
        mockTask.setTitle("这是一个测试任务");
        mockTask.setDescription("这是一个用于测试订阅消息的模拟任务。");
        mockTask.setEngineerName("测试工程师");
        mockTask.setPrice(new BigDecimal("199.99"));

        Customer mockCustomer = new Customer();
        mockCustomer.setId(999L);
        mockCustomer.setOpenid(testOpenid);

        mockTask.setCustomerId(mockCustomer.getId());


        try {
            switch (notificationType) {
                case "engineer_assigned":
                    weChatNotificationService.sendEngineerAssignedNotification(mockTask);
                    break;
                case "quote_generated":
                    weChatNotificationService.sendQuoteReminderNotification(mockTask);
                    break;
                case "price_confirmed":
                    weChatNotificationService.sendPriceConfirmedNotification(mockTask);
                    break;
                case "service_completed":
                    weChatNotificationService.sendServiceCompletedNotification(mockTask);
                    break;
                case "evaluation_received":
                    weChatNotificationService.sendEvaluationReceivedNotification(mockTask);
                    break;
                default:
                    return "Error: Invalid notificationType.";
            }
            return "Notification sent successfully for type: " + notificationType;
        } catch (Exception e) {
            log.error("发送测试通知时出错", e);
            return "Error: " + e.getMessage();
        }
    }
} 