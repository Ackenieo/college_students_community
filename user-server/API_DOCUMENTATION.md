# User-Server API 接口文档

## 接口概述

所有接口都已使用 VO 对象进行封装，统一使用 JSON 格式传递参数。

## 接口列表

### 1. 用户登录

**接口地址**: `POST /users/login`

**请求头**:
```
Content-Type: application/json
```

**请求体**:
```json
{
    "email": "1443762904@qq.com",
    "password": "123456"
}
```

**响应示例**:
```json
{
    "code": 200,
    "message": "登录成功",
    "data": {
        "token": "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...",
        "tokenType": "Bearer",
        "userId": 1,
        "username": "bom",
        "email": "1443762904@qq.com",
        "role": "STUDENT",
        "expiresAt": "2025-01-20 13:00:00"
    },
    "timestamp": 1760850764777,
    "success": true
}
```

### 2. 用户注册

**接口地址**: `POST /users/register`

**请求头**:
```
Content-Type: application/json
```

**请求体**:
```json
{
    "username": "bom",
    "email": "1443762904@qq.com",
    "password": "123456",
    "fullName": "张三",
    "phone": "13800138000",
    "code": "123456"
}
```

**响应示例**:
```json
{
    "code": 200,
    "message": "注册成功",
    "data": {
        "id": 1,
        "username": "bom",
        "email": "1443762904@qq.com",
        "fullName": "张三",
        "phone": "13800138000",
        "role": "STUDENT",
        "status": "ACTIVE",
        "createdAt": "2025-01-19 13:00:00",
        "lastLogin": null
    },
    "timestamp": 1760850764777,
    "success": true
}
```

**注意**: 注册接口现在返回 `RegisterUserResponseVO` 对象，其中 `role` 和 `status` 字段为字符串类型（枚举的 name() 值）。

### 3. 发送注册验证码

**接口地址**: `POST /users/send-register-code`

**请求头**:
```
Content-Type: application/json
```

**请求体**:
```json
{
    "email": "1443762904@qq.com"
}
```

**响应示例**:
```json
{
    "code": 200,
    "message": "验证码已发送",
    "data": "验证码已发送到您的邮箱",
    "timestamp": 1760850764777,
    "success": true
}
```

### 4. 发送密码重置验证码

**接口地址**: `POST /users/send-reset-code`

**请求头**:
```
Content-Type: application/json
```

**请求体**:
```json
{
    "email": "1443762904@qq.com"
}
```

**响应示例**:
```json
{
    "code": 200,
    "message": "验证码已发送",
    "data": "验证码已发送到您的邮箱",
    "timestamp": 1760850764777,
    "success": true
}
```

### 5. 重置密码

**接口地址**: `POST /users/reset-password`

**请求头**:
```
Content-Type: application/json
```

**请求体**:
```json
{
    "email": "1443762904@qq.com",
    "code": "123456",
    "newPassword": "newpassword123"
}
```

**响应示例**:
```json
{
    "code": 200,
    "message": "密码重置成功",
    "data": "密码重置成功，请使用新密码登录",
    "timestamp": 1760850764777,
    "success": true
}
```

## Postman 配置说明

### 统一配置

1. **请求头设置**:
   - 所有接口都需要设置 `Content-Type: application/json`

2. **Body 设置**:
   - 选择 `raw` 选项
   - 在右侧下拉菜单中选择 `JSON`
   - 输入对应的 JSON 数据

3. **URL 设置**:
   - 基础 URL: `localhost:8081`
   - 完整 URL: `localhost:8081/users/[接口路径]`

### 测试流程

1. **发送注册验证码** → **用户注册** → **用户登录**
2. **发送密码重置验证码** → **重置密码** → **用户登录**

## 注意事项

1. 所有接口都使用 JSON 格式传递参数
2. 验证码有效期为 5 分钟
3. JWT Token 有效期为 24 小时
4. 邮箱验证码长度固定为 6 位数字
