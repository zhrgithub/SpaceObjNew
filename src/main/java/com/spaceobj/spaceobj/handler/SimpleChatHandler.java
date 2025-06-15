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

@Slf4j
@Component
public class SimpleChatHandler extends TextWebSocketHandler {
    
    @Autowired
    private ChatService chatService;  // 注入Service层
    
    // 在线用户 Map<studentId, WebSocketSession>
    private final Map<Integer, WebSocketSession> onlineUsers = new ConcurrentHashMap<>();
    
    // 群成员 Map<groupId, Set<studentId>>
    private final Map<String, Set<Integer>> groupMembers = new ConcurrentHashMap<>();
    
    // 记录已经发送过私聊历史的对话 Map<studentId, Set<targetId>>
    private final Map<Integer, Set<Integer>> privateChatHistorySent = new ConcurrentHashMap<>();
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        try {
            // 从URL参数获取studentId
            Integer studentId = getStudentIdFromSession(session);
            if (studentId == null) {
                session.close();
                return;
            }
            
            onlineUsers.put(studentId, session);
            
            // 从数据库加载用户已加入的群组
            loadUserGroups(studentId);
            
            // 发送连接确认
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
    
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            JSONObject json = JSON.parseObject(message.getPayload());
            String type = json.getString("type");
            
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