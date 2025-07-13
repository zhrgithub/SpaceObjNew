package com.spaceobj.spaceobj.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/db-test")
@Slf4j
public class DatabaseTestController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/connection")
    public Map<String, Object> testConnection() {
        Map<String, Object> result = new HashMap<>();
        
        try (Connection connection = dataSource.getConnection()) {
            log.info("数据库连接测试 - 连接成功");
            
            DatabaseMetaData metaData = connection.getMetaData();
            
            result.put("status", "success");
            result.put("connected", true);
            result.put("database_url", metaData.getURL());
            result.put("database_product", metaData.getDatabaseProductName());
            result.put("database_version", metaData.getDatabaseProductVersion());
            result.put("driver_name", metaData.getDriverName());
            result.put("driver_version", metaData.getDriverVersion());
            result.put("user_name", metaData.getUserName());
            result.put("test_time", System.currentTimeMillis());
            
            log.info("数据库信息：{} {}", 
                    metaData.getDatabaseProductName(), 
                    metaData.getDatabaseProductVersion());
            
        } catch (SQLException e) {
            log.error("数据库连接测试失败", e);
            result.put("status", "error");
            result.put("connected", false);
            result.put("error", e.getMessage());
            result.put("error_code", e.getErrorCode());
            result.put("sql_state", e.getSQLState());
        }
        
        return result;
    }
    
    @GetMapping("/tables")
    public Map<String, Object> listTables() {
        Map<String, Object> result = new HashMap<>();
        
        try (Connection connection = dataSource.getConnection()) {
            log.info("查询数据库表信息");
            
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});
            
            java.util.List<String> tableNames = new java.util.ArrayList<>();
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                tableNames.add(tableName);
            }
            
            result.put("status", "success");
            result.put("table_count", tableNames.size());
            result.put("tables", tableNames);
            
            log.info("找到 {} 个表", tableNames.size());
            
        } catch (SQLException e) {
            log.error("查询表信息失败", e);
            result.put("status", "error");
            result.put("error", e.getMessage());
        }
        
        return result;
    }
    
    @GetMapping("/ping")
    public Map<String, Object> ping() {
        Map<String, Object> result = new HashMap<>();
        
        try (Connection connection = dataSource.getConnection()) {
            // 执行简单的ping查询
            boolean isValid = connection.isValid(5); // 5秒超时
            
            result.put("status", "success");
            result.put("ping", "pong");
            result.put("connection_valid", isValid);
            result.put("response_time", System.currentTimeMillis());
            
            log.info("数据库ping测试成功，连接有效: {}", isValid);
            
        } catch (SQLException e) {
            log.error("数据库ping测试失败", e);
            result.put("status", "error");
            result.put("ping", "failed");
            result.put("error", e.getMessage());
        }
        
        return result;
    }
} 