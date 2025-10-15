# 大学学生项目微服务架构

这是一个基于Spring Cloud的微服务架构项目，为大学学生管理系统提供完整的后端服务。

## 架构概览

### 微服务组件

1. **Nacos Server** (8848) - 服务注册与发现中心
2. **Config Server** (8888) - 配置中心
3. **API Gateway** (8080) - API网关
4. **User Server** (8081) - 用户服务
5. **Community Server** (8082) - 社区服务
6. **Agent Server** (8083) - 智能体服务
7. **Notification Server** (8084) - 通知服务

### 技术栈

- **框架**: Spring Boot 3.2.0, Spring Cloud 2023.0.0
- **服务发现**: Alibaba Nacos
- **配置中心**: Spring Cloud Config
- **API网关**: Spring Cloud Gateway
- **服务间通信**: OpenFeign
- **熔断器**: Resilience4j
- **数据库**: MySQL 8.0, MongoDB 6.0
- **缓存**: Redis 7
- **消息队列**: RabbitMQ 3
- **容器化**: Docker & Docker Compose

## 快速开始

### 前置要求

- Docker & Docker Compose
- JDK 17+
- Maven 3.6+

### 启动服务

#### 方式一：使用Docker Compose（推荐）

```bash
# Linux/Mac
chmod +x start-services.sh
./start-services.sh

# Windows
start-services.bat
```

#### 方式二：手动启动

```bash
# 启动基础设施服务
docker-compose up -d mysql redis mongodb rabbitmq

# 启动微服务
docker-compose up -d nacos config-server api-gateway user-server community-server agent-server notification-server
```

### 服务访问地址

- **Nacos服务注册中心**: http://localhost:8848/nacos (nacos/nacos)
- **配置中心**: http://localhost:8888
- **API网关**: http://localhost:8080
- **用户服务**: http://localhost:8081
- **社区服务**: http://localhost:8082
- **智能体服务**: http://localhost:8083
- **通知服务**: http://localhost:8084
- **RabbitMQ管理界面**: http://localhost:15672 (guest/guest)

## 项目结构

```
college-students-microservices/
├── config-server/              # 配置中心
├── api-gateway/                # API网关
├── user-server/                # 用户服务
├── community-server/           # 社区服务
├── agent-server/               # 智能体服务
├── notification-server/        # 通知服务
├── sql/                        # 数据库初始化脚本
├── docker-compose.yml          # Docker编排文件
├── Dockerfile                  # Docker构建文件
├── start-services.sh           # Linux启动脚本
├── start-services.bat          # Windows启动脚本
└── README.md                   # 项目文档
```

## 微服务详细说明

### 用户服务 (User Server)

负责用户管理功能：
- 用户注册、登录、信息管理
- 用户角色管理（管理员、教师、学生）
- 用户状态管理
- Redis缓存用户信息

**主要API**:
- `POST /users` - 创建用户
- `GET /users/{id}` - 获取用户信息
- `GET /users/username/{username}` - 根据用户名获取用户
- `GET /users/role/{role}` - 根据角色获取用户列表
- `PUT /users/{id}` - 更新用户信息
- `DELETE /users/{id}` - 删除用户

### 社区服务 (Community Server)

负责社区功能：
- 帖子发布与管理
- 评论系统
- 点赞功能
- 使用MongoDB存储非结构化数据

### 智能体服务 (Agent Server)

负责AI智能体功能：
- Dify智能体集成
- 智能对话服务
- 对话历史管理
- 智能问答支持

### 通知服务 (Notification Server)

负责通知功能：
- 实时通知推送
- WebSocket连接管理
- 消息队列处理
- 使用RabbitMQ进行异步消息处理

## 开发指南

### 本地开发

1. 启动基础设施服务：
```bash
docker-compose up -d mysql redis mongodb rabbitmq
```

2. 启动Nacos Server：
```bash
# 使用Docker启动Nacos
docker run --name nacos -e MODE=standalone -p 8848:8848 -p 9848:9848 nacos/nacos-server:v2.2.3
```

3. 启动Config Server：
```bash
cd config-server
mvn spring-boot:run
```

4. 启动其他微服务：
```bash
cd user-server
mvn spring-boot:run
```

### 配置管理

配置文件通过Config Server统一管理，支持多环境配置：
- `application-dev.yml` - 开发环境
- `application-prod.yml` - 生产环境
- `application-docker.yml` - Docker环境

### 服务间通信

使用OpenFeign进行服务间调用：

```java
@FeignClient(name = "user-server")
public interface UserServiceClient {
    @GetMapping("/users/{id}")
    UserDTO getUserById(@PathVariable Long id);
}
```

### 熔断器配置

使用Resilience4j实现熔断器模式：

```yaml
resilience4j:
  circuitbreaker:
    instances:
      userService:
        slidingWindowSize: 10
        failureRateThreshold: 50
        waitDurationInOpenState: 10s
```

## 监控与运维

### 健康检查

所有服务都集成了Spring Boot Actuator，提供健康检查端点：
- `GET /actuator/health` - 健康状态
- `GET /actuator/info` - 服务信息
- `GET /actuator/metrics` - 性能指标

### 日志管理

建议使用ELK Stack或类似工具进行日志聚合和分析。

### 监控告警

建议集成Prometheus + Grafana进行监控和告警。

## 部署指南

### 生产环境部署

1. 修改配置文件中的数据库连接信息
2. 配置适当的资源限制
3. 设置环境变量
4. 使用Docker Swarm或Kubernetes进行编排

### 扩展性

- 水平扩展：每个服务都可以独立扩展
- 负载均衡：通过API Gateway实现
- 数据库分片：支持读写分离和分库分表

## 常见问题

### Q: 服务启动失败怎么办？
A: 检查Docker是否正常运行，查看服务日志：`docker-compose logs [服务名]`

### Q: 如何添加新的微服务？
A: 1. 创建新的服务模块 2. 在父POM中添加模块 3. 在docker-compose.yml中添加服务配置

### Q: 如何修改数据库配置？
A: 修改对应服务的application.yml文件中的数据库连接配置

## 贡献指南

1. Fork项目
2. 创建功能分支
3. 提交更改
4. 推送到分支
5. 创建Pull Request

## 许可证

MIT License

## 联系方式

如有问题，请提交Issue或联系项目维护者。
