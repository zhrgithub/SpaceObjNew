package com.spaceobj.spaceobj.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.spaceobj.spaceobj.pojo.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.spaceobj.spaceobj.service.ChatService;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket聊天处理器
 * 
 * 负责处理WebSocket连接和聊天消息的核心类
 * 支持私聊、群聊、历史记录等功能
 * 
 * @author SpaceObj Team
 * @version 1.0
 */
@Slf4j
@Component
public class SimpleChatHandler extends TextWebSocketHandler {
    
    /**
     * 聊天服务层，处理消息存储和查询
     */
    @Autowired
    private ChatService chatService;
    
    /**
     * 在线用户映射表
     * Key: 学生ID (Integer)
     * Value: WebSocket会话 (WebSocketSession)
     * 用于快速查找在线用户的WebSocket连接
     */
    private final Map<Integer, WebSocketSession> onlineUsers = new ConcurrentHashMap<>();
    
    /**
     * 群组成员映射表
     * Key: 群组ID (String)
     * Value: 群组成员ID集合 (Set<Integer>)
     * 用于管理群组成员关系和消息转发
     */
    private final Map<String, Set<Integer>> groupMembers = new ConcurrentHashMap<>();
    
    /**
     * 私聊历史记录发送状态跟踪
     * Key: 发送者ID (Integer)
     * Value: 已发送过历史记录的目标用户ID集合 (Set<Integer>)
     * 用于避免重复发送私聊历史记录
     */
    private final Map<Integer, Set<Integer>> privateChatHistorySent = new ConcurrentHashMap<>();
    
