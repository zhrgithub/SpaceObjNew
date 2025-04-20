package com.spaceobj.spaceobj.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

// 学生实体类，使用Lombok简化getter/setter
@Data
public class Student implements Serializable {
    // 学生ID
    private Long studentId;
    // 名字
    private String firstName;
    // 姓氏
    private String lastName;

    // 生日，指定JSON日期格式
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthdate;
    // 电子邮件
    private String email;

    // 创建时间，指定JSON日期时间格式
    @JsonFormat(pattern = "yyyy-MM-dd HH:ss:mm")
    private LocalDateTime createdAt;

    // 更新时间，指定JSON日期时间格式
    @JsonFormat(pattern = "yyyy-MM-dd HH:ss:mm")
    private LocalDateTime updatedAt;



}
