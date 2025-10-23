# 大学生社区平台 - 项目设计文档

## 📋 项目概述

**项目名称**: 大学生社区平台 (College Students Community Platform)  
**架构模式**: 微服务架构 + Spring Cloud  
**技术栈**: Spring Boot 3.x, MySQL, Redis, MongoDB, Nacos, Docker  
**部署方式**: 云服务器 + Docker 容器化  

## 🎯 项目目标

为大学生提供一个集学习交流、生活分享、社团活动、求职就业、二手交易、情感交友于一体的综合性社区平台，支持智能AI助手、内容审核、实时通知等功能。

---

## 🏗️ 系统架构

### 微服务架构图

```
┌─────────────────────────────────────────────────────────────┐
│                    API Gateway (8080)                      │
│                   - 路由转发                                │
│                   - 认证鉴权                                │
│                   - 限流熔断                                │
└─────────────────────┬───────────────────────────────────────┘
                      │
    ┌─────────────────┼─────────────────┐
    │                 │                 │
┌───▼───┐         ┌───▼───┐         ┌───▼───┐
│User   │         │Community│        │Notification│
│Server │         │Server  │        │Server  │
│8081   │         │8082    │        │8084    │
└───┬───┘         └───┬───┘         └───┬───┘
    │                 │                 │
┌───▼───┐         ┌───▼───┐         ┌───▼───┐
│Agent  │         │Config │         │Monitor│
│Server │         │Server │         │Tools  │
│8083   │         │8888   │         │       │
└───────┘         └───────┘         └───────┘
```

### 服务注册与发现

- **Nacos**: 服务注册中心 + 配置中心
- **服务地址**: `8.148.189.41:8848`
- **配置管理**: 统一配置管理，支持动态更新

### 数据存储架构

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│     MySQL       │    │     Redis       │    │    MongoDB      │
│  关系型数据      │    │   缓存+会话     │    │   文档数据       │
│  - 用户信息      │    │  - 验证码       │    │  - 帖子内容     │
│  - 系统配置      │    │  - 用户会话     │    │  - 评论内容     │
│  - 审核记录      │    │  - 热点数据     │    │  - 消息记录     │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

---

## 🔄 核心业务流程

### 1. 用户注册与认证流程

```mermaid
sequenceDiagram
    participant C as 客户端
    participant U as User-Server
    participant E as Email-Service
    participant R as Redis
    participant D as Database

    C->>U: 1. 发送注册验证码请求
    U->>R: 2. 生成并存储验证码(5分钟)
    U->>E: 3. 发送验证码邮件
    E-->>U: 4. 邮件发送结果
    U-->>C: 5. 返回发送结果

    C->>U: 6. 提交注册信息+验证码
    U->>R: 7. 验证验证码
    U->>D: 8. 检查用户名/邮箱唯一性
    U->>D: 9. 创建用户记录
    U->>R: 10. 清除验证码
    U-->>C: 11. 返回注册结果
```

#### 数据库表操作
- 验证码发送阶段：
  - verification_codes: INSERT 验证码; 校验时 SELECT by email+type+code; 过期清理 DELETE/由事件触发
  - system_configs: SELECT 邮件模板/配置（若从表获取）
  - email_logs: 发送邮件后 INSERT 发送记录，错误时 UPDATE status/错误信息
- 提交注册阶段：
  - users: SELECT 校验 username/email 唯一; INSERT 新用户; 注册成功后 UPDATE last_login（可延后）
  - verification_codes: SELECT 校验验证码有效; 注册完成后 UPDATE is_used=TRUE 或 DELETE 过期/使用完成
  - user_sessions: 可选 INSERT 初始会话（若注册即登录）
  - system_logs/audit_logs: INSERT 行为日志与审计记录

### 2. 用户登录与JWT认证流程

```mermaid
sequenceDiagram
    participant C as 客户端
    participant U as User-Server
    participant R as Redis
    participant D as Database

    C->>U: 1. 提交登录信息(邮箱+密码)
    U->>D: 2. 验证用户信息
    U->>U: 3. 生成JWT Token
    U->>R: 4. 缓存用户信息
    U->>D: 5. 更新最后登录时间
    U-->>C: 6. 返回Token+用户信息
```

#### 数据库表操作
- users: SELECT by email 获取密码哈希/状态; 成功后 UPDATE last_login
- user_sessions: INSERT 新会话记录（token 哈希、设备、过期时间）; 心跳或访问时 UPDATE last_accessed; 登出或过期清理时 UPDATE is_active=FALSE 或 DELETE
- system_logs: INSERT 登录结果日志（INFO/ERROR）
- audit_logs: INSERT 登录操作审计（可选）

