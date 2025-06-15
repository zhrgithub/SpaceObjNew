# 简化版实时聊天系统 - 前后端交互原理图

## 概述
本文档展示了基于WebSocket的简化版实时聊天系统的前后端交互架构和消息流程。

## 系统架构图

### 1. 整体系统架构
```mermaid
graph TB
    subgraph "前端用户A"
        ClientA[JavaScript客户端A<br/>学生ID: 1]
        BrowserA[浏览器A]
        UAIA[用户界面A]
    end
    
    subgraph "前端用户B"
        ClientB[JavaScript客户端B<br/>学生ID: 2]
        BrowserB[浏览器B]
        UAIB[用户界面B]
    end
    
    subgraph "WebSocket服务层"
        WSHandler[SimpleChatHandler<br/>WebSocket处理器]
        OnlineUsers[在线用户映射<br/>Map&lt;studentId, session&gt;]
        GroupMembers[群组成员映射<br/>Map&lt;groupId, Set&lt;studentId&gt;&gt;]
    end
    
    subgraph "业务服务层"
        ChatService[ChatService<br/>聊天业务服务]
        ChatMapper[ChatMapper<br/>数据访问层]
    end
    
    subgraph "数据存储层"
        DB[(PostgreSQL数据库)]
        ChatMessages[chat_messages表]
        GroupMembersTable[group_members表]
    end
    
    %% WebSocket连接
    ClientA -.->|"WebSocket连接<br/>ws://localhost:8080/ws/chat?studentId=1"| WSHandler
    ClientB -.->|"WebSocket连接<br/>ws://localhost:8080/ws/chat?studentId=2"| WSHandler
    
    %% 连接管理
    WSHandler --> OnlineUsers
    WSHandler --> GroupMembers
    
    %% 业务服务调用
    WSHandler --> ChatService
    ChatService --> ChatMapper
    ChatMapper --> DB
    
    %% 数据库表关系
    DB --> ChatMessages
    DB --> GroupMembersTable
    
    %% 用户界面
    ClientA --> UAIA
    ClientB --> UAIB
    UAIA --> BrowserA
    UAIB --> BrowserB
```

### 2. 详细交互流程图
```mermaid
sequenceDiagram
    participant ClientA as 客户端A(用户1)
    participant WSHandler as WebSocket处理器
    participant ChatService as 聊天服务
    participant DB as 数据库
    participant ClientB as 客户端B(用户2)
    
    %% 连接建立
    Note over ClientA,ClientB: 1. 连接建立阶段
    ClientA->>WSHandler: WebSocket连接 + studentId=1
    WSHandler->>WSHandler: 存储会话到onlineUsers
    WSHandler->>ChatService: 加载用户群组信息
    ChatService->>DB: 查询用户已加入的群组
    DB-->>ChatService: 返回群组列表
    ChatService-->>WSHandler: 返回群组信息
    WSHandler-->>ClientA: CONNECT_SUCCESS
    
    ClientB->>WSHandler: WebSocket连接 + studentId=2
    WSHandler->>WSHandler: 存储会话到onlineUsers
    WSHandler-->>ClientB: CONNECT_SUCCESS
    
    %% 私聊消息
    Note over ClientA,ClientB: 2. 私聊消息流程
    ClientA->>WSHandler: PRIVATE_MESSAGE<br/>{targetStudentId:2, content:"你好"}
    WSHandler->>ChatService: 保存私聊消息
    ChatService->>DB: INSERT到chat_messages表
    DB-->>ChatService: 保存成功
    WSHandler->>WSHandler: 查找目标用户session
    WSHandler-->>ClientB: 转发PRIVATE_MESSAGE<br/>{senderId:1, content:"你好"}
    
    %% 群聊消息
    Note over ClientA,ClientB: 3. 群聊消息流程
    ClientA->>WSHandler: JOIN_GROUP<br/>{groupId:"group_123"}
    WSHandler->>ChatService: 保存群组成员关系
    ChatService->>DB: INSERT到group_members表
    WSHandler->>WSHandler: 更新内存中群组成员
    WSHandler-->>ClientA: JOIN_GROUP_SUCCESS
    
    ClientB->>WSHandler: JOIN_GROUP<br/>{groupId:"group_123"}
    WSHandler->>ChatService: 保存群组成员关系
    ChatService->>DB: INSERT到group_members表
    WSHandler-->>ClientB: JOIN_GROUP_SUCCESS
    
    ClientA->>WSHandler: GROUP_MESSAGE<br/>{groupId:"group_123", content:"大家好"}
    WSHandler->>WSHandler: 验证用户是否在群组中
    WSHandler->>ChatService: 保存群聊消息
    ChatService->>DB: INSERT到chat_messages表
    WSHandler->>WSHandler: 获取群组所有成员
    WSHandler-->>ClientB: 转发GROUP_MESSAGE<br/>{groupId:"group_123", senderId:1, content:"大家好"}
    
    %% 错误处理
    Note over ClientA,ClientB: 4. 错误处理
    ClientA->>WSHandler: 无效消息格式
    WSHandler-->>ClientA: ERROR<br/>{message:"消息格式错误"}
```

