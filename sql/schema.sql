-- 校园二手交易与互助平台数据库脚本 (MySQL 8.0)
CREATE DATABASE IF NOT EXISTS campus_trade DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE campus_trade;

-- 用户表
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    student_id    VARCHAR(20)  NOT NULL UNIQUE COMMENT '学号',
    password      VARCHAR(64)  NOT NULL COMMENT '密码(加密)',
    nickname      VARCHAR(50)  NOT NULL COMMENT '昵称',
    avatar        VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    role          TINYINT      NOT NULL DEFAULT 0 COMMENT '角色:0学生 1管理员',
    status        TINYINT      NOT NULL DEFAULT 0 COMMENT '状态:0待审核 1已通过 2已驳回',
    credit_score  INT          NOT NULL DEFAULT 100 COMMENT '信用分',
    create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '用户表';

-- 二手物品表
DROP TABLE IF EXISTS item;
CREATE TABLE item (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    seller_id       BIGINT         NOT NULL COMMENT '卖家ID',
    title           VARCHAR(100)   NOT NULL COMMENT '标题',
    description     TEXT           COMMENT '描述',
    price           DECIMAL(10,2)  NOT NULL COMMENT '价格',
    category        VARCHAR(30)    NOT NULL COMMENT '分类',
    condition_level TINYINT        NOT NULL DEFAULT 3 COMMENT '新旧程度1-5',
    images          VARCHAR(1000)  DEFAULT NULL COMMENT '图片URL,逗号分隔',
    status          TINYINT        NOT NULL DEFAULT 0 COMMENT '0待审核 1已通过 2已驳回 3已售出 4已下架',
    create_time     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_seller (seller_id),
    INDEX idx_category (category),
    INDEX idx_status (status)
) COMMENT '二手物品表';

-- 交易订单表
DROP TABLE IF EXISTS trade_order;
CREATE TABLE trade_order (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    item_id     BIGINT         NOT NULL COMMENT '物品ID',
    buyer_id    BIGINT         NOT NULL COMMENT '买家ID',
    seller_id   BIGINT         NOT NULL COMMENT '卖家ID',
    price       DECIMAL(10,2)  NOT NULL COMMENT '成交价格',
    status      TINYINT        NOT NULL DEFAULT 0 COMMENT '0待付款 1待发货 2待收货 3已完成 4已取消',
    create_time DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_buyer (buyer_id),
    INDEX idx_seller (seller_id)
) COMMENT '交易订单表';

-- 交易评价表
DROP TABLE IF EXISTS trade_review;
CREATE TABLE trade_review (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id    BIGINT   NOT NULL COMMENT '订单ID',
    reviewer_id BIGINT   NOT NULL COMMENT '评价人ID',
    reviewee_id BIGINT   NOT NULL COMMENT '被评价人ID',
    rating      TINYINT  NOT NULL COMMENT '评分1-5',
    content     VARCHAR(500) DEFAULT NULL COMMENT '评价内容',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_order_reviewer (order_id, reviewer_id)
) COMMENT '交易评价表';

-- 互助需求表
DROP TABLE IF EXISTS help_request;
CREATE TABLE help_request (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id     BIGINT         NOT NULL COMMENT '发布者ID',
    title       VARCHAR(100)   NOT NULL COMMENT '标题',
    description TEXT           COMMENT '描述',
    reward      DECIMAL(10,2)  DEFAULT 0 COMMENT '酬劳',
    status      TINYINT        NOT NULL DEFAULT 0 COMMENT '0待接单 1进行中 2已完成 3已取消',
    create_time DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user (user_id),
    INDEX idx_status (status)
) COMMENT '互助需求表';

-- 互助接单表
DROP TABLE IF EXISTS help_order;
CREATE TABLE help_order (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    request_id  BIGINT   NOT NULL COMMENT '需求ID',
    helper_id   BIGINT   NOT NULL COMMENT '接单者ID',
    status      TINYINT  NOT NULL DEFAULT 1 COMMENT '1进行中 2已完成',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_request (request_id)
) COMMENT '互助接单表';

-- 互助评价表
DROP TABLE IF EXISTS help_review;
CREATE TABLE help_review (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    help_order_id BIGINT   NOT NULL COMMENT '互助接单ID',
    reviewer_id   BIGINT   NOT NULL COMMENT '评价人ID',
    reviewee_id   BIGINT   NOT NULL COMMENT '被评价人ID',
    rating        TINYINT  NOT NULL COMMENT '评分1-5',
    content       VARCHAR(500) DEFAULT NULL,
    create_time   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_help_reviewer (help_order_id, reviewer_id)
) COMMENT '互助评价表';

-- 系统通知表
DROP TABLE IF EXISTS notification;
CREATE TABLE notification (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id     BIGINT       NOT NULL COMMENT '接收用户ID',
    title       VARCHAR(100) NOT NULL,
    content     VARCHAR(500) NOT NULL,
    type        VARCHAR(30)  NOT NULL COMMENT '通知类型',
    is_read     TINYINT      NOT NULL DEFAULT 0 COMMENT '0未读 1已读',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user (user_id)
) COMMENT '系统通知表';

-- 举报表
DROP TABLE IF EXISTS report;
CREATE TABLE report (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    reporter_id   BIGINT       NOT NULL COMMENT '举报人ID',
    target_type   VARCHAR(20)  NOT NULL COMMENT '目标类型:ITEM/USER/HELP',
    target_id     BIGINT       NOT NULL COMMENT '目标ID',
    reason        VARCHAR(500) NOT NULL COMMENT '举报原因',
    status        TINYINT      NOT NULL DEFAULT 0 COMMENT '0待处理 1已处理',
    handle_result VARCHAR(500) DEFAULT NULL COMMENT '处理结果',
    create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '举报表';

-- 私信表
DROP TABLE IF EXISTS message;
CREATE TABLE message (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    sender_id   BIGINT       NOT NULL COMMENT '发送者ID',
    receiver_id BIGINT       NOT NULL COMMENT '接收者ID',
    content     VARCHAR(1000) NOT NULL,
    is_read     TINYINT      NOT NULL DEFAULT 0,
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_sender (sender_id),
    INDEX idx_receiver (receiver_id)
) COMMENT '私信表';

-- 初始化管理员账号 学号:admin 密码:admin123
INSERT INTO sys_user (student_id, password, nickname, role, status, credit_score)
VALUES ('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', '系统管理员', 1, 1, 100);
