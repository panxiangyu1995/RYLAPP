package com.ryl.miniprogram.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ryl.miniprogram.entity.Task;
import com.ryl.miniprogram.exception.BusinessException;
import com.ryl.miniprogram.mapper.TaskMapper;
import com.ryl.miniprogram.service.TaskFlowService;
import com.ryl.miniprogram.service.TaskService;
import com.ryl.miniprogram.service.WeChatNotificationService;
import com.ryl.miniprogram.vo.PageVO;
import com.ryl.miniprogram.vo.StepRecordVO;
import com.ryl.miniprogram.vo.TaskDetailVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.sql.Timestamp;
import java.util.List;

/**
 * 任务服务实现类
 */
@Slf4j
@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {
    
    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private TaskFlowService taskFlowService;

    @Autowired
    private WeChatNotificationService weChatNotificationService;
    
    @Override
    public Task getById(Long id) {
        return taskMapper.selectById(id);
    }

    @Override
    public TaskDetailVO getTaskDetailById(Long id) {
        Task task = taskMapper.selectDetailById(id);
        log.info("从Mapper查询到的任务详情: {}", task);
        if (task == null) {
            return null;
        }

        TaskDetailVO taskDetailVO = new TaskDetailVO();
        BeanUtils.copyProperties(task, taskDetailVO);

        log.info("复制属性后的TaskDetailVO: {}", taskDetailVO);

        List<StepRecordVO> steps = taskFlowService.getTaskFlowHistoryWithDetails(String.valueOf(id));
        taskDetailVO.setSteps(steps);

        return taskDetailVO;
    }
    
    @Override
    public int countDailyTasks(Long userId) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        Date startDate = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
        return taskMapper.selectCount(new QueryWrapper<Task>()
                .eq("customer_id", userId)
                .ge("create_time", startDate)
        ).intValue();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(Task task) {
        // 校验用户当天提交订单数量
        if (task.getCustomerId() != null) {
            int todayCount = countDailyTasks(task.getCustomerId());
            if (todayCount >= 25) {
                throw new BusinessException("当天订单提交次数已达上限");
            }
        }
        
        // 确保 taskId 不为 null
        if (task.getTaskId() == null || task.getTaskId().isEmpty()) {
            throw new IllegalArgumentException("任务编号不能为空");
        }
        
        log.info("保存任务，taskId: {}, title: {}", task.getTaskId(), task.getTitle());
        task.setCreateTime(new Date());
        task.setUpdateTime(new Date());
        return taskMapper.insert(task) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(Task task) {
        task.setUpdateTime(new Date());
        return taskMapper.updateById(task) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(Long id) {
        return taskMapper.deleteById(id) > 0;
    }
    
    @Override
    public List<Task> listByCustomerId(Long customerId) {
        return taskMapper.selectByCustomerId(customerId);
    }
    
    @Override
    public List<Task> listByStatus(String status) {
        return taskMapper.selectByStatus(status);
    }
    
    @Override
    public PageVO<Task> listByPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Task> list = taskMapper.selectList(new QueryWrapper<>());
        PageInfo<Task> pageInfo = new PageInfo<>(list);
        
        PageVO<Task> pageVO = new PageVO<>();
        pageVO.setPageNum(pageInfo.getPageNum());
        pageVO.setPageSize(pageInfo.getPageSize());
        pageVO.setTotalPage(pageInfo.getPages());
        pageVO.setTotal(pageInfo.getTotal());
        pageVO.setList(pageInfo.getList());
        return pageVO;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Long id, String status) {
        Task task = getById(id);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }
        
        task.setStatus(status);
        task.setUpdateTime(new Date());
        return update(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmPrice(String taskId) {
        Task task = taskMapper.selectByTaskId(taskId);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }
        if (task.getPriceConfirmed() == 1) {
            log.warn("任务 {} 的价格已被确认，无需重复操作。", taskId);
            return;
        }

        task.setPriceConfirmed(1);
        task.setUpdateTime(new Date());
        int updatedRows = taskMapper.updateById(task);

        if (updatedRows > 0) {
            log.info("任务 {} 价格确认成功。", taskId);
            // 异步发送价格确认通知
            weChatNotificationService.sendPriceConfirmedNotification(task);
        } else {
            log.error("任务 {} 价格确认失败，更新数据库失败。", taskId);
            throw new BusinessException("价格确认失败");
        }
    }

    @Override
    @Transactional
    public void notifyCustomerOfPrice(Long customerId, String taskId, java.math.BigDecimal price) {
        Task task = taskMapper.selectByTaskId(taskId);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }

        task.setPrice(price);
        task.setPriceConfirmed(0); // 重置确认状态
        task.setUpdateTime(new Date());
        taskMapper.updateById(task);
        
        weChatNotificationService.sendQuoteReminderNotification(task);
    }
} 