@echo off
echo 启动大学学生项目微服务架构...

REM 检查Docker是否运行
docker info >nul 2>&1
if errorlevel 1 (
    echo 错误: Docker未运行，请先启动Docker
    pause
    exit /b 1
)

REM 停止并删除现有容器
echo 清理现有容器...
docker-compose down -v

REM 构建并启动服务
echo 构建并启动所有服务...
docker-compose up --build -d

REM 等待服务启动
echo 等待服务启动...
timeout /t 30 /nobreak >nul

REM 检查服务状态
echo 检查服务状态...
docker-compose ps

echo 微服务启动完成！
echo.
echo 服务访问地址：
echo - Nacos服务注册中心: http://localhost:8848/nacos ^(nacos/nacos^)
echo - 配置中心: http://localhost:8888
echo - API网关: http://localhost:8080
echo - 用户服务: http://localhost:8081
echo - 社区服务: http://localhost:8082
echo - 智能体服务: http://localhost:8083
echo - 通知服务: http://localhost:8084
echo - RabbitMQ管理界面: http://localhost:15672 ^(guest/guest^)
echo.
echo 查看日志: docker-compose logs -f [服务名]
echo 停止服务: docker-compose down
pause