## 核心组件说明

### 3.1 前端组件
- **JavaScript客户端**: 负责WebSocket连接管理和消息收发
- **用户界面**: 展示聊天消息和提供用户交互
- **浏览器**: 提供WebSocket API支持

### 3.2 后端组件
- **SimpleChatHandler**: WebSocket消息处理核心，管理连接和消息路由
- **ChatService**: 业务逻辑服务，处理消息存储and群组管理
- **ChatMapper**: 数据访问层，封装数据库操作
- **在线用户映射**: 内存中维护用户ID到WebSocket会话的映射
- **群组成员映射**: 内存中维护群组ID到成员列表的映射

### 3.3 数据存储
- **chat_messages表**: 存储所有聊天消息（私聊和群聊）
- **group_members表**: 存储群组成员关系

## 消息类型和流程

### 4.1 连接管理
```mermaid
graph LR
    A[客户端发起连接] --> B[服务端验证studentId]
    B --> C[加载用户群组信息]
    C --> D[存储到在线用户映射]
    D --> E[返回连接成功确认]
```

### 4.2 私聊消息流程
```mermaid
graph LR
    A[用户A发送私聊消息] --> B[服务端接收消息]
    B --> C[保存到数据库]
    C --> D[查找目标用户会话]
    D --> E[转发给用户B]
    E --> F[用户B接收消息]
```

### 4.3 群聊消息流程
```mermaid
graph LR
    A[用户加入群组] --> B[保存群组成员关系]
    B --> C[更新内存映射]
    C --> D[用户发送群聊消息]
    D --> E[验证群组成员身份]
    E --> F[保存消息到数据库]
    F --> G[转发给所有群成员]
```

## 技术特点

### 5.1 实时性
- 使用WebSocket协议实现真正的全双工通信
- 消息实时推送，无需轮询

### 5.2 可靠性
- 所有消息都持久化到数据库
- 完整的错误处理机制
- 连接断开后自动清理资源

### 5.3 扩展性
- 模块化设计，易于扩展新功能
- 统一的消息协议格式
- 支持水平扩展（可配合Redis等实现集群）

### 5.4 简洁性
- 只实现核心聊天功能
- 代码结构清晰，易于理解和维护
- 配置简单，部署方便

## 部署架构建议

### 6.1 开发环境
```mermaid
graph TB
    subgraph "开发机器"
        Frontend[前端HTML/JS]
        Backend[Spring Boot应用]
        DB[PostgreSQL数据库]
    end
    
    Frontend -.->|WebSocket| Backend
    Backend -->|JDBC| DB
```

### 6.2 生产环境
```mermaid
graph TB
    subgraph "负载均衡层"
        LB[Nginx负载均衡器]
    end
    
    subgraph "应用服务器集群"
        App1[Spring Boot实例1]
        App2[Spring Boot实例2]
    end
    
    subgraph "数据层"
        DB[(PostgreSQL主库)]
        DBSlave[(PostgreSQL从库)]
        Redis[(Redis缓存)]
    end
    
    LB --> App1
    LB --> App2
    App1 --> DB
    App2 --> DB
    App1 --> Redis
    App2 --> Redis
    DB --> DBSlave
```

## 安全考虑

### 7.1 当前实现
- 简单的studentId验证
- 基本的参数校验
- SQL注入防护（MyBatis）

### 7.2 生产环境建议
- 添加JWT令牌验证
- 实现用户权限控制
- 消息内容过滤
- 连接频率限制
- HTTPS/WSS加密传输

这个设计文档提供了完整的前后端交互原理图，有助于理解系统架构和开发实现。 