### 3. 帖子发布与内容审核流程

```mermaid
sequenceDiagram
    participant C as 客户端
    participant CS as Community-Server
    participant A as Agent-Server
    participant M as MongoDB
    participant D as Database

    C->>CS: 1. 提交帖子内容
    CS->>A: 2. 调用AI内容审核
    A-->>CS: 3. 返回审核结果
    alt 审核通过
        CS->>M: 4. 存储帖子到MongoDB
        CS->>D: 5. 记录审核日志
        CS-->>C: 6. 返回发布成功
    else 审核不通过
        CS->>D: 4. 记录审核日志
        CS-->>C: 5. 返回审核失败原因
    end
```

#### 数据库表操作
- content_reviews: AI 审核完成后 INSERT 审核记录（结果、模型、耗时、置信度）
- post_categories/tags: SELECT 基础配置（分类/标签存在性校验）；tags 命中时可调用存储过程 UpdateTagUsageCount 更新 use_count
- MongoDB（社区内容存储）: INSERT 帖子文档与评论文档（不在此 SQL 文件范围）
- reports: 审核不通过时可 INSERT 自动举报记录（可选）
- system_logs/audit_logs: INSERT 发布/审核流程日志与审计

### 4. AI智能助手对话流程

```mermaid
sequenceDiagram
    participant C as 客户端
    participant AS as Agent-Server
    participant DS as Dify-Service
    participant D as Database

    C->>AS: 1. 发送对话请求
    AS->>D: 2. 记录对话会话
    AS->>DS: 3. 调用Dify API
    DS-->>AS: 4. 返回AI回复
    AS->>D: 5. 保存对话记录
    AS-->>C: 6. 返回AI回复内容
```

#### 数据库表操作
- ai_conversations: 无对话或首次发起时 INSERT; 继续对话时 SELECT by conversation_id；结束时 UPDATE status/ended_at
- ai_messages: 每轮对话分别 INSERT USER 与 ASSISTANT 消息（含 metadata、token_count、耗时）
- content_reviews: 若启用对话内容安全审查，则 INSERT 审核记录（可选）
- system_logs: INSERT 关键步骤日志（调用 Dify、返回结果）

### 5. 实时通知推送流程

```mermaid
sequenceDiagram
    participant CS as Community-Server
    participant NS as Notification-Server
    participant WS as WebSocket
    participant C as 客户端

    CS->>NS: 1. 触发通知事件
    NS->>NS: 2. 生成通知内容
    NS->>WS: 3. 推送WebSocket消息
    WS-->>C: 4. 实时推送给用户
    NS->>NS: 5. 发送邮件通知(可选)
```

#### 数据库表操作
- notification_templates: SELECT 模板内容（标题/正文）
- email_logs: 邮件类通知 INSERT 记录，并在回执时 UPDATE status/sent_at/delivered_at
- system_configs: SELECT 通知与渠道开关配置
- system_logs: INSERT 推送状态日志

---

## 🔗 业务流程与数据库表操作映射（汇总表）

- 用户注册/验证：
  - verification_codes: INSERT/SELECT/UPDATE/DELETE
  - users: SELECT（唯一性）/INSERT/UPDATE（last_login 可选）
  - email_logs: INSERT/UPDATE
  - system_logs, audit_logs: INSERT

- 用户登录/会话：
  - users: SELECT/UPDATE（last_login）
  - user_sessions: INSERT/UPDATE（last_accessed,is_active）/DELETE（过期）
  - system_logs, audit_logs: INSERT

- 内容发布/审核：
  - content_reviews: INSERT
  - post_categories, tags: SELECT；tags 使用计数通过存储过程更新
  - reports: INSERT（不通过时可选）
  - system_logs, audit_logs: INSERT

- AI 对话：
  - ai_conversations: INSERT/SELECT/UPDATE
  - ai_messages: INSERT
  - content_reviews: INSERT（启用安全审查时）
  - system_logs: INSERT

- 通知推送：
  - notification_templates: SELECT
  - email_logs: INSERT/UPDATE
  - system_configs: SELECT
  - system_logs: INSERT

---

## 🗄️ 数据库表设计详解

### 用户服务相关表 (User Service)

