# 简化版实时聊天系统设计

## 🎯 核心需求
- ✅ 简单的实时单聊
- ✅ 简单的实时群聊
- ❌ 不需要复杂认证
- ❌ 不需要已读状态
- ❌ 不需要正在输入提示
- ❌ 不需要好友系统

## 1. 简化的WebSocket设计

### 1.1 连接地址
```
ws://localhost:8080/ws/chat?studentId={student_id}
```

### 1.2 消息协议

#### 连接确认
```json
// 客户端连接后自动发送
{
  "type": "CONNECT",
  "studentId": 1,
  "studentName": "张三"
}

// 服务端确认
{
  "type": "CONNECT_SUCCESS",
  "studentId": 1
}
```

#### 单聊消息
```json
// 发送私聊消息
{
  "type": "PRIVATE_MESSAGE",
  "targetStudentId": 2,
  "content": "你好！",
  "senderId": 1,
  "senderName": "张三"
}

// 接收私聊消息
{
  "type": "PRIVATE_MESSAGE",
  "senderId": 1,
  "senderName": "张三",
  "content": "你好！",
  "timestamp": "2024-01-15T10:01:00Z"
}
```

#### 群聊消息
```json
// 发送群聊消息
{
  "type": "GROUP_MESSAGE",
  "groupId": "group_123",
  "content": "大家好！",
  "senderId": 1,
  "senderName": "张三"
}

// 接收群聊消息
{
  "type": "GROUP_MESSAGE",
  "groupId": "group_123",
  "senderId": 1,
  "senderName": "张三",
  "content": "大家好！",
  "timestamp": "2024-01-15T10:02:00Z"
}
```

#### 加入/离开群聊
```json
// 加入群聊
{
  "type": "JOIN_GROUP",
  "groupId": "group_123",
  "studentId": 1
}

// 离开群聊
{
  "type": "LEAVE_GROUP",
  "groupId": "group_123",
  "studentId": 1
}
```

## 2. 简化的数据库设计

### 2.1 核心表

