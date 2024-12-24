package com.spaceobj.spaceobj.service;


import com.spaceobj.spaceobj.mapper.StudentMapper;
import com.spaceobj.spaceobj.pojo.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    @Autowired
    private StudentMapper studentMapper;

    public Student getStudentById(Long studentId) {
        return studentMapper.findById(studentId);
    }

    public List<Student> getAllStudents() {
        return studentMapper.findAll();
    }
}
