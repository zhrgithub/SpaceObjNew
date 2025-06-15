package com.spaceobj.spaceobj.controller;

import com.spaceobj.spaceobj.pojo.Student;
import com.spaceobj.spaceobj.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 聊天控制器
 * 
 * 提供聊天相关的REST API接口，包括：
 * - 获取群组列表
 * - 获取用户列表
 * 
 * 主要用于前端动态加载聊天对象选择列表
 * 
 * @author SpaceObj Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/chat")
@Tag(name = "聊天管理", description = "聊天相关接口")
@CrossOrigin(origins = "*") // 允许跨域请求
public class ChatController {

    /**
     * 聊天服务层，提供聊天相关的业务逻辑
     */
    @Autowired
    private ChatService chatService;

    /**
     * 获取所有群组列表
     * 
     * 从数据库查询所有存在的群组ID，返回给前端用于群聊目标选择
     * 
     * @return 包含群组列表的响应对象
     *         - code: 响应状态码 (200成功, 500失败)
     *         - message: 响应消息
     *         - data: 群组ID列表
     */
    @GetMapping("/groups")
    @Operation(summary = "获取所有群组列表", description = "获取系统中所有群组的ID列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取群组列表"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Map<String, Object> getAllGroups() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<String> groups = chatService.getAllGroups();
            response.put("code", 200);
            response.put("message", "获取群组列表成功");
            response.put("data", groups);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "获取群组列表失败：" + e.getMessage());
        }
        return response;
    }

    /**
     * 获取所有用户列表
     * 
     * 从数据库查询所有用户信息，返回给前端用于私聊对象选择和用户身份选择
     * 
     * @return 包含用户列表的响应对象
     *         - code: 响应状态码 (200成功, 500失败)
     *         - message: 响应消息
     *         - data: 用户信息列表，包含studentId、firstName、lastName等字段
     */
    @GetMapping("/users")
    @Operation(summary = "获取所有用户列表", description = "获取系统中所有用户的信息列表，用于私聊选择")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取用户列表"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Map<String, Object> getAllUsers() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Student> users = chatService.getAllUsers();
            response.put("code", 200);
            response.put("message", "获取用户列表成功");
            response.put("data", users);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "获取用户列表失败：" + e.getMessage());
        }
        return response;
    }
} 