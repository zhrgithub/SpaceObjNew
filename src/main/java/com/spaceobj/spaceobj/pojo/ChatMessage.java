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