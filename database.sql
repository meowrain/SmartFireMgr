-- Create the database
CREATE DATABASE IF NOT EXISTS `firepm` DEFAULT CHARACTER
    SET
    utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `firepm`;

CREATE TABLE IF NOT EXISTS t_admin
(
    `id`       INT AUTO_INCREMENT                                           NOT NULL PRIMARY KEY,
    `name`     varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '名字',
    `password` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密码',
    `phone`    varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号',
    `state`    varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '状态'
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;
