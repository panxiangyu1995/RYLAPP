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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class WeChatNotificationServiceImpl implements WeChatNotificationService {

    @Autowired
    private WxMaService wxMaService;

    @Autowired
    private NotificationLogMapper notificationLogMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Value("${wx.miniapp.template-ids.engineer-assigned}")
    private String engineerAssignedTemplateId;

    @Value("${wx.miniapp.template-ids.quote-generated}")
    private String quoteGeneratedTemplateId;
    
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

        // 准备数据
        Map<String, String> data = new HashMap<>();
        data.put("thing1", truncate(task.getTitle(), 20));
        data.put("name2", truncate(task.getEngineerName(), 10));

        String page = taskDetailPage + "?id=" + task.getId();
        sendMessage(customer.getOpenid(), templateId, data, page, task);
    }

    @Async
    @Override
    public void sendQuoteGeneratedNotification(Task task) {
        final String templateId = quoteGeneratedTemplateId;
        log.info("准备发送报价通知，任务ID: {}, 模板ID: {}", task.getTaskId(), templateId);

        if (isNotificationSent(task.getTaskId(), templateId)) {
            return;
        }

        Customer customer = customerMapper.selectById(task.getCustomerId());
        if (isCustomerInvalid(customer)) {
            return;
        }

        // 准备数据
        Map<String, String> data = new HashMap<>();
        data.put("thing1", truncate(task.getTitle(), 20));
        data.put("amount2", task.getPrice().toPlainString());
        data.put("date3", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));

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

        // 准备数据
        Map<String, String> data = new HashMap<>();
        data.put("thing1", truncate(task.getTitle(), 20));
        data.put("phrase2", "已确认");
        data.put("thing3", truncate("报价：" + task.getPrice().toPlainString() + "元", 20));

        String page = taskDetailPage + "?id=" + task.getId();
        sendMessage(customer.getOpenid(), templateId, data, page, task);
    }

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

        // 准备数据
        Map<String, String> data = new HashMap<>();
        data.put("thing1", truncate(task.getTitle(), 20));
        data.put("thing2", truncate("服务已完成，请评价", 20));
        data.put("thing3", truncate(task.getEngineerName(), 20));
        data.put("date4", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));

        String page = taskDetailPage + "?id=" + task.getId() + "&evaluate=true";
        sendMessage(customer.getOpenid(), templateId, data, page, task);
    }

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

        // 准备数据
        Map<String, String> data = new HashMap<>();
        data.put("thing1", truncate(task.getTitle(), 20));
        data.put("thing2", truncate("感谢您的评价", 20));
        data.put("thing3", truncate("我们将继续为您提供优质服务", 20));

        String page = taskDetailPage + "?id=" + task.getId();
        sendMessage(customer.getOpenid(), templateId, data, page, task);
    }

    private void sendMessage(String openid, String templateId, Map<String, String> data, String page, Task task) {
        try {
            // 创建WxMaSubscribeMessage对象
            WxMaSubscribeMessage.WxMaSubscribeMessageBuilder builder = WxMaSubscribeMessage.builder()
                    .toUser(openid)
                    .templateId(templateId)
                    .page(page)
                    .miniprogramState(miniprogramState);
            
            // 如果有数据，添加到消息中
            if (data != null && !data.isEmpty()) {
                // 使用反射查看WxMaSubscribeMessage类的结构
                Class<?> builderClass = builder.getClass();
                log.info("WxMaSubscribeMessageBuilder类名: {}", builderClass.getName());
                
                // 查看可用方法
                java.lang.reflect.Method[] methods = builderClass.getDeclaredMethods();
                for (java.lang.reflect.Method method : methods) {
                    log.info("方法: {}", method.getName());
                }
                
                // 尝试使用data方法
                try {
                    // 创建数据对象
                    java.util.Map<String, Object> dataMap = new java.util.HashMap<>();
                    for (Map.Entry<String, String> entry : data.entrySet()) {
                        java.util.Map<String, String> item = new java.util.HashMap<>();
                        item.put("value", entry.getValue());
                        dataMap.put(entry.getKey(), item);
                    }
                    
                    // 尝试调用data方法
                    builder.getClass().getMethod("data", java.util.Map.class).invoke(builder, dataMap);
                } catch (Exception e) {
                    log.error("反射调用data方法失败: {}", e.getMessage());
                    
                    // 如果上面的方法失败，尝试另一种方式
                    try {
                        // 创建WxMaSubscribeMessage.MsgData对象列表
                        java.util.List<Object> dataList = new java.util.ArrayList<>();
                        for (Map.Entry<String, String> entry : data.entrySet()) {
                            // 尝试创建MsgData对象
                            Class<?> msgDataClass = Class.forName("cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage$MsgData");
                            Object msgData = msgDataClass.getConstructor(String.class, String.class)
                                    .newInstance(entry.getKey(), entry.getValue());
                            dataList.add(msgData);
                        }
                        
                        // 尝试调用data方法
                        builder.getClass().getMethod("data", java.util.List.class).invoke(builder, dataList);
                    } catch (Exception ex) {
                        log.error("反射调用data方法失败(第二次尝试): {}", ex.getMessage());
                    }
                }
            }
            
            WxMaSubscribeMessage subscribeMessage = builder.build();

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