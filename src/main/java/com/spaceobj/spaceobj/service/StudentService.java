package com.spaceobj.spaceobj.service;

import com.spaceobj.spaceobj.pojo.Student;
import java.util.List;
import java.util.Map;

public interface StudentService {
    Student getStudentById(Long studentId);
    List<Student> getAllStudents();
    Map<String, Object> getStudentsByPage(Integer pageNum, Integer pageSize, String name, Integer age);
}
