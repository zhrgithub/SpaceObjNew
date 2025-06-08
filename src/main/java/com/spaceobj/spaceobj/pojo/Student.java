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


    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
