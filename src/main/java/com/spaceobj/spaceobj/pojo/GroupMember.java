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