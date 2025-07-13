# 数据库连接配置修复说明

## 问题描述
应用启动时出现以下错误：
```
Public Key Retrieval is not allowed
Failed to obtain JDBC Connection
```

## 问题原因
这是MySQL 8.0+版本的认证问题。MySQL 8.0默认使用`caching_sha2_password`认证插件，JDBC连接时需要允许公钥检索。

## 解决方案

### 1. 添加allowPublicKeyRetrieval=true参数
```yaml
url: jdbc:mysql://localhost:3307/school?...&allowPublicKeyRetrieval=true
```

### 2. 完整的JDBC URL参数说明
```yaml
url: jdbc:mysql://localhost:3307/school?
    useSSL=false                        # 禁用SSL（开发环境）
    &serverTimezone=UTC                 # 设置时区
    &characterEncoding=utf8             # 字符编码
    &allowPublicKeyRetrieval=true       # 允许公钥检索（解决认证问题）
    &useUnicode=true                    # 使用Unicode
    &autoReconnect=true                 # 自动重连
    &failOverReadOnly=false             # 故障转移时不设为只读
    &rewriteBatchedStatements=true      # 批量语句重写优化
```

### 3. HikariCP连接池配置
```yaml
hikari:
  maximum-pool-size: 10                 # 最大连接池数量
  minimum-idle: 5                       # 最小空闲连接数
  connection-timeout: 30000             # 连接超时时间(30秒)
  idle-timeout: 600000                  # 空闲连接超时时间(10分钟)
  max-lifetime: 1800000                 # 连接最大生命周期(30分钟)
  connection-test-query: SELECT 1       # 测试连接查询
```

### 4. MyBatis配置优化
```yaml
mybatis:
  configuration:
    map-underscore-to-camel-case: true  # 驼峰命名转换
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl  # SQL日志输出
    cache-enabled: true                 # 启用二级缓存
    lazy-loading-enabled: true          # 启用延迟加载
```

## 配置优势

### 1. 连接稳定性
- 自动重连机制
- 连接池管理
- 连接测试查询

### 2. 性能优化
- 批量语句重写
- 二级缓存
- 延迟加载
- 合理的连接池配置

### 3. 调试便利
- SQL日志输出
- 详细的错误信息
- 连接状态监控

## 注意事项

1. **生产环境建议**：
   - 启用SSL：`useSSL=true`
   - 设置强密码
   - 配置防火墙

2. **性能调优**：
   - 根据应用负载调整连接池大小
   - 监控连接池使用情况
   - 定期检查慢查询

3. **日志管理**：
   - 生产环境可以关闭SQL日志输出
   - 使用logback进行日志管理

## 测试验证

1. 重启应用，检查启动日志
2. 访问健康检查端点：`/actuator/health`
3. 测试数据库相关功能
4. 检查日志文件中的数据库连接信息

## 故障排除

如果仍然出现连接问题，检查：
1. MySQL服务是否运行在3307端口
2. 数据库`school`是否存在
3. 用户权限是否正确
4. 网络连接是否正常

## 性能监控

可以通过以下方式监控数据库连接：
1. Spring Boot Actuator端点
2. HikariCP监控指标
3. MyBatis插件
4. 数据库连接日志 