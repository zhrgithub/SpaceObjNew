package com.spaceobj.spaceobj.mapper;

import com.spaceobj.spaceobj.pojo.ChatMessage;
import com.spaceobj.spaceobj.pojo.GroupMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 聊天数据访问层接口
 * 
 * 提供聊天相关的数据库操作，包括：
 * - 消息的增删改查
 * - 群组成员管理
 * - 历史记录查询
 * 
 * 使用MyBatis框架进行数据库操作
 * 
 * @author SpaceObj Team
 * @version 1.0
 */
@Mapper
public interface ChatMapper {
    
    // ==================== 消息相关操作 ====================
    
    /**
     * 插入聊天消息到数据库
     * 
     * @param message 聊天消息对象，包含消息类型、发送者、接收者、内容等信息
     */
    void insertMessage(ChatMessage message);
    
    /**
     * 获取两个用户之间的私聊消息
     * 
     * @param userId1 用户1的ID
     * @param userId2 用户2的ID
     * @param limit 限制返回的消息数量
     * @param offset 偏移量，用于分页查询
     * @return 私聊消息列表，按时间倒序排列
     */
    List<ChatMessage> getPrivateMessages(@Param("userId1") Integer userId1, 
                                       @Param("userId2") Integer userId2,
                                       @Param("limit") int limit, 
                                       @Param("offset") int offset);
    
    /**
     * 获取指定群组的聊天消息
     * 
     * @param groupId 群组ID
     * @param limit 限制返回的消息数量
     * @param offset 偏移量，用于分页查询
     * @return 群聊消息列表，按时间倒序排列
     */
    List<ChatMessage> getGroupMessages(@Param("groupId") String groupId,
                                     @Param("limit") int limit, 
                                     @Param("offset") int offset);
    
    // ==================== 群组成员管理 ====================
    
    /**
     * 添加群组成员
     * 
     * @param member 群组成员对象，包含群组ID、学生ID、加入时间等信息
     */
    void insertGroupMember(GroupMember member);
    
    /**
     * 删除群组成员
     * 
     * @param groupId 群组ID
     * @param studentId 学生ID
     */
    void deleteGroupMember(@Param("groupId") String groupId, 
                          @Param("studentId") Integer studentId);
    
    /**
     * 检查用户是否为群组成员
     * 
     * @param groupId 群组ID
     * @param studentId 学生ID
     * @return true表示是群组成员，false表示不是
     */
    boolean isGroupMember(@Param("groupId") String groupId, 
                         @Param("studentId") Integer studentId);
    
    /**
     * 获取用户加入的所有群组
     * 
     * @param studentId 学生ID
     * @return 用户加入的群组ID列表
     */
    List<String> getUserGroups(@Param("studentId") Integer studentId);
    
    /**
     * 获取指定群组的所有成员
     * 
     * @param groupId 群组ID
     * @return 群组成员列表，包含成员信息和加入时间
     */
    List<GroupMember> getGroupMembers(@Param("groupId") String groupId);
    
    /**
     * 获取系统中所有群组ID列表
     * 
     * 查询所有存在的群组ID，用于前端群组选择
     * 
     * @return 所有群组ID的列表，去重并按字母顺序排序
     */
    List<String> getAllGroups();
} 