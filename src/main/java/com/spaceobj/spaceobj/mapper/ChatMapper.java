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
    
    // 获取所有群组ID列表
    List<String> getAllGroups();
} 