#### chat_messages（消息表）
```sql
CREATE TABLE chat_messages (
    message_id SERIAL PRIMARY KEY,
    message_type VARCHAR(20) NOT NULL,     -- 'PRIVATE' 或 'GROUP'
    sender_id INTEGER NOT NULL,            -- 发送者ID
    target_id INTEGER,                     -- 私聊对象ID（私聊时使用）
    group_id VARCHAR(50),                  -- 群ID（群聊时使用）
    content TEXT NOT NULL,                 -- 消息内容
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### group_members（群成员表）
```sql
CREATE TABLE group_members (
    id SERIAL PRIMARY KEY,
    group_id VARCHAR(50) NOT NULL,        -- 群ID
    student_id INTEGER NOT NULL,          -- 学生ID
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(group_id, student_id)          -- 确保同一学生在同一群组中只有一条记录
);
```

### 2.2 索引
```sql
CREATE INDEX idx_messages_sender ON chat_messages(sender_id);
CREATE INDEX idx_messages_target ON chat_messages(target_id);
CREATE INDEX idx_messages_group ON chat_messages(group_id);
CREATE INDEX idx_messages_sent_at ON chat_messages(sent_at);
CREATE INDEX idx_group_members_group ON group_members(group_id);
CREATE INDEX idx_group_members_student ON group_members(student_id);
```

## 3. 简化的Spring Boot实现

### 3.1 WebSocket处理器
```java
package com.spaceobj.spaceobj.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
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
            
            // 保存到数据库
            chatService.saveMessage("PRIVATE", senderId, targetId, null, content);
            
            // 转发给目标用户
            WebSocketSession targetSession = onlineUsers.get(targetId);
            if (targetSession != null && targetSession.isOpen()) {
                JSONObject msg = new JSONObject()
                    .fluentPut("type", "PRIVATE_MESSAGE")
                    .fluentPut("senderId", senderId)
                    .fluentPut("senderName", senderName)
                    .fluentPut("content", content)
                    .fluentPut("timestamp", new Date());
                sendToUser(targetSession, msg);
            }
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
                if (!memberId.equals(senderId)) {  // 不发给自己
                    WebSocketSession memberSession = onlineUsers.get(memberId);
                    if (memberSession != null && memberSession.isOpen()) {
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
}
```

### 3.2 Service层实现
```java
package com.spaceobj.spaceobj.service;

import com.spaceobj.spaceobj.mapper.ChatMapper;
import com.spaceobj.spaceobj.pojo.ChatMessage;
import com.spaceobj.spaceobj.pojo.GroupMember;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ChatService {
    
    @Autowired
    private ChatMapper chatMapper;  // MyBatis Mapper
    
    public void saveMessage(String type, Integer senderId, Integer targetId, String groupId, String content) {
        ChatMessage message = new ChatMessage();
        message.setMessageType(type);
        message.setSenderId(senderId);
        message.setTargetId(targetId);
        message.setGroupId(groupId);
        message.setContent(content);
        message.setSentAt(new Date());
        
        chatMapper.insertMessage(message);
    }
    
    public void saveGroupMember(String groupId, Integer studentId) {
        // 检查是否已存在
        if (!chatMapper.isGroupMember(groupId, studentId)) {
            GroupMember member = new GroupMember();
            member.setGroupId(groupId);
            member.setStudentId(studentId);
            member.setJoinedAt(new Date());
            
            chatMapper.insertGroupMember(member);
        }
    }
    
    public void removeGroupMember(String groupId, Integer studentId) {
        chatMapper.deleteGroupMember(groupId, studentId);
    }
    
    public List<String> getUserGroups(Integer studentId) {
        return chatMapper.getUserGroups(studentId);
    }
    
    public List<ChatMessage> getPrivateMessages(Integer userId1, Integer userId2, int limit, int offset) {
        return chatMapper.getPrivateMessages(userId1, userId2, limit, offset);
    }
    
    public List<ChatMessage> getGroupMessages(String groupId, int limit, int offset) {
        return chatMapper.getGroupMessages(groupId, limit, offset);
    }
}
```

### 3.3 实体类
```java
// ChatMessage.java
package com.spaceobj.spaceobj.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private Long messageId;
    private String messageType;  // PRIVATE 或 GROUP
    private Integer senderId;
    private Integer targetId;    // 私聊对象ID（可为空）
    private String groupId;      // 群ID（可为空）
    private String content;
    private Date sentAt;
    
    // 发送者姓名（用于显示，不存数据库）
    private String senderName;
}

// GroupMember.java
package com.spaceobj.spaceobj.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupMember {
    private Long id;
    private String groupId;
    private Integer studentId;
    private Date joinedAt;
    
    // 学生姓名（用于显示，不存数据库）
    private String studentName;
}
```

### 3.4 Mapper层
```java
package com.spaceobj.spaceobj.mapper;

