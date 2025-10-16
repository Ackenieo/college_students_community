package org.example.common.constant;

/**
 * 通用常量类
 */
public class CommonConstants {
    
    /**
     * 用户状态
     */
    public static class UserStatus {
        public static final Integer DISABLED = 0;  // 禁用
        public static final Integer ENABLED = 1;   // 启用
    }
    
    /**
     * 邮箱验证状态
     */
    public static class EmailVerified {
        public static final Integer NOT_VERIFIED = 0;  // 未验证
        public static final Integer VERIFIED = 1;      // 已验证
    }
    
    /**
     * 性别
     */
    public static class Gender {
        public static final Integer UNKNOWN = 0;  // 未知
        public static final Integer MALE = 1;     // 男
        public static final Integer FEMALE = 2;   // 女
    }
    
    /**
     * 验证码类型
     */
    public static class VerificationCodeType {
        public static final String REGISTER = "register";  // 注册
        public static final String RESET_PASSWORD = "reset";  // 重置密码
    }
    
    /**
     * 好友关系状态
     */
    public static class FriendshipStatus {
        public static final Integer PENDING = 0;   // 待处理
        public static final Integer ACCEPTED = 1;  // 已接受
        public static final Integer REJECTED = 2;  // 已拒绝
        public static final Integer BLOCKED = 3;   // 已屏蔽
    }
    
    /**
     * 消息类型
     */
    public static class MessageType {
        public static final String TEXT = "text";        // 文本
        public static final String IMAGE = "image";      // 图片
        public static final String FILE = "file";        // 文件
        public static final String SYSTEM = "system";    // 系统消息
    }
    
    /**
     * 通知类型
     */
    public static class NotificationType {
        public static final String LIKE = "like";           // 点赞
        public static final String COMMENT = "comment";     // 评论
        public static final String FOLLOW = "follow";       // 关注
        public static final String MESSAGE = "message";     // 消息
        public static final String SYSTEM = "system";       // 系统通知
    }
    
    /**
     * 帖子状态
     */
    public static class PostStatus {
        public static final Integer DRAFT = 0;      // 草稿
        public static final Integer PUBLISHED = 1;  // 已发布
        public static final Integer DELETED = 2;    // 已删除
        public static final Integer BLOCKED = 3;    // 已屏蔽
    }
    
    /**
     * 内容审核状态
     */
    public static class ContentReviewStatus {
        public static final Integer PENDING = 0;    // 待审核
        public static final Integer APPROVED = 1;   // 已通过
        public static final Integer REJECTED = 2;   // 已拒绝
    }
    
    /**
     * 默认分页参数
     */
    public static class PageConstants {
        public static final Integer DEFAULT_PAGE_NUM = 1;
        public static final Integer DEFAULT_PAGE_SIZE = 10;
        public static final Integer MAX_PAGE_SIZE = 100;
    }
    
    /**
     * 缓存键前缀
     */
    public static class CacheKeyPrefix {
        public static final String USER = "user:";
        public static final String VERIFICATION_CODE = "verify_code:";
        public static final String JWT_TOKEN = "jwt_token:";
        public static final String POST = "post:";
        public static final String FRIENDSHIP = "friendship:";
    }
    
    /**
     * 缓存过期时间（秒）
     */
    public static class CacheExpire {
        public static final Long VERIFICATION_CODE = 600L;      // 验证码10分钟
        public static final Long JWT_TOKEN = 86400L;           // JWT令牌24小时
        public static final Long USER_INFO = 3600L;            // 用户信息1小时
        public static final Long POST_CACHE = 1800L;           // 帖子缓存30分钟
    }
}
