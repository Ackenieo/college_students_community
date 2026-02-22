-- =============================================
-- 大学生社区平台数据库表结构
-- Database Schema for College Students Community Platform
-- =============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS college_students CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE college_students;

-- =============================================
-- 用户服务相关表 (User Service)
-- =============================================

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    password VARCHAR(255) NOT NULL COMMENT '密码(加密)',
    full_name VARCHAR(100) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    role ENUM('ADMIN', 'TEACHER', 'STUDENT') NOT NULL DEFAULT 'STUDENT' COMMENT '用户角色',
    status ENUM('ACTIVE', 'INACTIVE', 'SUSPENDED') NOT NULL DEFAULT 'ACTIVE' COMMENT '用户状态',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    last_login TIMESTAMP NULL COMMENT '最后登录时间',
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 用户会话表 (用于JWT token管理)
CREATE TABLE IF NOT EXISTS user_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '会话ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    token_hash VARCHAR(255) NOT NULL COMMENT 'Token哈希值',
    device_info VARCHAR(500) COMMENT '设备信息',
    ip_address VARCHAR(45) COMMENT 'IP地址',
    user_agent VARCHAR(1000) COMMENT '用户代理',
    expires_at TIMESTAMP NOT NULL COMMENT '过期时间',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    last_accessed TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后访问时间',
    is_active BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否活跃',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_token_hash (token_hash),
    INDEX idx_expires_at (expires_at),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户会话表';

-- 验证码表
CREATE TABLE IF NOT EXISTS verification_codes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '验证码ID',
    email VARCHAR(100) NOT NULL COMMENT '邮箱',
    code VARCHAR(10) NOT NULL COMMENT '验证码',
    type ENUM('REGISTER', 'RESET_PASSWORD', 'CHANGE_EMAIL') NOT NULL COMMENT '验证码类型',
    expires_at TIMESTAMP NOT NULL COMMENT '过期时间',
    is_used BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否已使用',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_email (email),
    INDEX idx_code (code),
    INDEX idx_type (type),
    INDEX idx_expires_at (expires_at),
    INDEX idx_is_used (is_used)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='验证码表';

-- =============================================
-- 社区服务相关表 (Community Service) - 关系型数据库部分
-- =============================================

