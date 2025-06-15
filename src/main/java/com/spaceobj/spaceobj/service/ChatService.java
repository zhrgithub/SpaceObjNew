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

@Service
public class ChatService {
    
    @Autowired
    private ChatMapper chatMapper;  // MyBatis Mapper
    
    @Autowired
    private StudentMapper studentMapper;  // 学生Mapper
    
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
    
    // 获取群组历史记录并填充发送者姓名
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
                    message.setSenderName("用户" + message.getSenderId());
                }
            }
        }
        
        return messages;
    }
    
    // 获取私聊历史记录并填充发送者姓名
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
                    message.setSenderName("用户" + message.getSenderId());
                }
            }
        }
        
        return messages;
    }
    
    // 获取所有群组ID列表
    public List<String> getAllGroups() {
        return chatMapper.getAllGroups();
    }
    
    // 获取所有用户列表（用于私聊选择）
    public List<Student> getAllUsers() {
        return studentMapper.findAll();
    }
} 