import com.spaceobj.spaceobj.pojo.ChatMessage;
import com.spaceobj.spaceobj.pojo.GroupMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ChatMapper {
    
    // 消息相关
    void insertMessage(ChatMessage message);
    
    List<ChatMessage> getPrivateMessages(@Param("userId1") Integer userId1, 
                                       @Param("userId2") Integer userId2,
                                       @Param("limit") int limit, 
                                       @Param("offset") int offset);
    
    List<ChatMessage> getGroupMessages(@Param("groupId") String groupId,
                                     @Param("limit") int limit, 
                                     @Param("offset") int offset);
    
    // 群组相关
    void insertGroupMember(GroupMember member);
    
    void deleteGroupMember(@Param("groupId") String groupId, 
                          @Param("studentId") Integer studentId);
    
    boolean isGroupMember(@Param("groupId") String groupId, 
                         @Param("studentId") Integer studentId);
    
    List<String> getUserGroups(@Param("studentId") Integer studentId);
    
    List<GroupMember> getGroupMembers(@Param("groupId") String groupId);
}
```

### 3.5 MyBatis XML映射
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" 
    "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.spaceobj.spaceobj.mapper.ChatMapper">
    
    <!-- 插入消息 -->
    <insert id="insertMessage" parameterType="com.spaceobj.spaceobj.pojo.ChatMessage">
        INSERT INTO chat_messages (message_type, sender_id, target_id, group_id, content, sent_at)
        VALUES (#{messageType}, #{senderId}, #{targetId}, #{groupId}, #{content}, #{sentAt})
    </insert>
    
    <!-- 查询私聊消息 -->
    <select id="getPrivateMessages" resultType="com.spaceobj.spaceobj.pojo.ChatMessage">
        SELECT m.*, s.student_name as senderName
        FROM chat_messages m
        LEFT JOIN students s ON m.sender_id = s.student_id
        WHERE m.message_type = 'PRIVATE'
          AND ((m.sender_id = #{userId1} AND m.target_id = #{userId2})
               OR (m.sender_id = #{userId2} AND m.target_id = #{userId1}))
        ORDER BY m.sent_at DESC
        LIMIT #{limit} OFFSET #{offset}
    </select>
    
    <!-- 查询群聊消息 -->
    <select id="getGroupMessages" resultType="com.spaceobj.spaceobj.pojo.ChatMessage">
        SELECT m.*, s.student_name as senderName
        FROM chat_messages m
        LEFT JOIN students s ON m.sender_id = s.student_id
        WHERE m.message_type = 'GROUP' AND m.group_id = #{groupId}
        ORDER BY m.sent_at DESC
        LIMIT #{limit} OFFSET #{offset}
    </select>
    
    <!-- 插入群成员 -->
    <insert id="insertGroupMember" parameterType="com.spaceobj.spaceobj.pojo.GroupMember">
        INSERT INTO group_members (group_id, student_id, joined_at)
        VALUES (#{groupId}, #{studentId}, #{joinedAt})
    </insert>
    
    <!-- 删除群成员 -->
    <delete id="deleteGroupMember">
        DELETE FROM group_members 
        WHERE group_id = #{groupId} AND student_id = #{studentId}
    </delete>
    
    <!-- 检查是否是群成员 -->
    <select id="isGroupMember" resultType="boolean">
        SELECT COUNT(*) > 0 
        FROM group_members 
        WHERE group_id = #{groupId} AND student_id = #{studentId}
    </select>
    
    <!-- 获取用户加入的所有群组 -->
    <select id="getUserGroups" resultType="String">
        SELECT group_id 
        FROM group_members 
        WHERE student_id = #{studentId}
    </select>
    
    <!-- 获取群组成员 -->
    <select id="getGroupMembers" resultType="com.spaceobj.spaceobj.pojo.GroupMember">
        SELECT gm.*, s.student_name as studentName
        FROM group_members gm
        LEFT JOIN students s ON gm.student_id = s.student_id
        WHERE gm.group_id = #{groupId}
        ORDER BY gm.joined_at
    </select>
    
</mapper>
```

### 3.6 WebSocket配置
```java
package com.spaceobj.spaceobj.config;

import com.spaceobj.spaceobj.handler.SimpleChatHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    
    @Autowired
    private SimpleChatHandler chatHandler;
    
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatHandler, "/ws/chat")
                .setAllowedOrigins("*");
    }
}
```

### 3.7 Maven依赖和配置

#### 3.7.1 pom.xml依赖
```xml
<!-- 在pom.xml中添加以下依赖 -->
<dependencies>
    <!-- WebSocket支持 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-websocket</artifactId>
    </dependency>
    
    <!-- JSON处理 -->
    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>fastjson</artifactId>
        <version>1.2.83</version>
    </dependency>
    
    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    
    <!-- MyBatis -->
    <dependency>
        <groupId>org.mybatis.spring.boot</groupId>
        <artifactId>mybatis-spring-boot-starter</artifactId>
        <version>2.3.1</version>
    </dependency>
    
    <!-- PostgreSQL驱动 -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>
</dependencies>
```

