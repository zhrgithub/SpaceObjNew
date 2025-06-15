package com.spaceobj.spaceobj.controller;

import com.spaceobj.spaceobj.pojo.Student;
import com.spaceobj.spaceobj.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@Tag(name = "学生管理", description = "学生信息管理相关接口")
public class StudentController {

    // 注入学生服务层
    @Autowired
    private StudentService studentService;

    // 获取所有学生列表
    @GetMapping("/list")
    @ResponseBody
    @Operation(summary = "获取所有学生列表", description = "获取系统中所有学生的信息列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取学生列表"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public List<Student> getAllStudents() {
        List<Student> students = studentService.getAllStudents();
        return students;
    }

    

    // 根据ID获取单个学生信息
    @GetMapping("/{id}")
    @ResponseBody
    @Operation(summary = "根据ID获取学生信息", description = "通过学生ID获取单个学生的详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取学生信息"),
            @ApiResponse(responseCode = "404", description = "学生不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Student getStudentById(
            @Parameter(description = "学生ID", required = true, example = "1")
            @PathVariable Long id) {
        return studentService.getStudentById(id);
    }

    // 添加学生（示例方法，未完全实现）
    public void addStudent(Student student) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
    }

    // 无限循环测试接口（仅用于测试，生产环境不建议使用）
    @GetMapping("infiniteLoop")
    @ResponseBody
    @Operation(summary = "无限循环测试接口", description = "仅用于测试，生产环境不建议使用")
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
    @Operation(summary = "分页查询学生列表", description = "根据条件分页查询学生信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "查询失败")
    })
    public Map<String, Object> getStudentsByPage(
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "学生姓名（模糊查询）", example = "张三")
            @RequestParam(required = false) String name,
            @Parameter(description = "学生年龄", example = "20")
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
