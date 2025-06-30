package com.ryl.engineer.controller;

import com.ryl.engineer.common.Result;
import com.ryl.engineer.dto.VehicleRecordRequest;
import com.ryl.engineer.dto.VehicleRecordResponse;
import com.ryl.engineer.dto.VehicleRecordStatsResponse;
import com.ryl.engineer.entity.User;
import com.ryl.engineer.service.UserService;
import com.ryl.engineer.service.VehicleRecordService;
import com.ryl.engineer.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * 车辆打卡记录控制器
 */
@RestController
@RequestMapping("/api/v1/vehicle-record")
public class VehicleRecordController {

    @Autowired
    private VehicleRecordService vehicleRecordService;
    
    @Autowired
    private UserService userService;

    /**
     * 创建打卡记录
     *
     * @param request 打卡记录请求
     * @param httpRequest HTTP请求
     * @return 打卡记录ID
     */
    @PostMapping
    public Result<Long> createRecord(@RequestBody @Validated VehicleRecordRequest request, HttpServletRequest httpRequest) {
        // 从请求头中获取token
        String token = httpRequest.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        // 验证token并获取用户ID
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) {
            return Result.error("未授权的访问");
        }
        
        // 创建打卡记录
        Long recordId = vehicleRecordService.createRecord(userId, request);
        return Result.success(recordId);
    }

    /**
     * 根据ID获取打卡记录
     *
     * @param id 记录ID
     * @return 打卡记录
     */
    @GetMapping("/{id}")
    public Result<VehicleRecordResponse> getRecordById(@PathVariable Long id) {
        VehicleRecordResponse record = vehicleRecordService.getRecordById(id);
        if (record == null) {
            return Result.error("记录不存在");
        }
        return Result.success(record);
    }

    /**
     * 获取当前用户的打卡记录列表
     *
     * @param httpRequest HTTP请求
     * @return 打卡记录列表
     */
    @GetMapping("/my-records")
    public Result<List<VehicleRecordResponse>> getMyRecords(HttpServletRequest httpRequest) {
        // 从请求头中获取token
        String token = httpRequest.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        // 验证token并获取用户ID
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) {
            return Result.error("未授权的访问");
        }
        
        // 获取打卡记录
        List<VehicleRecordResponse> records = vehicleRecordService.getRecordsByUserId(userId);
        return Result.success(records);
    }

    /**
     * 根据任务ID获取打卡记录列表
     *
     * @param taskId 任务ID
     * @return 打卡记录列表
     */
    @GetMapping("/task/{taskId}")
    public Result<List<VehicleRecordResponse>> getRecordsByTaskId(@PathVariable String taskId) {
        List<VehicleRecordResponse> records = vehicleRecordService.getRecordsByTaskId(taskId);
        return Result.success(records);
    }

    /**
     * 获取指定时间范围内的打卡记录列表
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param httpRequest HTTP请求
     * @return 打卡记录列表
     */
    @GetMapping("/time-range")
    public Result<List<VehicleRecordResponse>> getRecordsByTimeRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime,
            HttpServletRequest httpRequest) {
        // 从请求头中获取token
        String token = httpRequest.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        // 验证token并获取用户ID
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) {
            return Result.error("未授权的访问");
        }
        
        // 获取打卡记录
        List<VehicleRecordResponse> records = vehicleRecordService.getRecordsByTimeRange(userId, startTime, endTime);
        return Result.success(records);
    }

    /**
     * 获取指定月份的打卡统计信息
     *
     * @param year  年份
     * @param month 月份
     * @param httpRequest HTTP请求
     * @return 打卡统计信息
     */
    @GetMapping("/stats/month")
    public Result<VehicleRecordStatsResponse> getMonthlyStats(
            @RequestParam int year,
            @RequestParam int month,
            HttpServletRequest httpRequest) {
        // 从请求头中获取token
        String token = httpRequest.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        // 验证token并获取用户ID
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) {
            return Result.error("未授权的访问");
        }
        
        // 获取统计信息
        VehicleRecordStatsResponse stats = vehicleRecordService.getMonthlyStats(userId, year, month);
        return Result.success(stats);
    }

    /**
     * 获取当月的打卡统计信息
     *
     * @param httpRequest HTTP请求
     * @return 打卡统计信息
     */
    @GetMapping("/stats/current-month")
    public Result<VehicleRecordStatsResponse> getCurrentMonthStats(HttpServletRequest httpRequest) {
        // 从请求头中获取token
        String token = httpRequest.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        // 验证token并获取用户ID
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) {
            return Result.error("未授权的访问");
        }
        
        // 获取统计信息
        VehicleRecordStatsResponse stats = vehicleRecordService.getCurrentMonthStats(userId);
        return Result.success(stats);
    }
} 