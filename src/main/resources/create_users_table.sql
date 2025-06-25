-- 创建用户表用于JDBCTemplate演示
-- 适用于MySQL数据库
DROP TABLE IF EXISTS users;

CREATE TABLE
    users (
        id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
        name VARCHAR(100) NOT NULL COMMENT '用户姓名',
        email VARCHAR(255) NOT NULL UNIQUE COMMENT '邮箱地址',
        age INT DEFAULT 0 COMMENT '年龄',
        create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
        update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
        INDEX idx_name (name),
        INDEX idx_email (email),
        INDEX idx_age (age),
        INDEX idx_create_time (create_time)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表';

-- 插入一些测试数据
INSERT INTO
    users (name, email, age)
VALUES
    ('张三', 'zhangsan@example.com', 25),
    ('李四', 'lisi@example.com', 30),
    ('王五', 'wangwu@example.com', 28),
    ('赵六', 'zhaoliu@example.com', 22),
    ('钱七', 'qianqi@example.com', 35);

-- 查询验证
SELECT
    *
FROM
    users;