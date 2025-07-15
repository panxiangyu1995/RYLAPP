package com.ryl.engineer.service.impl;

import com.ryl.common.constant.TaskStatusConstants;
import com.ryl.engineer.entity.Task;
import com.ryl.engineer.entity.User;
import com.ryl.engineer.mapper.TaskMapper;
import com.ryl.engineer.mapper.UserMapper;
import com.ryl.engineer.service.ChatService;
import com.ryl.engineer.service.TaskDispatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class TaskDispatchServiceImpl implements TaskDispatchService {

    private static final Logger logger = LoggerFactory.getLogger(TaskDispatchServiceImpl.class);

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ChatService chatService;

    @Override
    @Scheduled(cron = "0 * * * * ?") // 每分钟执行一次
    @Transactional
    public void dispatchTasks() {
        logger.info("开始执行定时任务：派发新任务...");

        // 1. 查找所有待分配的任务
        List<Task> tasksToDispatch = taskMapper.findPendingTasks();
        if (CollectionUtils.isEmpty(tasksToDispatch)) {
            logger.info("没有需要派发的新任务。");
            return;
        }

        for (Task task : tasksToDispatch) {
            logger.info("正在为任务 #{} (类型: {}) 尝试派发...", task.getTaskId(), task.getTaskType());

            // 2. 根据任务的设备类型（这里我们用taskType作为近似匹配）查找合适的工程师
            // 注意：在真实业务中, deviceType 和 taskType 可能需要更复杂的映射关系
            List<User> potentialEngineers = userMapper.findEngineersByTechnicalCategory(task.getTaskType());

            if (CollectionUtils.isEmpty(potentialEngineers)) {
                logger.warn("任务 #{} (类型: {}) 没有找到技术分类匹配的工程师。", task.getTaskId(), task.getTaskType());
                continue;
            }

            // 3. 在候选工程师中，选择任务量最少的一位
            User bestEngineer = Collections.min(potentialEngineers, Comparator.comparingInt(engineer ->
                    taskMapper.countIncompleteTasksByUserId(engineer.getId())
            ));

            if (bestEngineer != null) {
                logger.info("为任务 #{} 找到最合适的工程师: {} (ID: {})", task.getTaskId(), bestEngineer.getName(), bestEngineer.getId());

                // 4. 分配任务并更新状态
                task.setEngineerId(bestEngineer.getId().intValue());
                task.setStatus(TaskStatusConstants.PENDING_CONFIRMATION);
                taskMapper.updateById(task);

                // 5. 发送系统消息通知工程师
                String messageContent = String.format(
                    "{\"text\":\"您有一个新的任务（%s）待处理，请及时确认。\",\"actions\":[{\"label\":\"接受\",\"type\":\"accept-task\",\"taskId\":\"%d\"},{\"label\":\"拒绝\",\"type\":\"reject-task\",\"taskId\":\"%d\"}]}",
                    task.getTitle(),
                    task.getId(),
                    task.getId()
                );
                chatService.sendSystemMessage(bestEngineer.getId(), messageContent);
                logger.info("已向工程师 {} 发送任务 #{} 的确认通知。", bestEngineer.getName(), task.getTaskId());

            } else {
                logger.error("任务 #{} 无法确定最合适的工程师进行派发。", task.getTaskId());
            }
        }

        logger.info("任务派发执行完毕。");
    }
} 