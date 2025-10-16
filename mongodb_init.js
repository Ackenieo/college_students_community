// =============================================
// 大学生社区平台 MongoDB 初始化脚本
// MongoDB Initialization Script for College Students Community Platform
// =============================================

// 创建数据库
db = db.getSiblingDB('college_students_community');

// =============================================
// 创建集合和索引
// =============================================

// 帖子集合
db.createCollection('posts');

// 为帖子集合创建索引
db.posts.createIndex({ "authorId": 1 });
db.posts.createIndex({ "status": 1 });
db.posts.createIndex({ "createdAt": -1 });
db.posts.createIndex({ "publishedAt": -1 });
db.posts.createIndex({ "likeCount": -1 });
db.posts.createIndex({ "tags": 1 });
db.posts.createIndex({ "title": "text", "content": "text" }); // 全文搜索索引
db.posts.createIndex({ "authorId": 1, "status": 1 });
db.posts.createIndex({ "status": 1, "createdAt": -1 });
db.posts.createIndex({ "status": 1, "publishedAt": -1 });

// 评论集合
db.createCollection('comments');

// 为评论集合创建索引
db.comments.createIndex({ "postId": 1 });
db.comments.createIndex({ "authorId": 1 });
db.comments.createIndex({ "createdAt": -1 });
db.comments.createIndex({ "parentCommentId": 1 });
db.comments.createIndex({ "postId": 1, "createdAt": -1 });
db.comments.createIndex({ "postId": 1, "parentCommentId": 1 });

// 点赞集合
db.createCollection('likes');

// 为点赞集合创建索引
db.likes.createIndex({ "userId": 1 });
db.likes.createIndex({ "targetId": 1 });
db.likes.createIndex({ "targetType": 1 });
db.likes.createIndex({ "createdAt": -1 });
db.likes.createIndex({ "userId": 1, "targetId": 1, "targetType": 1 }, { unique: true });

// 收藏集合
db.createCollection('favorites');

// 为收藏集合创建索引
db.favorites.createIndex({ "userId": 1 });
db.favorites.createIndex({ "postId": 1 });
db.favorites.createIndex({ "createdAt": -1 });
db.favorites.createIndex({ "userId": 1, "postId": 1 }, { unique: true });

// 好友关系集合
db.createCollection('friendships');

// 为好友关系集合创建索引
db.friendships.createIndex({ "userId": 1 });
db.friendships.createIndex({ "friendId": 1 });
db.friendships.createIndex({ "status": 1 });
db.friendships.createIndex({ "createdAt": -1 });
db.friendships.createIndex({ "userId": 1, "friendId": 1 }, { unique: true });
db.friendships.createIndex({ "friendId": 1, "status": 1 });

// 消息集合
db.createCollection('messages');

// 为消息集合创建索引
db.messages.createIndex({ "senderId": 1 });
db.messages.createIndex({ "receiverId": 1 });
db.messages.createIndex({ "createdAt": -1 });
db.messages.createIndex({ "status": 1 });
db.messages.createIndex({ "senderId": 1, "receiverId": 1 });
db.messages.createIndex({ "receiverId": 1, "status": 1 });

// 通知集合
db.createCollection('notifications');

// 为通知集合创建索引
db.notifications.createIndex({ "receiverId": 1 });
db.notifications.createIndex({ "type": 1 });
db.notifications.createIndex({ "status": 1 });
db.notifications.createIndex({ "createdAt": -1 });
db.notifications.createIndex({ "expireAt": 1 });
db.notifications.createIndex({ "receiverId": 1, "status": 1 });
db.notifications.createIndex({ "receiverId": 1, "createdAt": -1 });

// 文件上传记录集合
db.createCollection('file_uploads');

// 为文件上传集合创建索引
db.file_uploads.createIndex({ "uploaderId": 1 });
db.file_uploads.createIndex({ "fileType": 1 });
db.file_uploads.createIndex({ "uploadedAt": -1 });
db.file_uploads.createIndex({ "status": 1 });

// =============================================
// 插入示例数据
// =============================================

