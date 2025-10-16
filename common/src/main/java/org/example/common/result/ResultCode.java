package org.example.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应码枚举
 */
@Getter
@AllArgsConstructor
public enum ResultCode {
    
    // 成功
    SUCCESS(200, "操作成功"),
    
    // 客户端错误
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    CONFLICT(409, "资源冲突"),
    VALIDATION_ERROR(422, "参数校验失败"),
    
    // 服务器错误
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务不可用"),
    GATEWAY_TIMEOUT(504, "网关超时"),
    
    // 业务错误码
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_ALREADY_EXISTS(1002, "用户已存在"),
    INVALID_CREDENTIALS(1003, "用户名或密码错误"),
    TOKEN_EXPIRED(1004, "令牌已过期"),
    TOKEN_INVALID(1005, "令牌无效"),
    EMAIL_NOT_VERIFIED(1006, "邮箱未验证"),
    VERIFICATION_CODE_EXPIRED(1007, "验证码已过期"),
    VERIFICATION_CODE_INVALID(1008, "验证码无效"),
    
    // 帖子相关错误码
    POST_NOT_FOUND(2001, "帖子不存在"),
    POST_DELETED(2002, "帖子已删除"),
    POST_PERMISSION_DENIED(2003, "无权限操作此帖子"),
    CONTENT_SENSITIVE(2004, "内容包含敏感信息"),
    
    // 好友相关错误码
    FRIENDSHIP_NOT_FOUND(3001, "好友关系不存在"),
    FRIENDSHIP_ALREADY_EXISTS(3002, "好友关系已存在"),
    FRIEND_REQUEST_NOT_FOUND(3003, "好友请求不存在"),
    
    // 消息相关错误码
    MESSAGE_NOT_FOUND(4001, "消息不存在"),
    MESSAGE_SEND_FAILED(4002, "消息发送失败"),
    
    // 通知相关错误码
    NOTIFICATION_NOT_FOUND(5001, "通知不存在"),
    EMAIL_SEND_FAILED(5002, "邮件发送失败");
    
    /**
     * 响应码
     */
    private final Integer code;
    
    /**
     * 响应消息
     */
    private final String message;
}
