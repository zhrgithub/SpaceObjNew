package com.spaceobj.spaceobj.service;

import com.spaceobj.spaceobj.mapper.ChatMapper;
import com.spaceobj.spaceobj.mapper.StudentMapper;
import com.spaceobj.spaceobj.pojo.ChatMessage;
import com.spaceobj.spaceobj.pojo.GroupMember;
import com.spaceobj.spaceobj.pojo.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 聊天服务类
 * 
 * 提供聊天相关的业务逻辑处理，包括：
 * - 消息存储和查询
 * - 群组成员管理
 * - 历史记录获取
 * - 用户和群组列表获取
 * 
 * @author SpaceObj Team
 * @version 1.0
 */
@Service
public class ChatService {
    
    /**
     * 聊天数据访问层，处理消息和群组相关的数据库操作
     */
    @Autowired
    private ChatMapper chatMapper;
    
    /**
     * 学生数据访问层，处理用户信息相关的数据库操作
     */
    @Autowired
    private StudentMapper studentMapper;
    
    /**
     * 保存聊天消息到数据库
     * 
     * @param type 消息类型 (PRIVATE/GROUP)
     * @param senderId 发送者ID
     * @param targetId 目标用户ID (私聊时使用)
     * @param groupId 群组ID (群聊时使用)
     * @param content 消息内容
     */
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
    
    /**
     * 添加群组成员
     * 
     * 检查用户是否已经是群组成员，如果不是则添加
     * 
     * @param groupId 群组ID
     * @param studentId 学生ID
     */
    public void saveGroupMember(String groupId, Integer studentId) {
        // 检查是否已存在，避免重复添加
        if (!chatMapper.isGroupMember(groupId, studentId)) {
            GroupMember member = new GroupMember();
            member.setGroupId(groupId);
            member.setStudentId(studentId);
            member.setJoinedAt(new Date());
            
            chatMapper.insertGroupMember(member);
        }
    }
    
    /**
     * 移除群组成员
     * 
     * @param groupId 群组ID
     * @param studentId 学生ID
     */
    public void removeGroupMember(String groupId, Integer studentId) {
        chatMapper.deleteGroupMember(groupId, studentId);
    }
    
    /**
     * 获取用户加入的所有群组
     * 
     * @param studentId 学生ID
     * @return 群组ID列表
     */
    public List<String> getUserGroups(Integer studentId) {
        return chatMapper.getUserGroups(studentId);
    }
    
    /**
     * 获取私聊消息记录
     * 
     * @param userId1 用户1的ID
     * @param userId2 用户2的ID
     * @param limit 限制返回的消息数量
     * @param offset 偏移量，用于分页
     * @return 私聊消息列表
     */
    public List<ChatMessage> getPrivateMessages(Integer userId1, Integer userId2, int limit, int offset) {
        return chatMapper.getPrivateMessages(userId1, userId2, limit, offset);
    }
    
    /**
     * 获取群聊消息记录
     * 
     * @param groupId 群组ID
     * @param limit 限制返回的消息数量
     * @param offset 偏移量，用于分页
     * @return 群聊消息列表
     */
    public List<ChatMessage> getGroupMessages(String groupId, int limit, int offset) {
        return chatMapper.getGroupMessages(groupId, limit, offset);
    }
    
    /**
     * 获取群组历史记录并填充发送者姓名
     * 
     * 从数据库获取群聊消息，并为每条消息填充发送者的真实姓名
     * 如果无法获取姓名，则使用"用户ID"作为显示名
     * 
     * @param groupId 群组ID
     * @param limit 限制返回的消息数量
     * @param offset 偏移量，用于分页
     * @return 包含发送者姓名的群聊消息列表
     */
    public List<ChatMessage> getGroupMessagesWithSenderNames(String groupId, int limit, int offset) {
        List<ChatMessage> messages = chatMapper.getGroupMessages(groupId, limit, offset);
        
        // 为每条消息填充发送者姓名
        for (ChatMessage message : messages) {
            if (message.getSenderId() != null) {
                try {
                    Student student = studentMapper.findById(message.getSenderId().longValue());
                    if (student != null) {
                        // 组合姓和名
                        String fullName = (student.getFirstName() != null ? student.getFirstName() : "") + 
                                         (student.getLastName() != null ? student.getLastName() : "");
                        message.setSenderName(fullName.trim());
                        
                        // 如果姓名为空，使用学生ID作为显示名
                        if (message.getSenderName().isEmpty()) {
                            message.setSenderName("用户" + message.getSenderId());
                        }
                    } else {
                        message.setSenderName("用户" + message.getSenderId());
                    }
                } catch (Exception e) {
                    // 异常情况下使用默认显示名
                    message.setSenderName("用户" + message.getSenderId());
                }
            }
        }
        
        return messages;
    }
    
    /**
     * 获取私聊历史记录并填充发送者姓名
     * 
     * 从数据库获取私聊消息，并为每条消息填充发送者的真实姓名
     * 如果无法获取姓名，则使用"用户ID"作为显示名
     * 
     * @param userId1 用户1的ID
     * @param userId2 用户2的ID
     * @param limit 限制返回的消息数量
     * @param offset 偏移量，用于分页
     * @return 包含发送者姓名的私聊消息列表
     */
    public List<ChatMessage> getPrivateMessagesWithSenderNames(Integer userId1, Integer userId2, int limit, int offset) {
        List<ChatMessage> messages = chatMapper.getPrivateMessages(userId1, userId2, limit, offset);
        
        // 为每条消息填充发送者姓名
        for (ChatMessage message : messages) {
            if (message.getSenderId() != null) {
                try {
                    Student student = studentMapper.findById(message.getSenderId().longValue());
                    if (student != null) {
                        // 组合姓和名
                        String fullName = (student.getFirstName() != null ? student.getFirstName() : "") + 
                                         (student.getLastName() != null ? student.getLastName() : "");
                        message.setSenderName(fullName.trim());
                        
                        // 如果姓名为空，使用学生ID作为显示名
                        if (message.getSenderName().isEmpty()) {
                            message.setSenderName("用户" + message.getSenderId());
                        }
                    } else {
                        message.setSenderName("用户" + message.getSenderId());
                    }
                } catch (Exception e) {
                    // 异常情况下使用默认显示名
                    message.setSenderName("用户" + message.getSenderId());
                }
            }
        }
        
        return messages;
    }
    
    /**
     * 获取所有群组ID列表
     * 
     * 从数据库查询所有存在的群组ID，用于前端群组选择
     * 
     * @return 群组ID列表
     */
    public List<String> getAllGroups() {
        return chatMapper.getAllGroups();
    }
    
    /**
     * 获取所有用户列表
     * 
     * 从数据库查询所有用户信息，用于前端用户选择（私聊对象选择）
     * 
     * @return 用户信息列表
     */
    public List<Student> getAllUsers() {
        return studentMapper.findAll();
    }
} 