-- 帖子分类表
CREATE TABLE IF NOT EXISTS post_categories (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID',
    name VARCHAR(50) NOT NULL UNIQUE COMMENT '分类名称',
    description VARCHAR(200) COMMENT '分类描述',
    icon VARCHAR(100) COMMENT '分类图标',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序',
    is_active BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否启用',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_sort_order (sort_order),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='帖子分类表';

-- 标签表
CREATE TABLE IF NOT EXISTS tags (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '标签ID',
    name VARCHAR(30) NOT NULL UNIQUE COMMENT '标签名称',
    description VARCHAR(100) COMMENT '标签描述',
    color VARCHAR(7) DEFAULT '#1890ff' COMMENT '标签颜色',
    use_count INT NOT NULL DEFAULT 0 COMMENT '使用次数',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_name (name),
    INDEX idx_use_count (use_count)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';

-- 用户关注表
CREATE TABLE IF NOT EXISTS user_follows (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '关注ID',
    follower_id BIGINT NOT NULL COMMENT '关注者ID',
    following_id BIGINT NOT NULL COMMENT '被关注者ID',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '关注时间',
    FOREIGN KEY (follower_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (following_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_follower_following (follower_id, following_id),
    INDEX idx_follower_id (follower_id),
    INDEX idx_following_id (following_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户关注表';

-- 黑名单表  new
CREATE TABLE IF NOT EXISTS blacklist (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '黑名单ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    blocked_user_id BIGINT NOT NULL COMMENT '被拉黑的用户ID',
    reason VARCHAR(500) COMMENT '拉黑原因',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (blocked_user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_blocked (user_id, blocked_user_id),
    INDEX idx_user_id (user_id),
    INDEX idx_blocked_user_id (blocked_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='黑名单表';

-- 通知表 (new)
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '通知ID',
    receiver_id BIGINT NOT NULL COMMENT '接收者ID',
    sender_id BIGINT COMMENT '发送者ID',
    sender_nickname VARCHAR(100) COMMENT '发送者昵称',
    sender_avatar VARCHAR(500) COMMENT '发送者头像',
    notification_type VARCHAR(50) NOT NULL COMMENT '通知类型',
    content TEXT COMMENT '通知内容',
    is_read BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否已读',
    target_id VARCHAR(50) COMMENT '目标ID(帖子ID、评论ID等)',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_receiver_id (receiver_id),
    INDEX idx_is_read (is_read),
    INDEX idx_receiver_read (receiver_id, is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知表';

-- 举报表
CREATE TABLE IF NOT EXISTS reports (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '举报ID',
    reporter_id BIGINT NOT NULL COMMENT '举报人ID',
    target_type ENUM('POST', 'COMMENT', 'USER', 'MESSAGE') NOT NULL COMMENT '举报目标类型',
    target_id VARCHAR(50) NOT NULL COMMENT '举报目标ID',
    reason ENUM('SPAM', 'INAPPROPRIATE', 'HARASSMENT', 'FAKE', 'OTHER') NOT NULL COMMENT '举报原因',
    description TEXT COMMENT '举报描述',
    status ENUM('PENDING', 'PROCESSING', 'RESOLVED', 'REJECTED') NOT NULL DEFAULT 'PENDING' COMMENT '处理状态',
    admin_notes TEXT COMMENT '管理员备注',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '举报时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (reporter_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_reporter_id (reporter_id),
    INDEX idx_target (target_type, target_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='举报表';

-- =============================================
-- 通知服务相关表 (Notification Service) - 关系型数据库部分
-- =============================================

-- 通知模板表
CREATE TABLE IF NOT EXISTS notification_templates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '模板ID',
    name VARCHAR(50) NOT NULL UNIQUE COMMENT '模板名称',
    title_template VARCHAR(200) NOT NULL COMMENT '标题模板',
    content_template TEXT NOT NULL COMMENT '内容模板',
    type ENUM('EMAIL', 'SMS', 'PUSH', 'IN_APP') NOT NULL COMMENT '通知类型',
    category VARCHAR(50) NOT NULL COMMENT '通知分类',
    is_active BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否启用',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_name (name),
    INDEX idx_type (type),
    INDEX idx_category (category),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知模板表';

-- 邮件发送记录表
CREATE TABLE IF NOT EXISTS email_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '邮件ID',
    to_email VARCHAR(100) NOT NULL COMMENT '收件人邮箱',
    subject VARCHAR(200) NOT NULL COMMENT '邮件主题',
    content TEXT NOT NULL COMMENT '邮件内容',
    template_id BIGINT COMMENT '模板ID',
    status ENUM('PENDING', 'SENT', 'DELIVERED', 'FAILED', 'BOUNCED') NOT NULL DEFAULT 'PENDING' COMMENT '发送状态',
    error_message TEXT COMMENT '错误信息',
    sent_at TIMESTAMP NULL COMMENT '发送时间',
    delivered_at TIMESTAMP NULL COMMENT '送达时间',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (template_id) REFERENCES notification_templates(id) ON DELETE SET NULL,
    INDEX idx_to_email (to_email),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    INDEX idx_sent_at (sent_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='邮件发送记录表';

-- 系统配置表
CREATE TABLE IF NOT EXISTS system_configs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '配置ID',
    config_key VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键',
    config_value TEXT COMMENT '配置值',
    description VARCHAR(200) COMMENT '配置描述',
    category VARCHAR(50) NOT NULL COMMENT '配置分类',
    is_encrypted BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否加密',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_config_key (config_key),
    INDEX idx_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- =============================================
-- Agent服务相关表 (Agent Service)
-- =============================================

-- AI对话记录表
CREATE TABLE IF NOT EXISTS ai_conversations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '对话ID',
    conversation_id VARCHAR(100) NOT NULL UNIQUE COMMENT '外部对话ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    session_id VARCHAR(100) COMMENT '会话ID',
    status ENUM('ACTIVE', 'ENDED', 'SUSPENDED') NOT NULL DEFAULT 'ACTIVE' COMMENT '对话状态',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    ended_at TIMESTAMP NULL COMMENT '结束时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_conversation_id (conversation_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI对话记录表';

-- AI对话消息表
CREATE TABLE IF NOT EXISTS ai_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '消息ID',
    conversation_id BIGINT NOT NULL COMMENT '对话ID',
    message_type ENUM('USER', 'ASSISTANT', 'SYSTEM') NOT NULL COMMENT '消息类型',
    content TEXT NOT NULL COMMENT '消息内容',
    metadata JSON COMMENT '元数据',
    token_count INT COMMENT 'Token数量',
    processing_time_ms INT COMMENT '处理时间(毫秒)',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (conversation_id) REFERENCES ai_conversations(id) ON DELETE CASCADE,
    INDEX idx_conversation_id (conversation_id),
    INDEX idx_message_type (message_type),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI对话消息表';

-- 内容审核记录表
CREATE TABLE IF NOT EXISTS content_reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '审核ID',
    content_id VARCHAR(50) NOT NULL COMMENT '内容ID',
    content_type ENUM('POST', 'COMMENT', 'MESSAGE') NOT NULL COMMENT '内容类型',
    content_text TEXT NOT NULL COMMENT '审核内容',
    review_result ENUM('APPROVED', 'REJECTED', 'PENDING', 'NEEDS_HUMAN') NOT NULL COMMENT '审核结果',
    review_reason TEXT COMMENT '审核原因',
    confidence_score DECIMAL(3,2) COMMENT '置信度分数',
    ai_model VARCHAR(50) COMMENT 'AI模型',
    processing_time_ms INT COMMENT '处理时间(毫秒)',
    reviewed_by BIGINT COMMENT '审核人ID',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (reviewed_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_content (content_type, content_id),
    INDEX idx_review_result (review_result),
    INDEX idx_created_at (created_at),
    INDEX idx_reviewed_by (reviewed_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容审核记录表';

-- =============================================
-- 系统监控相关表
-- =============================================

-- 系统日志表
CREATE TABLE IF NOT EXISTS system_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    level ENUM('DEBUG', 'INFO', 'WARN', 'ERROR', 'FATAL') NOT NULL COMMENT '日志级别',
    logger_name VARCHAR(100) NOT NULL COMMENT '日志记录器',
    message TEXT NOT NULL COMMENT '日志消息',
    exception_info TEXT COMMENT '异常信息',
    user_id BIGINT COMMENT '用户ID',
    ip_address VARCHAR(45) COMMENT 'IP地址',
    user_agent VARCHAR(1000) COMMENT '用户代理',
    request_id VARCHAR(100) COMMENT '请求ID',
    service_name VARCHAR(50) COMMENT '服务名称',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_level (level),
    INDEX idx_logger_name (logger_name),
    INDEX idx_user_id (user_id),
    INDEX idx_service_name (service_name),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统日志表';

-- 操作审计表
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '审计ID',
    user_id BIGINT COMMENT '用户ID',
    action VARCHAR(100) NOT NULL COMMENT '操作动作',
    resource_type VARCHAR(50) NOT NULL COMMENT '资源类型',
    resource_id VARCHAR(50) NOT NULL COMMENT '资源ID',
    old_values JSON COMMENT '旧值',
    new_values JSON COMMENT '新值',
    ip_address VARCHAR(45) COMMENT 'IP地址',
    user_agent VARCHAR(1000) COMMENT '用户代理',
    service_name VARCHAR(50) COMMENT '服务名称',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_action (action),
    INDEX idx_resource (resource_type, resource_id),
    INDEX idx_service_name (service_name),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作审计表';

-- =============================================
-- 初始化数据
-- =============================================

-- 插入默认管理员用户 (密码: admin123)
INSERT IGNORE INTO users (id, username, email, password, full_name, role, status) VALUES 
(1, 'admin', 'admin@college.edu.cn', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFJ9q9z7G6H4QKJ8nL5z8s2', '系统管理员', 'ADMIN', 'ACTIVE');

-- 插入默认帖子分类
INSERT IGNORE INTO post_categories (id, name, description, icon, sort_order) VALUES 
(1, '学习交流', '学习经验分享、课程讨论、作业互助', 'book', 1),
(2, '生活分享', '校园生活、美食推荐、娱乐活动', 'home', 2),
(3, '社团活动', '社团招募、活动通知、兴趣交流', 'users', 3),
(4, '求职就业', '实习信息、面试经验、职业规划', 'briefcase', 4),
(5, '二手交易', '二手物品买卖、拼车信息、租赁信息', 'shopping-cart', 5),
(6, '情感交友', '情感咨询、交友活动、恋爱话题', 'heart', 6);

-- 插入默认标签
INSERT IGNORE INTO tags (name, description, color, use_count) VALUES 
('学习', '学习相关话题', '#1890ff', 0),
('考试', '考试相关话题', '#52c41a', 0),
('实习', '实习相关话题', '#faad14', 0),
('社团', '社团活动话题', '#722ed1', 0),
('美食', '美食分享话题', '#fa541c', 0),
('运动', '运动健身话题', '#13c2c2', 0),
('科技', '科技数码话题', '#eb2f96', 0),
('旅行', '旅行分享话题', '#2f54eb', 0);

-- 插入默认通知模板
INSERT IGNORE INTO notification_templates (name, title_template, content_template, type, category) VALUES 
('register_verification', '注册验证码', '您的注册验证码是：{code}，有效期10分钟。', 'EMAIL', 'verification'),
('password_reset', '密码重置验证码', '您的密码重置验证码是：{code}，有效期10分钟。', 'EMAIL', 'verification'),
('friend_request', '新的好友申请', '{senderName} 想要添加您为好友', 'IN_APP', 'social'),
('message_notification', '新消息', '{senderName} 给您发了一条消息：{messagePreview}', 'IN_APP', 'message'),
('like_notification', '收到新的点赞', '{senderName} 点赞了您的{contentType}', 'IN_APP', 'interaction'),
('comment_notification', '收到新的评论', '{senderName} 评论了您的帖子：{commentPreview}', 'IN_APP', 'interaction');

-- 插入系统配置
INSERT IGNORE INTO system_configs (config_key, config_value, description, category) VALUES 
('site.name', '大学生社区平台', '网站名称', 'site'),
('site.description', '为大学生提供交流互动的社区平台', '网站描述', 'site'),
('email.smtp.host', 'smtp.qq.com', 'SMTP服务器地址', 'email'),
('email.smtp.port', '587', 'SMTP端口', 'email'),
('email.smtp.username', '', 'SMTP用户名', 'email'),
('email.smtp.password', '', 'SMTP密码', 'email'),
('jwt.secret', 'college-students-secret-key-2024', 'JWT密钥', 'security'),
('jwt.expiration', '86400000', 'JWT过期时间(毫秒)', 'security'),
('upload.max.file.size', '10485760', '最大上传文件大小(字节)', 'upload'),
('upload.allowed.types', 'jpg,jpeg,png,gif,pdf,doc,docx', '允许上传的文件类型', 'upload');

-- =============================================
-- 创建视图
-- =============================================

-- 用户统计视图
CREATE OR REPLACE VIEW user_stats AS
SELECT 
    u.id,
    u.username,
    u.email,
    u.full_name,
    u.status,
    u.created_at,
    u.last_login,
    COUNT(DISTINCT uf1.follower_id) as followers_count,
    COUNT(DISTINCT uf2.following_id) as following_count,
    COUNT(DISTINCT us.id) as active_sessions
FROM users u
LEFT JOIN user_follows uf1 ON u.id = uf1.following_id
LEFT JOIN user_follows uf2 ON u.id = uf2.follower_id
LEFT JOIN user_sessions us ON u.id = us.user_id AND us.is_active = TRUE
GROUP BY u.id, u.username, u.email, u.full_name, u.status, u.created_at, u.last_login;

-- 内容审核统计视图
CREATE OR REPLACE VIEW content_review_stats AS
SELECT 
    DATE(created_at) as review_date,
    content_type,
    review_result,
    COUNT(*) as count,
    AVG(processing_time_ms) as avg_processing_time,
    AVG(confidence_score) as avg_confidence_score
FROM content_reviews
GROUP BY DATE(created_at), content_type, review_result
ORDER BY review_date DESC, content_type, review_result;

-- =============================================
-- 创建存储过程
-- =============================================

DELIMITER //

-- 清理过期会话的存储过程
CREATE PROCEDURE CleanExpiredSessions()
BEGIN
    DELETE FROM user_sessions 
    WHERE expires_at < NOW() OR is_active = FALSE;
    
    SELECT ROW_COUNT() as cleaned_sessions;
END //

-- 清理过期验证码的存储过程
CREATE PROCEDURE CleanExpiredVerificationCodes()
BEGIN
    DELETE FROM verification_codes 
    WHERE expires_at < NOW() OR is_used = TRUE;
    
    SELECT ROW_COUNT() as cleaned_codes;
END //

-- 更新标签使用次数的存储过程
CREATE PROCEDURE UpdateTagUsageCount(IN tag_name VARCHAR(30))
BEGIN
    UPDATE tags 
    SET use_count = use_count + 1, updated_at = NOW()
    WHERE name = tag_name;
    
    SELECT ROW_COUNT() as updated_tags;
END //

DELIMITER ;

-- =============================================
-- 创建事件调度器 (需要开启事件调度器)
-- =============================================

-- 开启事件调度器
SET GLOBAL event_scheduler = ON;

-- 每小时清理过期会话
CREATE EVENT IF NOT EXISTS cleanup_expired_sessions
ON SCHEDULE EVERY 1 HOUR
STARTS CURRENT_TIMESTAMP
DO
  CALL CleanExpiredSessions();

-- 每小时清理过期验证码
CREATE EVENT IF NOT EXISTS cleanup_expired_verification_codes
ON SCHEDULE EVERY 1 HOUR
STARTS CURRENT_TIMESTAMP
DO
  CALL CleanExpiredVerificationCodes();

-- 每天凌晨2点清理30天前的日志
CREATE EVENT IF NOT EXISTS cleanup_old_logs
ON SCHEDULE EVERY 1 DAY
STARTS '2024-01-01 02:00:00'
DO
  DELETE FROM system_logs WHERE created_at < DATE_SUB(NOW(), INTERVAL 30 DAY);

-- =============================================
-- 创建索引优化
-- =============================================

-- 为常用查询创建复合索引
CREATE INDEX idx_users_role_status ON users(role, status);
CREATE INDEX idx_users_created_status ON users(created_at, status);
CREATE INDEX idx_user_sessions_user_active ON user_sessions(user_id, is_active);
CREATE INDEX idx_verification_codes_email_type ON verification_codes(email, type);
CREATE INDEX idx_content_reviews_result_date ON content_reviews(review_result, created_at);
CREATE INDEX idx_system_logs_level_date ON system_logs(level, created_at);
CREATE INDEX idx_audit_logs_user_date ON audit_logs(user_id, created_at);

-- =============================================
-- 权限设置
-- =============================================

-- 创建应用数据库用户
CREATE USER IF NOT EXISTS 'college_app'@'%' IDENTIFIED BY 'CollegeApp2024!';
GRANT SELECT, INSERT, UPDATE, DELETE ON college_students.* TO 'college_app'@'%';

-- 创建只读用户（用于监控和报表）
CREATE USER IF NOT EXISTS 'college_readonly'@'%' IDENTIFIED BY 'CollegeReadOnly2024!';
GRANT SELECT ON college_students.* TO 'college_readonly'@'%';

-- 刷新权限
FLUSH PRIVILEGES;

-- =============================================
-- 数据库初始化完成
-- =============================================

SELECT 'Database schema initialization completed successfully!' as message;
