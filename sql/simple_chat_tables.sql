-- 简化版聊天系统数据库表设计
-- 基于现有的students表设计

-- 1. 聊天消息表
CREATE TABLE chat_messages (
    message_id SERIAL PRIMARY KEY,
    message_type VARCHAR(20) NOT NULL,     -- 'PRIVATE' 或 'GROUP'
    sender_id INTEGER NOT NULL,            -- 发送者ID
    target_id INTEGER,                     -- 私聊对象ID（私聊时使用）
    group_id VARCHAR(50),                  -- 群ID（群聊时使用）
    content TEXT NOT NULL,                 -- 消息内容
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. 群成员表
CREATE TABLE group_members (
    id SERIAL PRIMARY KEY,
    group_id VARCHAR(50) NOT NULL,        -- 群ID
    student_id INTEGER NOT NULL,          -- 学生ID
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(group_id, student_id)          -- 确保同一学生在同一群组中只有一条记录
);

-- 创建索引以提高查询性能
CREATE INDEX idx_messages_sender ON chat_messages(sender_id);
CREATE INDEX idx_messages_target ON chat_messages(target_id);
CREATE INDEX idx_messages_group ON chat_messages(group_id);
CREATE INDEX idx_messages_sent_at ON chat_messages(sent_at);
CREATE INDEX idx_group_members_group ON group_members(group_id);
CREATE INDEX idx_group_members_student ON group_members(student_id);

-- 示例：插入一些测试数据（可选）
-- INSERT INTO group_members (group_id, student_id) VALUES ('group_123', 1);
-- INSERT INTO group_members (group_id, student_id) VALUES ('group_123', 2);
-- INSERT INTO chat_messages (message_type, sender_id, group_id, content) VALUES ('GROUP', 1, 'group_123', '大家好！'); 