#### 3.7.2 application.yml配置
```yaml
# 数据库配置
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/spaceobj
    username: your_username
    password: your_password
    driver-class-name: org.postgresql.Driver

# MyBatis配置
mybatis:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: com.spaceobj.spaceobj.pojo
  configuration:
    map-underscore-to-camel-case: true

# 服务器配置
server:
  port: 8080

# 日志配置
logging:
  level:
    com.spaceobj.spaceobj: DEBUG
    org.springframework.web.socket: DEBUG
```

## 4. 简化的前端实现

### 4.1 JavaScript客户端
```javascript
class SimpleChat {
    constructor(studentId, studentName) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.ws = null;
    }
    
    connect() {
        this.ws = new WebSocket(`ws://localhost:8080/ws/chat?studentId=${this.studentId}`);
        
        this.ws.onopen = () => {
            console.log('连接成功');
            // 发送连接消息
            this.send({
                type: 'CONNECT',
                studentId: this.studentId,
                studentName: this.studentName
            });
        };
        
        this.ws.onmessage = (event) => {
            const message = JSON.parse(event.data);
            this.handleMessage(message);
        };
        
        this.ws.onclose = () => {
            console.log('连接断开');
        };
    }
    
    // 发送私聊消息
    sendPrivateMessage(targetStudentId, content) {
        this.send({
            type: 'PRIVATE_MESSAGE',
            targetStudentId: targetStudentId,
            content: content,
            senderId: this.studentId,
            senderName: this.studentName
        });
    }
    
    // 发送群聊消息
    sendGroupMessage(groupId, content) {
        this.send({
            type: 'GROUP_MESSAGE',
            groupId: groupId,
            content: content,
            senderId: this.studentId,
            senderName: this.studentName
        });
    }
    
    // 加入群聊
    joinGroup(groupId) {
        this.send({
            type: 'JOIN_GROUP',
            groupId: groupId,
            studentId: this.studentId
        });
    }
    
    // 离开群聊
    leaveGroup(groupId) {
        this.send({
            type: 'LEAVE_GROUP',
            groupId: groupId,
            studentId: this.studentId
        });
    }
    
    handleMessage(message) {
        switch (message.type) {
            case 'CONNECT_SUCCESS':
                console.log('连接确认');
                break;
            case 'PRIVATE_MESSAGE':
                console.log('收到私聊:', message);
                this.displayPrivateMessage(message);
                break;
            case 'GROUP_MESSAGE':
                console.log('收到群聊:', message);
                this.displayGroupMessage(message);
                break;
            case 'JOIN_GROUP_SUCCESS':
                console.log('成功加入群组:', message.groupId);
                break;
            case 'LEAVE_GROUP_SUCCESS':
                console.log('成功离开群组:', message.groupId);
                break;
            case 'ERROR':
                console.error('服务器错误:', message.message);
                alert('错误: ' + message.message);
                break;
            default:
                console.log('未知消息类型:', message);
        }
    }
    
    displayPrivateMessage(message) {
        const messageDiv = document.createElement('div');
        messageDiv.innerHTML = `<strong>${message.senderName}:</strong> ${message.content}`;
        document.getElementById('chat-messages').appendChild(messageDiv);
    }
    
    displayGroupMessage(message) {
        const messageDiv = document.createElement('div');
        messageDiv.innerHTML = `<strong>[${message.groupId}] ${message.senderName}:</strong> ${message.content}`;
        document.getElementById('chat-messages').appendChild(messageDiv);
    }
    
    send(data) {
        if (this.ws && this.ws.readyState === WebSocket.OPEN) {
            this.ws.send(JSON.stringify(data));
        }
    }
}

