# 多阶段构建，用于构建所有微服务
FROM maven:3.9.4-openjdk-17-slim AS builder

WORKDIR /app
COPY pom.xml .
COPY . .

# 构建所有服务
RUN mvn clean package -DskipTests

# 基础镜像
FROM openjdk:17-jre-slim

# 安装必要的工具
RUN apt-get update && apt-get install -y \
    curl \
    && rm -rf /var/lib/apt/lists/*

# 创建应用目录
WORKDIR /app

# 复制构建的jar文件
COPY --from=builder /app/config-server/target/*.jar config-server.jar
COPY --from=builder /app/api-gateway/target/*.jar api-gateway.jar
COPY --from=builder /app/user-server/target/*.jar user-server.jar
COPY --from=builder /app/community-server/target/*.jar community-server.jar
COPY --from=builder /app/agent-server/target/*.jar agent-server.jar
COPY --from=builder /app/notification-server/target/*.jar notification-server.jar

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# 默认启动API Gateway
CMD ["java", "-jar", "api-gateway.jar"]
