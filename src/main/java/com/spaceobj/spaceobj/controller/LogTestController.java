package com.spaceobj.spaceobj.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/log-test")
@Slf4j
public class LogTestController {

    @GetMapping("/test")
    public Map<String, Object> testLog() {
        log.info("测试INFO级别日志，时间：{}", System.currentTimeMillis());
        log.debug("测试DEBUG级别日志，用户操作：查看日志测试");
        log.warn("测试WARN级别日志，这是一个警告");
        log.error("测试ERROR级别日志，这是一个错误测试");
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "日志测试完成，请检查控制台、文件和logstash");
        response.put("timestamp", System.currentTimeMillis());
        
        return response;
    }
    
    @GetMapping("/info")
    public Map<String, Object> infoLog() {
        log.info("INFO日志：用户访问了info接口");
        
        Map<String, Object> response = new HashMap<>();
        response.put("level", "INFO");
        response.put("message", "INFO级别日志已发送到logstash");
        
        return response;
    }
    
    @GetMapping("/error")
    public Map<String, Object> errorLog() {
        log.error("ERROR日志：模拟错误情况");
        
        Map<String, Object> response = new HashMap<>();
        response.put("level", "ERROR");
        response.put("message", "ERROR级别日志已发送到logstash");
        
        return response;
    }
} 