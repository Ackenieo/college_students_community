# 智能体服务使用指南

## 概述

智能体服务(Agent Server)是一个专门用于集成Dify智能体的微服务，提供智能对话、问答和历史记录管理等功能。

## 功能特性

- 🤖 **Dify智能体集成**: 与Dify平台无缝集成
- 💬 **智能对话**: 支持多轮对话和上下文理解
- 📝 **对话历史**: 自动保存和管理对话历史
- 🔄 **容错处理**: 熔断器和重试机制保证服务稳定性
- ⚡ **高性能**: 基于WebFlux的异步处理
- 🗄️ **缓存支持**: Redis缓存提升响应速度

## 技术架构

```
┌─────────────────┐    ┌─────────────────┐
│   客户端应用     │    │   移动端应用     │
└─────────┬───────┘    └─────────┬───────┘
          │                      │
          └──────────────────────┼──────────────────────┘
                                 │
                    ┌─────────────┴─────────────┐
                    │       API Gateway         │
                    │     (路由到agent服务)      │
                    └─────────────┬─────────────┘
                                  │
                    ┌─────────────┴─────────────┐
                    │      Agent Server         │
                    │         (8083)            │
                    │                           │
                    │ - Dify API调用            │
                    │ - 对话管理                │
                    │ - 缓存处理                │
                    │ - 熔断器保护              │
                    └─────────────┬─────────────┘
                                  │
                    ┌─────────────┴─────────────┐
                    │      Dify Platform        │
                    │         (AI引擎)          │
                    │                           │
                    │ - GPT模型                 │
                    │ - 智能体逻辑              │
                    │ - 知识库                  │
                    └───────────────────────────┘
```

## API接口

### 1. 智能对话

**POST** `/api/agent/chat`

发送消息给智能体进行对话。

**请求参数:**
```json
{
  "userId": "user123",           // 用户ID (必填)
  "query": "你好，请介绍一下Java", // 查询内容 (必填)
  "conversationId": "conv123"    // 对话ID (可选)
}
```

**响应示例:**
```json
{
  "id": "msg_123",
  "answer": "Java是一种面向对象的编程语言...",
  "conversationId": "conv_123",
  "status": "success",
  "timestamp": "2024-01-01T12:00:00"
}
```

### 2. 获取对话历史

**GET** `/api/agent/conversation/{conversationId}?userId={userId}`

获取指定对话的历史记录。

**响应示例:**
```json
{
  "id": "conv_123",
  "answer": "对话历史内容...",
  "conversationId": "conv_123",
  "status": "success",
  "timestamp": "2024-01-01T12:00:00"
}
```

### 3. 健康检查

**GET** `/api/agent/health`

检查服务健康状态。

**响应:**
```
Agent service is running
```

## 配置说明

### 应用配置 (application.yml)

```yaml
server:
  port: 8083

spring:
  application:
    name: agent-server
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
  redis:
    host: localhost
    port: 6379
    database: 4

# Dify配置
dify:
  api:
    base-url: http://localhost:5001/v1
    api-key: app-your-dify-api-key-here
    timeout: 30000
    max-retries: 3
  agent:
    default-model: gpt-3.5-turbo
    max-tokens: 1000
    temperature: 0.7
```

### 熔断器配置

```yaml
resilience4j:
  circuitbreaker:
    instances:
      difyAgent:
        slidingWindowSize: 10
        failureRateThreshold: 50
        waitDurationInOpenState: 10s
  retry:
    instances:
      difyAgent:
        maxAttempts: 3
        waitDuration: 1000ms
```

## 使用示例

### 1. 发送消息

```bash
curl -X POST http://localhost:8083/agent/chat \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "query": "请解释一下微服务架构的优势"
  }'
```

### 2. 继续对话

```bash
curl -X POST http://localhost:8083/agent/chat \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "query": "能举个例子吗？",
    "conversationId": "conv_123"
  }'
```

### 3. 获取对话历史

```bash
curl -X GET "http://localhost:8083/agent/conversation/conv_123?userId=user123"
```

## 集成Dify

### 1. 获取API Key

1. 登录Dify控制台
2. 创建应用
3. 在应用设置中获取API Key

### 2. 配置API Key

```yaml
dify:
  api:
    api-key: app-your-actual-api-key-here
```

### 3. 配置Dify URL

```yaml
dify:
  api:
    base-url: https://your-dify-instance.com/v1
```

## 部署说明

### Docker部署

```bash
# 构建镜像
docker build -t agent-server .

# 运行容器
docker run -d \
  --name agent-server \
  -p 8083:8083 \
  -e SPRING_PROFILES_ACTIVE=docker \
  -e DIFY_API_KEY=your-api-key \
  agent-server
```

### Docker Compose部署

```yaml
agent-server:
  build: .
  container_name: college-agent-server
  ports:
    - "8083:8083"
  environment:
    - SPRING_PROFILES_ACTIVE=docker
    - DIFY_API_KEY=your-dify-api-key
    - SPRING_REDIS_HOST=redis
  depends_on:
    - redis
    - nacos
```

## 监控和日志

### 健康检查

- **端点**: `/actuator/health`
- **熔断器状态**: `/actuator/circuitbreakers`
- **重试状态**: `/actuator/retries`

### 日志配置

```yaml
logging:
  level:
    org.example.agentserver: DEBUG
    org.springframework.cloud: DEBUG
```

## 故障排除

### 常见问题

1. **Dify API调用失败**
   - 检查API Key是否正确
   - 确认Dify服务是否可访问
   - 查看网络连接

2. **熔断器触发**
   - 检查Dify服务状态
   - 调整熔断器阈值
   - 查看错误日志

3. **Redis连接失败**
   - 确认Redis服务运行状态
   - 检查连接配置
   - 验证网络连通性

### 调试模式

启用调试日志：

```yaml
logging:
  level:
    org.example.agentserver: DEBUG
    org.springframework.web.reactive.function.client: DEBUG
```

## 最佳实践

1. **API Key管理**: 使用环境变量或配置中心管理敏感信息
2. **错误处理**: 实现优雅的降级策略
3. **缓存策略**: 合理使用Redis缓存提升性能
4. **监控告警**: 设置熔断器状态监控
5. **日志管理**: 记录关键操作和错误信息

## 扩展功能

- 支持多种AI模型
- 实现对话分析
- 添加用户偏好设置
- 支持文件上传和处理
- 集成更多第三方AI服务