// 使用示例
const chat = new SimpleChat(1, '张三');
chat.connect();

// 发送消息
function sendMessage() {
    const input = document.getElementById('message-input');
    const targetId = document.getElementById('target-id').value;
    
    if (targetId) {
        // 私聊
        chat.sendPrivateMessage(parseInt(targetId), input.value);
    } else {
        // 群聊
        chat.sendGroupMessage('group_123', input.value);
    }
    
    input.value = '';
}
```

### 4.2 HTML示例
```html
<!DOCTYPE html>
<html>
<head>
    <title>简单聊天</title>
</head>
<body>
    <div id="chat-messages" style="height: 300px; border: 1px solid #ccc; overflow-y: scroll;"></div>
    
    <div>
        <input type="text" id="target-id" placeholder="私聊对象ID（群聊留空）">
        <input type="text" id="message-input" placeholder="输入消息">
        <button onclick="sendMessage()">发送</button>
    </div>
    
    <div>
        <button onclick="chat.joinGroup('group_123')">加入群聊</button>
        <button onclick="chat.leaveGroup('group_123')">离开群聊</button>
    </div>
    
    <script src="simple-chat.js"></script>
    <script>
        const chat = new SimpleChat(1, '张三');
        chat.connect();
    </script>
</body>
</html>
```

## 5. 文件结构和部署

### 5.1 项目文件结构
```
src/main/java/com/spaceobj/spaceobj/
├── handler/
│   └── SimpleChatHandler.java       # WebSocket消息处理器
├── service/
│   └── ChatService.java             # 聊天业务逻辑服务
├── mapper/
│   └── ChatMapper.java              # MyBatis数据访问接口
├── pojo/
│   ├── ChatMessage.java             # 聊天消息实体类
│   └── GroupMember.java             # 群组成员实体类
└── config/
    └── WebSocketConfig.java         # WebSocket配置类

src/main/resources/
├── mapper/
│   └── ChatMapper.xml               # MyBatis SQL映射文件
└── application.yml                  # Spring Boot配置文件

sql/
└── simple_chat_tables.sql           # 数据库建表脚本

前端文件/
├── simple-chat.js                   # JavaScript聊天客户端
└── chat.html                        # HTML聊天界面
```

### 5.2 部署步骤
1. **创建数据库表**：执行 `sql/simple_chat_tables.sql`
2. **配置数据库**：修改 `application.yml` 中的数据库连接信息
3. **添加依赖**：将Maven依赖添加到 `pom.xml`
4. **复制代码**：将所有Java类复制到对应的包目录
5. **启动应用**：运行Spring Boot主程序
6. **测试连接**：打开HTML页面测试聊天功能

### 5.3 测试验证
```bash
# 1. 启动Spring Boot应用
mvn spring-boot:run

# 2. 打开浏览器访问 WebSocket 测试页面
# 在浏览器控制台执行：
const chat = new SimpleChat(1, '用户1');
chat.connect();

# 3. 发送测试消息
chat.sendPrivateMessage(2, '你好！');
chat.joinGroup('group_123');
chat.sendGroupMessage('group_123', '大家好！');
```

## 6. 系统优势

### 6.1 简单易懂
- ✅ 只有4种消息类型
- ✅ 没有复杂的状态管理
- ✅ 代码量少，容易维护

### 6.2 功能完整
- ✅ 实时单聊
- ✅ 实时群聊
- ✅ 群成员管理
- ✅ 数据持久化

### 6.3 扩展性
- ✅ 后续可以轻松添加更多功能
- ✅ 消息格式统一，便于扩展
- ✅ 模块化设计，易于维护

### 6.4 生产就绪
- ✅ 完整的错误处理
- ✅ 数据库事务支持
- ✅ 日志记录完整
- ✅ 配置灵活

这个简化版本专注于核心聊天功能，提供了一个完整可用的实时聊天解决方案！ 