#### 1. `users` - 用户表
**作用**: 存储用户基本信息
```sql
- id: 用户唯一标识
- username: 用户名(唯一)
- email: 邮箱(唯一)
- password: 加密密码
- full_name: 真实姓名
- phone: 手机号
- role: 用户角色(ADMIN/TEACHER/STUDENT)
- status: 用户状态(ACTIVE/INACTIVE/SUSPENDED)
- created_at: 创建时间
- updated_at: 更新时间
- last_login: 最后登录时间
```

#### 2. `user_sessions` - 用户会话表
**作用**: 管理JWT Token和用户会话
```sql
- id: 会话ID
- user_id: 关联用户ID
- token_hash: Token哈希值
- device_info: 设备信息
- ip_address: IP地址
- expires_at: 过期时间
- is_active: 是否活跃
```

#### 3. `verification_codes` - 验证码表
**作用**: 存储邮箱验证码
```sql
- id: 验证码ID
- email: 邮箱地址
- code: 验证码
- type: 验证码类型(REGISTER/RESET_PASSWORD/CHANGE_EMAIL)
- expires_at: 过期时间
- is_used: 是否已使用
```

### 社区服务相关表 (Community Service)

#### 4. `post_categories` - 帖子分类表
**作用**: 管理帖子分类
```sql
- id: 分类ID
- name: 分类名称
- description: 分类描述
- icon: 分类图标
- sort_order: 排序
- is_active: 是否启用
```

#### 5. `tags` - 标签表
**作用**: 管理内容标签
```sql
- id: 标签ID
- name: 标签名称
- description: 标签描述
- color: 标签颜色
- use_count: 使用次数
```

#### 6. `user_follows` - 用户关注表
**作用**: 管理用户关注关系
```sql
- id: 关注ID
- follower_id: 关注者ID
- following_id: 被关注者ID
- created_at: 关注时间
```

#### 7. `reports` - 举报表
**作用**: 管理用户举报
```sql
- id: 举报ID
- reporter_id: 举报人ID
- target_type: 举报目标类型(POST/COMMENT/USER/MESSAGE)
- target_id: 举报目标ID
- reason: 举报原因(SPAM/INAPPROPRIATE/HARASSMENT/FAKE/OTHER)
- status: 处理状态(PENDING/PROCESSING/RESOLVED/REJECTED)
```

### 通知服务相关表 (Notification Service)

#### 8. `notification_templates` - 通知模板表
**作用**: 管理通知模板
```sql
- id: 模板ID
- name: 模板名称
- title_template: 标题模板
- content_template: 内容模板
- type: 通知类型(EMAIL/SMS/PUSH/IN_APP)
- category: 通知分类
- is_active: 是否启用
```

#### 9. `email_logs` - 邮件发送记录表
**作用**: 记录邮件发送历史
```sql
- id: 邮件ID
- to_email: 收件人邮箱
- subject: 邮件主题
- content: 邮件内容
- template_id: 模板ID
- status: 发送状态(PENDING/SENT/DELIVERED/FAILED/BOUNCED)
- sent_at: 发送时间
- delivered_at: 送达时间
```

#### 10. `system_configs` - 系统配置表
**作用**: 存储系统配置参数
```sql
- id: 配置ID
- config_key: 配置键
- config_value: 配置值
- description: 配置描述
- category: 配置分类
- is_encrypted: 是否加密
```

### Agent服务相关表 (Agent Service)

#### 11. `ai_conversations` - AI对话记录表
**作用**: 管理AI对话会话
```sql
- id: 对话ID
- conversation_id: 外部对话ID
- user_id: 用户ID
- session_id: 会话ID
- status: 对话状态(ACTIVE/ENDED/SUSPENDED)
- created_at: 创建时间
- ended_at: 结束时间
```

#### 12. `ai_messages` - AI对话消息表
**作用**: 存储AI对话消息
```sql
- id: 消息ID
- conversation_id: 对话ID
- message_type: 消息类型(USER/ASSISTANT/SYSTEM)
- content: 消息内容
- metadata: 元数据(JSON)
- token_count: Token数量
- processing_time_ms: 处理时间
```

#### 13. `content_reviews` - 内容审核记录表
**作用**: 记录AI内容审核结果
```sql
- id: 审核ID
- content_id: 内容ID
- content_type: 内容类型(POST/COMMENT/MESSAGE)
- content_text: 审核内容
- review_result: 审核结果(APPROVED/REJECTED/PENDING/NEEDS_HUMAN)
- review_reason: 审核原因
- confidence_score: 置信度分数
- ai_model: AI模型
- processing_time_ms: 处理时间
- reviewed_by: 审核人ID
```

### 系统监控相关表

