# Logback与Logstash配置说明

## 概述
本项目已配置logback日志框架，支持将日志同时输出到控制台、文件和Logstash。

## 配置详情

### 1. 依赖添加
在`pom.xml`中添加了以下依赖：
```xml
<dependency>
    <groupId>net.logstash.logback</groupId>
    <artifactId>logstash-logback-encoder</artifactId>
    <version>7.4</version>
</dependency>
```

### 2. Logback配置文件
位置：`src/main/resources/logback.xml`

#### 主要配置项：
- **控制台输出**：实时查看日志
- **文件输出**：日志保存到`./logs/SpaceObj.log`
- **Logstash输出**：发送到`127.0.0.1:5001`

#### 日志文件配置：
- 日志文件路径：`./logs/SpaceObj.log`
- 按天轮转，单个文件最大100MB
- 保留30天的历史日志

#### Logstash配置：
- 目标地址：`127.0.0.1:5001`
- 连接超时：5秒
- 重连延迟：1秒
- 使用异步处理以提高性能

### 3. 日志级别配置
- `com.spaceobj.spaceobj`：DEBUG级别
- `org.springframework.web.socket`：DEBUG级别
- `com.spaceobj.spaceobj.mapper`：DEBUG级别
- `org.mybatis`：DEBUG级别
- 其他：INFO级别

## 使用方法

### 1. 启动应用
确保Logstash服务在`127.0.0.1:5001`运行，然后启动Spring Boot应用。

### 2. 测试日志功能
访问以下接口测试日志输出：
- `GET /log-test/test` - 测试所有日志级别
- `GET /log-test/info` - 测试INFO级别日志
- `GET /log-test/error` - 测试ERROR级别日志

### 3. 在代码中使用
```java
@Slf4j
public class YourController {
    
    @GetMapping("/example")
    public String example() {
        log.info("这是一个INFO级别的日志");
        log.debug("这是一个DEBUG级别的日志");
        log.warn("这是一个WARN级别的日志");
        log.error("这是一个ERROR级别的日志");
        return "success";
    }
}
```

## 日志格式
- 控制台和文件：`%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n`
- Logstash：JSON格式，包含应用名称和上下文信息

## 注意事项
1. 确保Logstash服务正在运行并监听5001端口
2. 如果Logstash服务不可用，应用仍然会正常运行，只是无法发送日志到Logstash
3. 日志文件会在项目根目录下的`logs`文件夹中创建
4. 可以通过修改`logback.xml`中的配置来调整日志级别和输出格式

## 故障排除
如果日志没有正确发送到Logstash，请检查：
1. Logstash服务是否在`127.0.0.1:5001`运行
2. 网络连接是否正常
3. 查看应用启动日志中是否有连接错误信息 