package com.ryl.engineer.service;

import com.ryl.engineer.dto.request.TaskStepRecordRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 任务流程相关服务接口
 */
public interface TaskFlowService {

    /**
     * 添加一条任务步骤工作记录，并处理附件上传
     *
     * @param taskId  当前任务的ID
     * @param request 包含记录文本内容的请求体
     * @param files   附带的附件文件数组，可以为空
     * @throws IOException 如果文件上传失败
     */
    void addTaskFlowRecord(String taskId, TaskStepRecordRequest request, MultipartFile[] files) throws IOException;

    /**
     * 将任务推进到下一个步骤
     *
     * @param taskId 需要推进的任务ID
     * @return 如果操作成功，返回 true；否则返回 false
     */
    boolean nextStep(String taskId);

    /**
     * 将任务回退到上一个步骤
     *
     * @param taskId 需要回退的任务ID
     * @return 如果操作成功，返回 true；否则返回 false
     */
    boolean prevStep(String taskId);
} 