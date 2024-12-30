package com.spaceobj.spaceobj.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
public class Student implements Serializable {
    private Long studentId;
    private String firstName;
    private String lastName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthdate;
    private String email;

    @JsonFormat(pattern = "yyyy-MM-dd HH:ss:mm")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:ss:mm")
    private LocalDateTime updatedAt;
}