// 插入示例帖子
db.posts.insertMany([
    {
        "authorId": NumberLong(1),
        "authorUsername": "admin",
        "authorEmail": "admin@college.edu.cn",
        "title": "欢迎来到大学生社区平台！",
        "content": "欢迎各位同学加入我们的社区平台！这里是一个开放、友好的交流空间，大家可以分享学习经验、生活感悟，结识志同道合的朋友。",
        "images": [],
        "tags": ["欢迎", "社区"],
        "status": "APPROVED",
        "reviewResult": "APPROVED",
        "likeCount": 0,
        "commentCount": 0,
        "shareCount": 0,
        "createdAt": new Date(),
        "updatedAt": new Date(),
        "publishedAt": new Date()
    },
    {
        "authorId": NumberLong(1),
        "authorUsername": "admin",
        "authorEmail": "admin@college.edu.cn",
        "title": "学习交流专区使用指南",
        "content": "学习交流专区是大家分享学习经验、讨论课程内容、寻求帮助的地方。请遵守社区规范，友好交流。",
        "images": [],
        "tags": ["学习", "指南"],
        "status": "APPROVED",
        "reviewResult": "APPROVED",
        "likeCount": 0,
        "commentCount": 0,
        "shareCount": 0,
        "createdAt": new Date(),
        "updatedAt": new Date(),
        "publishedAt": new Date()
    }
]);

// 插入示例通知
db.notifications.insertMany([
    {
        "receiverId": NumberLong(1),
        "title": "欢迎使用大学生社区平台",
        "content": "欢迎您加入我们的社区平台！您可以在这里分享学习经验、结交朋友、参与讨论。",
        "type": "GENERAL",
        "priority": "NORMAL",
        "category": "welcome",
        "status": "UNREAD",
        "createdAt": new Date()
    },
    {
        "receiverId": NumberLong(1),
        "title": "系统通知",
        "content": "社区平台已正式上线，欢迎大家积极参与！",
        "type": "SYSTEM",
        "priority": "HIGH",
        "category": "system",
        "status": "UNREAD",
        "createdAt": new Date()
    }
]);

// =============================================
// 创建聚合管道视图
// =============================================

// 热门帖子视图
db.createView(
    "hot_posts",
    "posts",
    [
        { $match: { "status": "APPROVED" } },
        { $sort: { "likeCount": -1, "createdAt": -1 } },
        { $limit: 100 }
    ]
);

// 用户活跃度统计视图
db.createView(
    "user_activity_stats",
    "posts",
    [
        {
            $group: {
                "_id": "$authorId",
                "username": { $first: "$authorUsername" },
                "postCount": { $sum: 1 },
                "totalLikes": { $sum: "$likeCount" },
                "lastPostDate": { $max: "$createdAt" }
            }
        },
        { $sort: { "postCount": -1 } }
    ]
);

// 标签使用统计视图
db.createView(
    "tag_usage_stats",
    "posts",
    [
        { $unwind: "$tags" },
        {
            $group: {
                "_id": "$tags",
                "usageCount": { $sum: 1 },
                "avgLikes": { $avg: "$likeCount" }
            }
        },
        { $sort: { "usageCount": -1 } }
    ]
);

// =============================================
// 创建数据验证规则
// =============================================

// 为帖子集合添加验证规则
db.runCommand({
    collMod: "posts",
    validator: {
        $jsonSchema: {
            bsonType: "object",
            required: ["authorId", "authorUsername", "authorEmail", "title", "content", "status", "createdAt"],
            properties: {
                authorId: {
                    bsonType: "long",
                    description: "作者ID必须是数字"
                },
                authorUsername: {
                    bsonType: "string",
                    minLength: 1,
                    maxLength: 50,
                    description: "作者用户名必须是1-50个字符的字符串"
                },
                authorEmail: {
                    bsonType: "string",
                    pattern: "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
                    description: "作者邮箱必须是有效的邮箱格式"
                },
                title: {
                    bsonType: "string",
                    minLength: 1,
                    maxLength: 200,
                    description: "标题必须是1-200个字符的字符串"
                },
                content: {
                    bsonType: "string",
                    minLength: 1,
                    maxLength: 10000,
                    description: "内容必须是1-10000个字符的字符串"
                },
                status: {
                    enum: ["PENDING", "APPROVED", "REJECTED", "DELETED"],
                    description: "状态必须是枚举值之一"
                },
                likeCount: {
                    bsonType: "int",
                    minimum: 0,
                    description: "点赞数必须是非负整数"
                },
                commentCount: {
                    bsonType: "int",
                    minimum: 0,
                    description: "评论数必须是非负整数"
                },
                shareCount: {
                    bsonType: "int",
                    minimum: 0,
                    description: "分享数必须是非负整数"
                }
            }
        }
    }
});