#### 14. `system_logs` - 系统日志表
**作用**: 记录系统运行日志
```sql
- id: 日志ID
- level: 日志级别(DEBUG/INFO/WARN/ERROR/FATAL)
- logger_name: 日志记录器
- message: 日志消息
- exception_info: 异常信息
- user_id: 用户ID
- ip_address: IP地址
- user_agent: 用户代理
- request_id: 请求ID
- service_name: 服务名称
```

#### 15. `audit_logs` - 操作审计表
**作用**: 记录用户操作审计
```sql
- id: 审计ID
- user_id: 用户ID
- action: 操作动作
- resource_type: 资源类型
- resource_id: 资源ID
- old_values: 旧值(JSON)
- new_values: 新值(JSON)
- ip_address: IP地址
- user_agent: 用户代理
- service_name: 服务名称
```

---

## 🔧 技术实现细节

### 1. 认证与授权
- **JWT Token**: 无状态认证，支持跨服务验证
- **密码加密**: BCrypt算法加密存储
- **会话管理**: Redis缓存用户会话信息
- **权限控制**: 基于角色的访问控制(RBAC)

### 2. 数据一致性
- **分布式事务**: 使用Seata分布式事务管理
- **数据同步**: Redis缓存与数据库双写一致性
- **消息队列**: RabbitMQ异步处理，保证最终一致性

### 3. 性能优化
- **缓存策略**: Redis多级缓存，热点数据预加载
- **数据库优化**: 索引优化，读写分离
- **接口限流**: 基于令牌桶算法的接口限流
- **CDN加速**: 静态资源CDN分发

### 4. 安全防护
- **内容审核**: AI+人工双重审核机制
- **SQL注入防护**: 参数化查询，ORM框架保护
- **XSS防护**: 输入输出过滤，CSP策略
- **CSRF防护**: Token验证机制

---

## 📊 业务功能模块

### 1. 用户管理模块
- 用户注册/登录/登出
- 个人信息管理
- 密码修改/找回
- 用户状态管理

### 2. 社区交流模块
- 帖子发布/编辑/删除
- 评论互动
- 点赞/收藏
- 用户关注/粉丝

### 3. 内容管理模块
- 分类管理
- 标签管理
- 内容审核
- 举报处理

### 4. 智能助手模块
- AI对话
- 内容生成
- 智能推荐
- 学习助手

### 5. 通知系统模块
- 实时通知
- 邮件通知
- 消息推送
- 通知模板

### 6. 系统管理模块
- 用户管理
- 内容管理
- 系统配置
- 数据统计

---

## 🚀 部署架构

### 云服务器部署
```
服务器: 8.148.189.41
├── Nacos (8848) - 服务注册与配置中心
├── MySQL (3306) - 关系型数据库
├── Redis (6379) - 缓存数据库
├── MongoDB (27017) - 文档数据库
├── RabbitMQ (5672) - 消息队列
└── 微服务集群
    ├── API Gateway (8080)
    ├── User Server (8081)
    ├── Community Server (8082)
    ├── Agent Server (8083)
    └── Notification Server (8084)
```

### Docker容器化
- 所有服务Docker化部署
- Docker Compose统一管理
- 容器资源限制和监控
- 自动重启和健康检查

---

## 📈 扩展性设计

### 1. 水平扩展
- 微服务独立扩展
- 数据库读写分离
- 缓存集群部署
- 负载均衡分发

### 2. 功能扩展
- 插件化架构
- 模块化设计
- API版本管理
- 第三方集成

### 3. 性能扩展
- 分布式缓存
- 数据库分库分表
- 消息队列集群
- CDN全球分发

---

## 🔍 监控与运维

### 1. 系统监控
- 服务健康检查
- 性能指标监控
- 错误日志收集
- 告警通知机制

### 2. 业务监控
- 用户行为分析
- 业务指标统计
- 异常情况预警
- 数据质量监控

### 3. 运维工具
- 自动化部署
- 配置管理
- 日志分析
- 性能调优

---

## 📝 总结

本项目采用现代化的微服务架构，通过合理的数据库设计和业务流程规划，为大学生提供了一个功能完善、性能优良、安全可靠的社区平台。系统具备良好的扩展性和维护性，能够满足大规模用户的使用需求。

**核心优势**:
- ✅ 微服务架构，高可用性
- ✅ 智能AI助手，提升用户体验
- ✅ 完善的内容审核机制
- ✅ 实时通知系统
- ✅ 云原生部署，易于扩展
- ✅ 全面的监控和运维支持
