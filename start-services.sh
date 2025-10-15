#!/bin/bash

echo "启动大学学生项目微服务架构..."

# 检查Docker是否运行
if ! docker info > /dev/null 2>&1; then
    echo "错误: Docker未运行，请先启动Docker"
    exit 1
fi

# 停止并删除现有容器
echo "清理现有容器..."
docker-compose down -v

# 构建并启动服务
echo "构建并启动所有服务..."
docker-compose up --build -d

# 等待服务启动
echo "等待服务启动..."
sleep 30

# 检查服务状态
echo "检查服务状态..."
docker-compose ps

echo "微服务启动完成！"
echo ""
echo "服务访问地址："
echo "- Nacos服务注册中心: http://localhost:8848/nacos (nacos/nacos)"
echo "- 配置中心: http://localhost:8888"
echo "- API网关: http://localhost:8080"
echo "- 用户服务: http://localhost:8081"
echo "- 社区服务: http://localhost:8082"
echo "- 智能体服务: http://localhost:8083"
echo "- 通知服务: http://localhost:8084"
echo "- RabbitMQ管理界面: http://localhost:15672 (guest/guest)"
echo ""
echo "查看日志: docker-compose logs -f [服务名]"
echo "停止服务: docker-compose down"