// 为评论集合添加验证规则
db.runCommand({
    collMod: "comments",
    validator: {
        $jsonSchema: {
            bsonType: "object",
            required: ["postId", "authorId", "authorUsername", "content", "createdAt"],
            properties: {
                postId: {
                    bsonType: "string",
                    description: "帖子ID必须是字符串"
                },
                authorId: {
                    bsonType: "long",
                    description: "作者ID必须是数字"
                },
                authorUsername: {
                    bsonType: "string",
                    minLength: 1,
                    maxLength: 50,
                    description: "作者用户名必须是1-50个字符的字符串"
                },
                content: {
                    bsonType: "string",
                    minLength: 1,
                    maxLength: 1000,
                    description: "评论内容必须是1-1000个字符的字符串"
                },
                likeCount: {
                    bsonType: "int",
                    minimum: 0,
                    description: "点赞数必须是非负整数"
                }
            }
        }
    }
});

// =============================================
// 创建定期清理任务
// =============================================

// 创建清理过期通知的函数
function cleanupExpiredNotifications() {
    const result = db.notifications.deleteMany({
        "expireAt": { $lt: new Date() }
    });
    print("清理过期通知: " + result.deletedCount + " 条");
    return result;
}

// 创建清理已删除帖子的函数
function cleanupDeletedPosts() {
    const thirtyDaysAgo = new Date();
    thirtyDaysAgo.setDate(thirtyDaysAgo.getDate() - 30);
    
    const result = db.posts.deleteMany({
        "status": "DELETED",
        "updatedAt": { $lt: thirtyDaysAgo }
    });
    print("清理已删除帖子: " + result.deletedCount + " 条");
    return result;
}

// 创建更新统计数据的过程
function updateStatistics() {
    // 更新帖子统计数据
    db.posts.aggregate([
        { $match: {} },
        { $out: "temp_post_stats" }
    ]);
    
    // 更新用户活跃度统计
    db.posts.aggregate([
        {
            $group: {
                "_id": "$authorId",
                "username": { $first: "$authorUsername" },
                "postCount": { $sum: 1 },
                "totalLikes": { $sum: "$likeCount" },
                "totalComments": { $sum: "$commentCount" }
            }
        },
        { $out: "user_stats" }
    ]);
    
    print("统计数据更新完成");
}

// =============================================
// 创建备份脚本
// =============================================

function createBackup() {
    const timestamp = new Date().toISOString().replace(/[:.]/g, '-');
    const backupName = `college_students_backup_${timestamp}`;
    
    // 这里可以添加实际的备份逻辑
    print(`创建备份: ${backupName}`);
    return backupName;
}

// =============================================
// 性能优化设置
// =============================================

// 设置读写关注级别
db.runCommand({
    setDefaultRWConcern: {
        defaultReadConcern: { level: "majority" },
        defaultWriteConcern: { w: "majority", j: true }
    }
});

// 创建数据库用户
db.createUser({
    user: "college_app",
    pwd: "CollegeApp2024!",
    roles: [
        { role: "readWrite", db: "college_students_community" }
    ]
});

db.createUser({
    user: "college_readonly",
    pwd: "CollegeReadOnly2024!",
    roles: [
        { role: "read", db: "college_students_community" }
    ]
});

// =============================================
// 输出初始化完成信息
// =============================================

print("=============================================");
print("MongoDB 初始化完成!");
print("数据库: college_students_community");
print("集合数量: " + db.getCollectionNames().length);
print("索引数量: " + db.posts.getIndexes().length);
print("=============================================");

// 显示所有集合
print("已创建的集合:");
db.getCollectionNames().forEach(function(name) {
    print("- " + name);
});

print("初始化脚本执行完成！");
