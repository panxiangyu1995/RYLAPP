package com.ryl.engineer.controller;

import com.ryl.engineer.common.Result;
import com.ryl.engineer.dto.VehicleInfoRequest;
import com.ryl.engineer.dto.VehicleInfoResponse;
import com.ryl.engineer.entity.VehicleInfo;
import com.ryl.engineer.service.VehicleInfoService;
import com.ryl.engineer.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 车辆信息控制器
 */
@RestController
@RequestMapping("/api/v1/user")
public class VehicleInfoController {

    @Autowired
    private VehicleInfoService vehicleInfoService;

    /**
     * 获取用户车辆信息
     *
     * @param request HTTP请求
     * @return 车辆信息响应
     */
    @GetMapping("/vehicle")
    public Result<VehicleInfoResponse> getUserVehicle(HttpServletRequest request) {
        // 从请求头中获取token
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        if (token == null || token.isEmpty()) {
            return Result.error("未登录");
        }
        
        // 从token中获取用户ID
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) {
            return Result.error("无效的token");
        }
        
        // 获取用户车辆信息
        VehicleInfoResponse response = vehicleInfoService.getUserVehicle(userId);
        if (response == null) {
            return Result.error("没有车辆信息");
        }
        
        return Result.success(response);
    }

    /**
     * 更新用户车辆信息
     *
     * @param request     车辆信息请求
     * @param httpRequest HTTP请求
     * @return 更新结果
     */
    @PutMapping("/vehicle")
    public Result<Map<String, Object>> updateVehicle(@RequestBody @Validated VehicleInfoRequest request, HttpServletRequest httpRequest) {
        // 从请求头中获取token
        String token = httpRequest.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        if (token == null || token.isEmpty()) {
            return Result.error("未登录");
        }
        
        // 从token中获取用户ID
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) {
            return Result.error("无效的token");
        }
        
        // 更新用户车辆信息
        VehicleInfo vehicleInfo = vehicleInfoService.updateVehicle(userId, request);
        if (vehicleInfo == null) {
            return Result.error("更新失败");
        }
        
        // 返回更新成功的结果
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("updated", true);
        resultMap.put("vehicleId", vehicleInfo.getId());
        
        return Result.success(resultMap);
    }

    /**
     * 获取用户所有车辆信息
     *
     * @param request HTTP请求
     * @return 车辆信息列表
     */
    @GetMapping("/vehicle/list")
    public Result<List<VehicleInfoResponse>> getUserVehicles(HttpServletRequest request) {
        // 从请求头中获取token
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        if (token == null || token.isEmpty()) {
            return Result.error("未登录");
        }
        
        // 从token中获取用户ID
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) {
            return Result.error("无效的token");
        }
        
        // 获取用户车辆信息列表
        List<VehicleInfoResponse> vehicleInfoList = vehicleInfoService.getUserVehicleResponses(userId);
        
        return Result.success(vehicleInfoList);
    }
    
    /**
     * 获取指定ID的车辆信息
     *
     * @param id      车辆ID
     * @param request HTTP请求
     * @return 车辆信息
     */
    @GetMapping("/vehicle/{id}")
    public Result<VehicleInfoResponse> getVehicleById(@PathVariable("id") Long id, HttpServletRequest request) {
        // 从请求头中获取token
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        if (token == null || token.isEmpty()) {
            return Result.error("未登录");
        }
        
        // 从token中获取用户ID
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) {
            return Result.error("无效的token");
        }
        
        // 获取指定ID的车辆信息
        VehicleInfoResponse vehicleInfo = vehicleInfoService.getVehicleById(id, userId);
        if (vehicleInfo == null) {
            return Result.error("车辆信息不存在");
        }
        
        return Result.success(vehicleInfo);
    }
    
    /**
     * 创建用户车辆信息
     *
     * @param request     车辆信息请求
     * @param httpRequest HTTP请求
     * @return 创建结果
     */
    @PostMapping("/vehicle")
    public Result<Map<String, Object>> createVehicle(@RequestBody @Validated VehicleInfoRequest request, HttpServletRequest httpRequest) {
        // 从请求头中获取token
        String token = httpRequest.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        if (token == null || token.isEmpty()) {
            return Result.error("未登录");
        }
        
        // 从token中获取用户ID
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) {
            return Result.error("无效的token");
        }
        
        // 创建用户车辆信息
        VehicleInfo vehicleInfo = vehicleInfoService.createVehicle(userId, request);
        if (vehicleInfo == null) {
            return Result.error("创建失败");
        }
        
        // 返回创建成功的结果
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("created", true);
        resultMap.put("vehicleId", vehicleInfo.getId());
        
        return Result.success(resultMap);
    }
    
    /**
     * 删除用户车辆信息
     *
     * @param id      车辆ID
     * @param request HTTP请求
     * @return 删除结果
     */
    @DeleteMapping("/vehicle/{id}")
    public Result<Map<String, Object>> deleteVehicle(@PathVariable("id") Long id, HttpServletRequest request) {
        // 从请求头中获取token
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        if (token == null || token.isEmpty()) {
            return Result.error("未登录");
        }
        
        // 从token中获取用户ID
        Long userId = JwtUtil.getUserId(token);
        if (userId == null) {
            return Result.error("无效的token");
        }
        
        // 删除用户车辆信息
        boolean success = vehicleInfoService.deleteVehicle(id, userId);
        if (!success) {
            return Result.error("删除失败");
        }
        
        // 返回删除成功的结果
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("deleted", true);
        
        return Result.success(resultMap);
    }
} 