    /**
     * WebSocket连接建立后的处理
     * 
     * 当客户端成功建立WebSocket连接时调用此方法
     * 主要功能：
     * 1. 从URL参数中提取学生ID
     * 2. 将用户添加到在线用户列表
     * 3. 加载用户已加入的群组信息
     * 4. 发送连接成功确认消息
     * 
     * @param session WebSocket会话对象
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        try {
            // 从URL参数获取studentId
            Integer studentId = getStudentIdFromSession(session);
            if (studentId == null) {
                session.close();
                return;
            }
            
            // 将用户添加到在线用户列表
            onlineUsers.put(studentId, session);
            
            // 从数据库加载用户已加入的群组
            loadUserGroups(studentId);
            
            // 发送连接确认消息
            sendToUser(session, new JSONObject()
                .fluentPut("type", "CONNECT_SUCCESS")
                .fluentPut("studentId", studentId));
                
        } catch (Exception e) {
            log.error("WebSocket连接建立失败", e);
            try {
                session.close();
            } catch (IOException ex) {
                log.error("关闭WebSocket连接失败", ex);
            }
        }
    }
    
    /**
     * 处理接收到的文本消息
     * 
     * 解析客户端发送的JSON消息，根据消息类型分发到对应的处理方法
     * 支持的消息类型：
     * - CONNECT: 连接确认
     * - PRIVATE_MESSAGE: 私聊消息
     * - GROUP_MESSAGE: 群聊消息
     * - JOIN_GROUP: 加入群组
     * - LEAVE_GROUP: 离开群组
     * 
     * @param session WebSocket会话对象
     * @param message 接收到的文本消息
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            // 解析JSON消息
            JSONObject json = JSON.parseObject(message.getPayload());
            String type = json.getString("type");
            
            // 根据消息类型分发处理
            switch (type) {
                case "CONNECT":
                    handleConnect(session, json);
                    break;
                case "PRIVATE_MESSAGE":
                    handlePrivateMessage(session, json);
                    break;
                case "GROUP_MESSAGE":
                    handleGroupMessage(session, json);
                    break;
                case "JOIN_GROUP":
                    handleJoinGroup(session, json);
                    break;
                case "LEAVE_GROUP":
                    handleLeaveGroup(session, json);
                    break;
                default:
                    sendError(session, "未知的消息类型: " + type);
            }
        } catch (Exception e) {
            log.error("处理WebSocket消息失败", e);
            sendError(session, "消息处理失败");
        }
    }
    
    private void handleConnect(WebSocketSession session, JSONObject json) {
        try {
            Integer studentId = json.getInteger("studentId");
            String studentName = json.getString("studentName");
            
            log.info("处理CONNECT消息: studentId={}, studentName={}", studentId, studentName);
            
            // 发送连接成功确认消息
            sendToUser(session, new JSONObject()
                .fluentPut("type", "CONNECT_SUCCESS")
                .fluentPut("message", "连接成功"));
                
        } catch (Exception e) {
            log.error("处理CONNECT消息失败", e);
            sendError(session, "连接处理失败");
        }
    }
    
    private void handlePrivateMessage(WebSocketSession session, JSONObject json) {
        try {
            Integer targetId = json.getInteger("targetStudentId");
            String content = json.getString("content");
            Integer senderId = json.getInteger("senderId");
            String senderName = json.getString("senderName");
            
            if (targetId == null || content == null || senderId == null) {
                sendError(session, "私聊消息参数不完整");
                return;
            }
            
            // 检查是否需要发送私聊历史记录
            Set<Integer> sentTo = privateChatHistorySent.computeIfAbsent(senderId, k -> new HashSet<>());
            if (!sentTo.contains(targetId)) {
                // 第一次与此用户私聊，发送历史记录
                sendPrivateHistory(session, senderId, targetId);
                sentTo.add(targetId);
            }
            
            // 保存到数据库
            chatService.saveMessage("PRIVATE", senderId, targetId, null, content);
            
            // 给接收者发送消息
            WebSocketSession targetSession = onlineUsers.get(targetId);
            if (targetSession != null && targetSession.isOpen()) {
                JSONObject targetMsg = new JSONObject()
                    .fluentPut("type", "PRIVATE_MESSAGE")
                    .fluentPut("senderId", senderId)
                    .fluentPut("senderName", senderName)
                    .fluentPut("content", content)
                    .fluentPut("timestamp", new Date());
                sendToUser(targetSession, targetMsg);
            }
            
            // 给发送者发送确认消息，让发送者也能看到自己发送的消息
            JSONObject senderMsg = new JSONObject()
                .fluentPut("type", "PRIVATE_MESSAGE")
                .fluentPut("senderId", senderId)
                .fluentPut("senderName", "我")  // 显示为"我"
                .fluentPut("content", content)
                .fluentPut("targetId", targetId)  // 添加目标ID用于显示
                .fluentPut("isSender", true)  // 标记这是发送者看到的消息
                .fluentPut("timestamp", new Date());
            sendToUser(session, senderMsg);
            
        } catch (Exception e) {
            log.error("处理私聊消息失败", e);
            sendError(session, "发送私聊消息失败");
        }
    }
    
    private void handleGroupMessage(WebSocketSession session, JSONObject json) {
        try {
            String groupId = json.getString("groupId");
            String content = json.getString("content");
            Integer senderId = json.getInteger("senderId");
            String senderName = json.getString("senderName");
            
            if (groupId == null || content == null || senderId == null) {
                sendError(session, "群聊消息参数不完整");
                return;
            }
            
            // 验证用户是否在群组中
            Set<Integer> members = groupMembers.get(groupId);
            if (members == null || !members.contains(senderId)) {
                sendError(session, "您不是该群组的成员");
                return;
            }
            
            // 保存到数据库
            chatService.saveMessage("GROUP", senderId, null, groupId, content);
            
            // 转发给群成员
            JSONObject msg = new JSONObject()
                .fluentPut("type", "GROUP_MESSAGE")
                .fluentPut("groupId", groupId)
                .fluentPut("senderId", senderId)
                .fluentPut("senderName", senderName)
                .fluentPut("content", content)
                .fluentPut("timestamp", new Date());
                
            members.forEach(memberId -> {
                WebSocketSession memberSession = onlineUsers.get(memberId);
                if (memberSession != null && memberSession.isOpen()) {
                    // 如果是发送者自己，标记为自己发送的消息
                    if (memberId.equals(senderId)) {
                        JSONObject senderMsg = new JSONObject()
                            .fluentPut("type", "GROUP_MESSAGE")
                            .fluentPut("groupId", groupId)
                            .fluentPut("senderId", senderId)
                            .fluentPut("senderName", "我")
                            .fluentPut("content", content)
                            .fluentPut("isSender", true)
                            .fluentPut("timestamp", new Date());
                        sendToUser(memberSession, senderMsg);
                    } else {
                        // 发给其他群成员
                        sendToUser(memberSession, msg);
                    }
                }
            });
        } catch (Exception e) {
            log.error("处理群聊消息失败", e);
            sendError(session, "发送群聊消息失败");
        }
    }
    
    private void handleJoinGroup(WebSocketSession session, JSONObject json) {
        try {
            String groupId = json.getString("groupId");
            Integer studentId = json.getInteger("studentId");
            
            if (groupId == null || studentId == null) {
                sendError(session, "加入群组参数不完整");
                return;
            }
            
            // 添加到群成员
            groupMembers.computeIfAbsent(groupId, k -> new HashSet<>()).add(studentId);
            
            // 保存到数据库
            chatService.saveGroupMember(groupId, studentId);
            
            // 发送确认
            sendToUser(session, new JSONObject()
                .fluentPut("type", "JOIN_GROUP_SUCCESS")
                .fluentPut("groupId", groupId));
            
            // 发送群组历史记录 (最近20条)
            sendGroupHistory(session, groupId);
                
        } catch (Exception e) {
            log.error("加入群组失败", e);
            sendError(session, "加入群组失败");
        }
    }
    
    private void handleLeaveGroup(WebSocketSession session, JSONObject json) {
        try {
            String groupId = json.getString("groupId");
            Integer studentId = json.getInteger("studentId");
            
            if (groupId == null || studentId == null) {
                sendError(session, "离开群组参数不完整");
                return;
            }
            
            // 从群成员中移除
            Set<Integer> members = groupMembers.get(groupId);
            if (members != null) {
                members.remove(studentId);
            }
            
            // 从数据库删除
            chatService.removeGroupMember(groupId, studentId);
            
            // 发送确认
            sendToUser(session, new JSONObject()
                .fluentPut("type", "LEAVE_GROUP_SUCCESS")
                .fluentPut("groupId", groupId));
                
        } catch (Exception e) {
            log.error("离开群组失败", e);
            sendError(session, "离开群组失败");
        }
    }
    
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        try {
            Integer studentId = getStudentIdFromSession(session);
            if (studentId != null) {
                onlineUsers.remove(studentId);
                log.info("用户 {} 断开WebSocket连接", studentId);
            }
        } catch (Exception e) {
            log.error("处理WebSocket连接关闭失败", e);
        }
    }
    
    // 辅助方法
    private void sendToUser(WebSocketSession session, JSONObject message) {
        try {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(message.toJSONString()));
            }
        } catch (IOException e) {
            log.error("发送WebSocket消息失败", e);
        }
    }
    
    private void sendError(WebSocketSession session, String errorMessage) {
        JSONObject error = new JSONObject()
            .fluentPut("type", "ERROR")
            .fluentPut("message", errorMessage);
        sendToUser(session, error);
    }
    
    private Integer getStudentIdFromSession(WebSocketSession session) {
        try {
            String query = session.getUri().getQuery();
            if (query != null && query.contains("studentId=")) {
                String[] parts = query.split("studentId=");
                if (parts.length > 1) {
                    String idStr = parts[1].split("&")[0]; // 处理多个参数的情况
                    return Integer.parseInt(idStr);
                }
            }
        } catch (Exception e) {
            log.error("解析studentId失败", e);
        }
        return null;
    }
    
    // 从数据库加载用户已加入的群组
    private void loadUserGroups(Integer studentId) {
        try {
            List<String> userGroups = chatService.getUserGroups(studentId);
            for (String groupId : userGroups) {
                groupMembers.computeIfAbsent(groupId, k -> new HashSet<>()).add(studentId);
            }
        } catch (Exception e) {
            log.error("加载用户群组失败", e);
        }
    }
    
    // 发送群组历史记录
    private void sendGroupHistory(WebSocketSession session, String groupId) {
        try {
            // 获取最近20条历史记录
            List<ChatMessage> historyMessages = chatService.getGroupMessagesWithSenderNames(groupId, 20, 0);
            
            if (!historyMessages.isEmpty()) {
                // 发送历史记录标识
                sendToUser(session, new JSONObject()
                    .fluentPut("type", "GROUP_HISTORY_START")
                    .fluentPut("groupId", groupId)
                    .fluentPut("count", historyMessages.size()));
                
                // 逐条发送历史消息
                for (ChatMessage message : historyMessages) {
                    JSONObject historyMsg = new JSONObject()
                        .fluentPut("type", "GROUP_HISTORY_MESSAGE")
                        .fluentPut("groupId", groupId)
                        .fluentPut("senderId", message.getSenderId())
                        .fluentPut("senderName", message.getSenderName())
                        .fluentPut("content", message.getContent())
                        .fluentPut("timestamp", message.getSentAt())
                        .fluentPut("isHistory", true);
                    sendToUser(session, historyMsg);
                }
                
                // 发送历史记录结束标识
                sendToUser(session, new JSONObject()
                    .fluentPut("type", "GROUP_HISTORY_END")
                    .fluentPut("groupId", groupId));
            }
        } catch (Exception e) {
            log.error("发送群组历史记录失败", e);
        }
    }
    
    // 发送私聊历史记录
    private void sendPrivateHistory(WebSocketSession session, Integer senderId, Integer targetId) {
        try {
            // 获取最近20条历史记录
            List<ChatMessage> historyMessages = chatService.getPrivateMessagesWithSenderNames(senderId, targetId, 20, 0);
            
            if (!historyMessages.isEmpty()) {
                // 发送历史记录标识
                sendToUser(session, new JSONObject()
                    .fluentPut("type", "PRIVATE_HISTORY_START")
                    .fluentPut("targetId", targetId)
                    .fluentPut("count", historyMessages.size()));
                
                // 逐条发送历史消息
                for (ChatMessage message : historyMessages) {
                    // 判断消息的发送者和接收者关系
                    boolean isSender = message.getSenderId().equals(senderId);
                    
                    JSONObject historyMsg = new JSONObject()
                        .fluentPut("type", "PRIVATE_HISTORY_MESSAGE")
                        .fluentPut("senderId", message.getSenderId())
                        .fluentPut("senderName", message.getSenderName())
                        .fluentPut("content", message.getContent())
                        .fluentPut("timestamp", message.getSentAt())
                        .fluentPut("targetId", isSender ? message.getTargetId() : message.getSenderId())
                        .fluentPut("isSender", isSender)
                        .fluentPut("isHistory", true);
                    sendToUser(session, historyMsg);
                }
                
                // 发送历史记录结束标识
                sendToUser(session, new JSONObject()
                    .fluentPut("type", "PRIVATE_HISTORY_END")
                    .fluentPut("targetId", targetId));
            }
        } catch (Exception e) {
            log.error("发送私聊历史记录失败", e);
        }
    }
} 