# SpaceObj 项目文档

本目录包含 SpaceObj 学生管理系统的所有技术文档。

## 文档结构

- [`simple-chat-design.md`](./simple-chat-design.md) - ⭐ **完整的简化版聊天系统设计**（单聊+群聊）
- [`logback-logstash-setup.md`](./logback-logstash-setup.md) - 📝 **Logback与Logstash配置说明**
- [`database-config-fix.md`](./database-config-fix.md) - 🔧 **数据库连接问题修复指南**

## 快速开始

1. 阅读 [简化版聊天系统设计](./simple-chat-design.md) - 包含完整的实现方案
2. 执行 `sql/simple_chat_tables.sql` 创建数据库表
3. 复制代码到你的Spring Boot项目中
4. 启动项目即可使用实时聊天功能

## 测试接口

### 日志功能测试
- `GET /log-test/test` - 测试所有日志级别
- `GET /log-test/info` - 测试INFO级别日志
- `GET /log-test/error` - 测试ERROR级别日志

### 数据库连接测试
- `GET /db-test/connection` - 测试数据库连接状态
- `GET /db-test/tables` - 查看数据库表信息
- `GET /db-test/ping` - 数据库ping测试

### 健康检查
- `GET /actuator/health` - 应用健康状态

## 配置说明

### 日志配置
- 日志文件位置：`./logs/SpaceObj.log`
- Logstash地址：`127.0.0.1:5001`
- 详细配置见：[logback-logstash-setup.md](./logback-logstash-setup.md)

### 数据库配置
- 连接池：HikariCP
- 最大连接数：10
- 详细配置见：[database-config-fix.md](./database-config-fix.md)

## 故障排除

### 1. 数据库连接问题
如果遇到 "Public Key Retrieval is not allowed" 错误，请查看：
- [database-config-fix.md](./database-config-fix.md)

### 2. 日志配置问题
如果遇到logback配置错误，请查看：
- [logback-logstash-setup.md](./logback-logstash-setup.md)

### 3. 常见问题
1. 确保MySQL服务运行在3307端口
2. 确保数据库`school`已创建
3. 确保Logstash服务运行在5001端口（可选）

## 更新记录

- 2025-01-13: 🔧 **修复数据库连接问题** - 解决MySQL 8.0认证错误
- 2025-01-13: 📝 **添加Logback与Logstash配置** - 完整的日志管理方案
- 2025-01-13: 🚀 **优化数据库配置** - 添加HikariCP连接池配置
- 2025-01-13: ✅ **添加测试接口** - 数据库连接测试和日志功能测试
- 2024-01-15: 添加聊天系统设计文档
- 2024-01-15: 完成聊天功能数据库设计
- 2024-01-15: 完成聊天功能API接口设计
- 2024-01-15: **重新设计聊天架构** - 基于WebSocket的实时聊天系统
- 2024-01-15: ⭐ **简化版聊天系统** - 专注核心功能，去掉复杂性 