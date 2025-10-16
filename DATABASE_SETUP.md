# 数据库初始化指南

本文档提供了大学生社区平台数据库的完整初始化指南，包括MySQL、MongoDB和Redis的配置说明。

## 📋 目录

- [系统要求](#系统要求)
- [MySQL 数据库设置](#mysql-数据库设置)
- [MongoDB 数据库设置](#mongodb-数据库设置)
- [Redis 缓存设置](#redis-缓存设置)
- [数据备份策略](#数据备份策略)
- [性能优化建议](#性能优化建议)
- [故障排除](#故障排除)

## 🖥️ 系统要求

### 硬件要求
- **CPU**: 4核心以上
- **内存**: 8GB以上
- **存储**: 100GB以上SSD
- **网络**: 千兆网络

### 软件要求
- **操作系统**: Linux (Ubuntu 20.04+ / CentOS 7+)
- **MySQL**: 8.0+
- **MongoDB**: 5.0+
- **Redis**: 6.0+
- **Java**: 17+

## 🗄️ MySQL 数据库设置

### 1. 安装 MySQL 8.0

```bash
# Ubuntu/Debian
sudo apt update
sudo apt install mysql-server-8.0

# CentOS/RHEL
sudo yum install mysql-server
```

### 2. 启动 MySQL 服务

```bash
sudo systemctl start mysql
sudo systemctl enable mysql
```

### 3. 安全配置

```bash
sudo mysql_secure_installation
```

### 4. 执行数据库初始化脚本

```bash
# 登录MySQL
mysql -u root -p

# 执行初始化脚本
source /path/to/database_schema.sql
```

### 5. 验证安装

```bash
# 检查数据库
mysql -u college_app -p -e "SHOW DATABASES;"

# 检查表结构
mysql -u college_app -p college_students -e "SHOW TABLES;"
```

### 6. MySQL 配置优化

创建 `/etc/mysql/mysql.conf.d/college_students.cnf`:

```ini
[mysqld]
# 基本配置
port = 3306
bind-address = 0.0.0.0
max_connections = 1000
max_user_connections = 500

# 字符集配置
character-set-server = utf8mb4
collation-server = utf8mb4_unicode_ci

# 缓存配置
innodb_buffer_pool_size = 2G
innodb_log_file_size = 256M
innodb_log_buffer_size = 16M
query_cache_size = 128M
query_cache_type = 1

# 连接配置
wait_timeout = 28800
interactive_timeout = 28800

# 日志配置
log-error = /var/log/mysql/error.log
slow_query_log = 1
slow_query_log_file = /var/log/mysql/slow.log
long_query_time = 2

# 二进制日志
log-bin = mysql-bin
binlog_format = ROW
expire_logs_days = 7

# 安全配置
sql_mode = STRICT_TRANS_TABLES,NO_ZERO_DATE,NO_ZERO_IN_DATE,ERROR_FOR_DIVISION_BY_ZERO
```

## 🍃 MongoDB 数据库设置

### 1. 安装 MongoDB 5.0

```bash
# Ubuntu/Debian
wget -qO - https://www.mongodb.org/static/pgp/server-5.0.asc | sudo apt-key add -
echo "deb [ arch=amd64,arm64 ] https://repo.mongodb.org/apt/ubuntu focal/mongodb-org/5.0 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-5.0.list
sudo apt update
sudo apt install mongodb-org

# CentOS/RHEL
sudo yum install mongodb-org
```

### 2. 启动 MongoDB 服务

```bash
sudo systemctl start mongod
sudo systemctl enable mongod
```

### 3. 执行 MongoDB 初始化脚本

```bash
# 执行初始化脚本
mongo /path/to/mongodb_init.js
```

### 4. 验证安装

```bash
# 连接MongoDB
mongo

# 检查数据库
use college_students_community
show collections

# 检查索引
db.posts.getIndexes()
```

### 5. MongoDB 配置优化

创建 `/etc/mongod.conf`:

```yaml
storage:
  dbPath: /var/lib/mongo
  journal:
    enabled: true
  wiredTiger:
    engineConfig:
      cacheSizeGB: 2

systemLog:
  destination: file
  logAppend: true
  path: /var/log/mongodb/mongod.log

net:
  port: 27017
  bindIp: 0.0.0.0

processManagement:
  timeZoneInfo: /usr/share/zoneinfo

security:
  authorization: enabled

operationProfiling:
  slowOpThresholdMs: 100
  mode: slowOp

replication:
  replSetName: "rs0"
```

## 🔴 Redis 缓存设置

### 1. 安装 Redis 6.0

```bash
# Ubuntu/Debian
sudo apt update
sudo apt install redis-server

# CentOS/RHEL
sudo yum install redis
```

### 2. 配置 Redis

```bash
# 复制配置文件
sudo cp /path/to/redis_config.conf /etc/redis/redis.conf

# 启动Redis服务
sudo systemctl start redis
sudo systemctl enable redis
```

### 3. 验证安装

```bash
# 连接Redis
redis-cli

# 测试连接
ping
# 应该返回 PONG

# 检查配置
config get "*"
```

### 4. Redis 配置优化

创建 `/etc/redis/college_students.conf`:

```conf
# 内存配置
maxmemory 2gb
maxmemory-policy allkeys-lru

# 持久化配置
save 900 1
save 300 10
save 60 10000

# 网络配置
timeout 300
tcp-keepalive 60
maxclients 10000

# 安全配置
requirepass CollegeRedis2024!
```

## 💾 数据备份策略

### 1. MySQL 备份

```bash
#!/bin/bash
# mysql_backup.sh

DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="/backup/mysql"
DB_NAME="college_students"

# 创建备份目录
mkdir -p $BACKUP_DIR

# 执行备份
mysqldump -u college_app -p$MYSQL_PASSWORD \
  --single-transaction \
  --routines \
  --triggers \
  --events \
  $DB_NAME > $BACKUP_DIR/${DB_NAME}_${DATE}.sql

# 压缩备份文件
gzip $BACKUP_DIR/${DB_NAME}_${DATE}.sql

# 删除7天前的备份
find $BACKUP_DIR -name "*.sql.gz" -mtime +7 -delete

echo "MySQL backup completed: ${DB_NAME}_${DATE}.sql.gz"
```

### 2. MongoDB 备份

```bash
#!/bin/bash
# mongodb_backup.sh

DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="/backup/mongodb"
DB_NAME="college_students_community"

# 创建备份目录
mkdir -p $BACKUP_DIR

# 执行备份
mongodump --db $DB_NAME \
  --out $BACKUP_DIR/${DB_NAME}_${DATE}

# 压缩备份文件
tar -czf $BACKUP_DIR/${DB_NAME}_${DATE}.tar.gz \
  -C $BACKUP_DIR ${DB_NAME}_${DATE}

# 删除原目录
rm -rf $BACKUP_DIR/${DB_NAME}_${DATE}

# 删除7天前的备份
find $BACKUP_DIR -name "*.tar.gz" -mtime +7 -delete

echo "MongoDB backup completed: ${DB_NAME}_${DATE}.tar.gz"
```

### 3. Redis 备份

```bash
#!/bin/bash
# redis_backup.sh

DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="/backup/redis"

# 创建备份目录
mkdir -p $BACKUP_DIR

# 执行备份
redis-cli --rdb $BACKUP_DIR/dump_${DATE}.rdb

# 压缩备份文件
gzip $BACKUP_DIR/dump_${DATE}.rdb

# 删除7天前的备份
find $BACKUP_DIR -name "dump_*.rdb.gz" -mtime +7 -delete

echo "Redis backup completed: dump_${DATE}.rdb.gz"
```

### 4. 设置定时备份

```bash
# 编辑crontab
crontab -e

# 添加定时任务
# 每天凌晨2点执行MySQL备份
0 2 * * * /path/to/mysql_backup.sh

# 每天凌晨3点执行MongoDB备份
0 3 * * * /path/to/mongodb_backup.sh

# 每天凌晨4点执行Redis备份
0 4 * * * /path/to/redis_backup.sh
```

## ⚡ 性能优化建议

### 1. MySQL 优化

- **索引优化**: 为常用查询字段创建合适的索引
- **查询优化**: 使用EXPLAIN分析查询计划
- **连接池**: 配置合适的连接池大小
- **缓存**: 启用查询缓存和InnoDB缓冲池

### 2. MongoDB 优化

- **索引策略**: 为查询模式创建复合索引
- **分片**: 在数据量大时考虑分片
- **副本集**: 配置副本集提高可用性
- **压缩**: 启用数据压缩节省存储空间

### 3. Redis 优化

- **内存管理**: 合理设置最大内存和淘汰策略
- **持久化**: 根据需求选择RDB或AOF
- **集群**: 在需要时配置Redis集群
- **监控**: 使用Redis监控工具

## 🔧 故障排除

### 1. MySQL 常见问题

**连接数过多**:
```sql
-- 查看当前连接数
SHOW PROCESSLIST;

-- 修改最大连接数
SET GLOBAL max_connections = 1000;
```

**慢查询优化**:
```sql
-- 启用慢查询日志
SET GLOBAL slow_query_log = 'ON';
SET GLOBAL long_query_time = 2;

-- 查看慢查询
SHOW VARIABLES LIKE 'slow_query_log_file';
```

### 2. MongoDB 常见问题

**内存不足**:
```javascript
// 查看内存使用情况
db.serverStatus().mem

// 查看集合统计信息
db.posts.stats()
```

**索引优化**:
```javascript
// 查看查询计划
db.posts.find({authorId: 1}).explain()

// 创建复合索引
db.posts.createIndex({authorId: 1, status: 1, createdAt: -1})
```

### 3. Redis 常见问题

**内存使用过高**:
```bash
# 查看内存使用情况
redis-cli info memory

# 查看大键
redis-cli --bigkeys

# 手动触发内存淘汰
redis-cli config set maxmemory-policy allkeys-lru
```

## 📊 监控和维护

### 1. 数据库监控

- **MySQL**: 使用MySQL Workbench或phpMyAdmin
- **MongoDB**: 使用MongoDB Compass或MongoDB Atlas
- **Redis**: 使用RedisInsight或redis-cli

### 2. 性能监控

```bash
# MySQL性能监控
mysqladmin -u root -p status
mysqladmin -u root -p processlist

# MongoDB性能监控
mongostat
mongotop

# Redis性能监控
redis-cli info
redis-cli monitor
```

### 3. 日志管理

- **MySQL日志**: `/var/log/mysql/`
- **MongoDB日志**: `/var/log/mongodb/`
- **Redis日志**: `/var/log/redis/`

## 🔐 安全建议

1. **访问控制**: 使用强密码和最小权限原则
2. **网络安全**: 配置防火墙和SSL/TLS
3. **定期更新**: 保持数据库软件最新版本
4. **备份验证**: 定期验证备份文件的完整性
5. **监控告警**: 设置异常情况告警机制

## 📞 技术支持

如果在数据库初始化过程中遇到问题，请：

1. 检查系统日志文件
2. 验证配置文件语法
3. 确认服务状态
4. 查看错误日志
5. 联系技术支持团队

---

**注意**: 请根据实际环境调整配置参数，确保在生产环境中进行充分的测试。
