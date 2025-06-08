package com.spaceobj.spaceobj.controller;

import com.spaceobj.spaceobj.pojo.Student;
import com.spaceobj.spaceobj.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 学生控制器，处理与学生相关的 HTTP 请求
@Controller
@RequestMapping("/students")
public class StudentController {

    // 注入学生服务层
    @Autowired
    private StudentService studentService;

    // 获取所有学生列表
    @GetMapping("/list")
    @ResponseBody
    public List<Student> getAllStudents() {
        List<Student> students = studentService.getAllStudents();
        return students;
    }

    

    // 根据ID获取单个学生信息
    @GetMapping("/{id}")
    @ResponseBody
    public Student getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id);
    }

    // 添加学生（示例方法，未完全实现）
    public void addStudent(Student student) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
    }

    // 无限循环测试接口（仅用于测试，生产环境不建议使用）
    @GetMapping("infiniteLoop")
    @ResponseBody
    public Map<String,Object> infiniteLoop() {
        Map<String,Object> map = new HashMap<>();
        boolean flag = true;
        while (flag) {
            createStudent();
        }
        return map;
    }

    // 创建学生的辅助方法
    private void createStudent() {
        new HashMap<>();
    }

    // 写一个排序的算法

    // 分页查询学生列表
    @GetMapping("/page")
    @ResponseBody
    public Map<String, Object> getStudentsByPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer age) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 调用 Service 层的分页查询方法
            Map<String, Object> pageResult = studentService.getStudentsByPage(pageNum, pageSize, name, age);
            
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", pageResult.get("records"));
            response.put("total", pageResult.get("total"));
            response.put("pages", pageResult.get("pages"));
            response.put("current", pageNum);
            response.put("size", pageSize);
            
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败：" + e.getMessage());
        }
        
        return response;
    }

}
