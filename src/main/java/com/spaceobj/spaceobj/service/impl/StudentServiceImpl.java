package com.spaceobj.spaceobj.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.spaceobj.spaceobj.pojo.Student;
import com.spaceobj.spaceobj.mapper.StudentMapper;
import com.spaceobj.spaceobj.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StudentServiceImpl implements StudentService {
    
    @Autowired
    private StudentMapper studentMapper;

    @Override
    public Student getStudentById(Long studentId) {
        return studentMapper.findById(studentId);
    }

    @Override
    public List<Student> getAllStudents() {
        return studentMapper.findAll();
    }

    @Override
    public Map<String, Object> getStudentsByPage(Integer pageNum, Integer pageSize, String name, Integer age) {
        Map<String, Object> result = new HashMap<>();
        
        // 设置分页参数
        PageHelper.startPage(pageNum, pageSize);
        
        // 执行查询，这里需要在 StudentMapper 中添加对应的方法
        List<Student> students = studentMapper.findByCondition(name, age);
        
        // 获取分页信息
        PageInfo<Student> pageInfo = new PageInfo<>(students);
        
        // 封装返回结果
        result.put("records", pageInfo.getList());
        result.put("total", pageInfo.getTotal());
        result.put("pages", pageInfo.getPages());
        
        return result;
    }
} 