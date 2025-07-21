package com.ryl.miniprogram.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import com.ryl.miniprogram.entity.Customer;
import com.ryl.miniprogram.entity.NotificationLog;
import com.ryl.miniprogram.entity.Task;
import com.ryl.miniprogram.mapper.CustomerMapper;
import com.ryl.miniprogram.mapper.NotificationLogMapper;
import com.ryl.miniprogram.service.WeChatNotificationService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WeChatNotificationServiceImpl implements WeChatNotificationService {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private WxMaService wxMaService;

    @Autowired
    private NotificationLogMapper notificationLogMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Value("${wx.miniapp.template-ids.engineer-assigned}")
    private String engineerAssignedTemplateId;

    @Value("${wx.miniapp.template-ids.quote-reminder}")
    private String quoteReminderTemplateId;
    
    @Value("${wx.miniapp.template-ids.price-confirmed}")
    private String priceConfirmedTemplateId;

    @Value("${wx.miniapp.template-ids.service-completed}")
    private String serviceCompletedTemplateId;

    @Value("${wx.miniapp.template-ids.evaluation-received}")
    private String evaluationReceivedTemplateId;

    @Value("${wx.miniapp.page.task-detail}")
    private String taskDetailPage;

    @Value("${wx.miniapp.miniprogram-state}")
    private String miniprogramState;

    @Async
    @Override
    public void sendEngineerAssignedNotification(Task task) {
        final String templateId = engineerAssignedTemplateId;
        log.info("准备发送工程师接单通知，任务ID: {}, 模板ID: {}", task.getTaskId(), templateId);

        if (isNotificationSent(task.getTaskId(), templateId)) {
            return;
        }

        Customer customer = customerMapper.selectById(task.getCustomerId());
        if (isCustomerInvalid(customer)) {
            return;
        }

        List<WxMaSubscribeMessage.MsgData> data = Arrays.asList(
                new WxMaSubscribeMessage.MsgData("thing1", "您的订单已有工程师接单"),
                new WxMaSubscribeMessage.MsgData("time2", DATE_FORMAT.format(new Date())),
                new WxMaSubscribeMessage.MsgData("character_string3", task.getTaskId()),
                new WxMaSubscribeMessage.MsgData("phrase4", "处理中")
        );

        String page = taskDetailPage + "?id=" + task.getId();
        sendMessage(customer.getOpenid(), templateId, data, page, task);
    }

    @Async
    @Override
    public void sendQuoteReminderNotification(Task task) {
        final String templateId = quoteReminderTemplateId;
        log.info("准备发送报价提醒通知，任务ID: {}, 模板ID: {}", task.getTaskId(), templateId);

        if (isNotificationSent(task.getTaskId(), templateId)) {
            return;
        }

        Customer customer = customerMapper.selectById(task.getCustomerId());
        if (isCustomerInvalid(customer)) {
            return;
        }

        List<WxMaSubscribeMessage.MsgData> data = Arrays.asList(
                new WxMaSubscribeMessage.MsgData("amount4", task.getPrice().toPlainString()),
                new WxMaSubscribeMessage.MsgData("character_string1", task.getTaskId())
        );

        String page = taskDetailPage + "?id=" + task.getId() + "&confirm=true";
        sendMessage(customer.getOpenid(), templateId, data, page, task);
    }

    @Override
    public void sendPriceConfirmedNotification(Task task) {
        final String templateId = priceConfirmedTemplateId;
        log.info("准备发送报价确认通知，任务ID: {}, 模板ID: {}", task.getTaskId(), templateId);

        if (isNotificationSent(task.getTaskId(), templateId)) {
            return;
        }

        Customer customer = customerMapper.selectById(task.getCustomerId());
        if (isCustomerInvalid(customer)) {
            return;
        }

        List<WxMaSubscribeMessage.MsgData> data = Arrays.asList(
                new WxMaSubscribeMessage.MsgData("thing1", "您的订单报价已确认"),
                new WxMaSubscribeMessage.MsgData("time2", DATE_FORMAT.format(new Date())),
                new WxMaSubscribeMessage.MsgData("character_string3", task.getTaskId()),
                new WxMaSubscribeMessage.MsgData("phrase4", "报价已确认")
        );

        String page = taskDetailPage + "?id=" + task.getId();
        sendMessage(customer.getOpenid(), templateId, data, page, task);
    }

    @Async
    @Override
    public void sendServiceCompletedNotification(Task task) {
        final String templateId = serviceCompletedTemplateId;
        log.info("准备发送服务完成通知，任务ID: {}, 模板ID: {}", task.getTaskId(), templateId);

        if (isNotificationSent(task.getTaskId(), templateId)) {
            return;
        }

        Customer customer = customerMapper.selectById(task.getCustomerId());
        if (isCustomerInvalid(customer)) {
            return;
        }

        List<WxMaSubscribeMessage.MsgData> data = Arrays.asList(
                new WxMaSubscribeMessage.MsgData("thing1", "您的订单已服务完成，请及时评价"),
                new WxMaSubscribeMessage.MsgData("time2", DATE_FORMAT.format(new Date())),
                new WxMaSubscribeMessage.MsgData("character_string3", task.getTaskId()),
                new WxMaSubscribeMessage.MsgData("phrase4", "待评价")
        );

        String page = taskDetailPage + "?id=" + task.getId() + "&evaluate=true";
        sendMessage(customer.getOpenid(), templateId, data, page, task);
    }

    @Async
    @Override
    public void sendEvaluationReceivedNotification(Task task) {
        final String templateId = evaluationReceivedTemplateId;
        log.info("准备发送评价通知，任务ID: {}, 模板ID: {}", task.getTaskId(), templateId);

        if (isNotificationSent(task.getTaskId(), templateId)) {
            return;
        }

        Customer customer = customerMapper.selectById(task.getCustomerId());
        if (isCustomerInvalid(customer)) {
            return;
        }

        List<WxMaSubscribeMessage.MsgData> data = Arrays.asList(
                new WxMaSubscribeMessage.MsgData("thing1", "感谢您的宝贵评价！"),
                new WxMaSubscribeMessage.MsgData("thing4", truncate(task.getTitle(), 20)),
                new WxMaSubscribeMessage.MsgData("thing5", truncate(task.getEngineerName(), 20)),
                new WxMaSubscribeMessage.MsgData("character_string6", task.getTaskId())
        );

        String page = taskDetailPage + "?id=" + task.getId();
        sendMessage(customer.getOpenid(), templateId, data, page, task);
    }

    private void sendMessage(String openid, String templateId, List<WxMaSubscribeMessage.MsgData> data, String page, Task task) {
        try {
            WxMaSubscribeMessage subscribeMessage = WxMaSubscribeMessage.builder()
                    .toUser(openid)
                    .templateId(templateId)
                    .page(page)
                    .miniprogramState(miniprogramState)
                    .data(data)
                    .build();

            // 如果是测试任务，则不记录数据库日志
            if ("TEST-TASK-001".equals(task.getTaskId())) {
                try {
                    wxMaService.getMsgService().sendSubscribeMsg(subscribeMessage);
                    log.info("测试订阅消息发送成功: openid={}, templateId={}", openid, templateId);
                } catch (WxErrorException e) {
                    log.error("测试订阅消息发送失败: openid={}, templateId={}, 错误: {}", openid, templateId, e.getMessage());
                }
                return;
            }
            
            NotificationLog logEntry = new NotificationLog();
            logEntry.setTaskId(task.getTaskId());
            logEntry.setCustomerId(task.getCustomerId());
            logEntry.setTemplateId(templateId);
            logEntry.setCreateTime(new Date());

            try {
                wxMaService.getMsgService().sendSubscribeMsg(subscribeMessage);
                log.info("订阅消息发送成功: openid={}, templateId={}", openid, templateId);
                logEntry.setStatus("SENT");
            } catch (WxErrorException e) {
                log.error("订阅消息发送失败: openid={}, templateId={}, 错误: {}", openid, templateId, e.getMessage());
                logEntry.setStatus("FAILED");
                logEntry.setResponseCode(String.valueOf(e.getError().getErrorCode()));
                logEntry.setResponseMessage(e.getError().getErrorMsg());
            } finally {
                logEntry.setUpdateTime(new Date());
                notificationLogMapper.insert(logEntry);
            }
        } catch (Exception e) {
            log.error("发送消息时发生异常: {}", e.getMessage());
        }
    }

    private boolean isNotificationSent(String taskId, String templateId) {
        NotificationLog existingLog = notificationLogMapper.selectByTaskIdAndTemplateId(taskId, templateId);
        if (existingLog != null && "SENT".equals(existingLog.getStatus())) {
            log.warn("任务 {} 的模板 {} 通知已发送过，不再重复发送。", taskId, templateId);
            return true;
        }
        return false;
    }

    private boolean isCustomerInvalid(Customer customer) {
        if (customer == null || customer.getOpenid() == null) {
            log.error("无法发送通知：找不到客户信息或客户OpenID为空, 客户ID: {}", customer != null ? customer.getId() : "null");
            return true;
        }
        return false;
    }

    private String truncate(String value, int length) {
        if (value == null) {
            return "";
        }
        return value.length() > length ? value.substring(0, length) : value;
    }
}