-- ==========================================================================
-- 校园二手物品交易与互助服务平台 初始化脚本
-- 适用环境: MySQL 8.0+
-- 字符集  : utf8mb4 / utf8mb4_unicode_ci
-- 说明    : 基于项目 entity 与 mapper 定义生成，可重复执行（先 DROP 后 CREATE）
--           执行后将创建 campus_trade 库、全部业务表及默认管理员账号
-- 用法    : mysql -u root -p < init.sql
-- ==========================================================================

CREATE DATABASE IF NOT EXISTS campus_trade
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
USE campus_trade;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 按依赖顺序删除，保证脚本可重复执行
DROP TABLE IF EXISTS message;
DROP TABLE IF EXISTS report;
DROP TABLE IF EXISTS notification;
DROP TABLE IF EXISTS help_review;
DROP TABLE IF EXISTS help_order;
DROP TABLE IF EXISTS help_request;
DROP TABLE IF EXISTS trade_review;
DROP TABLE IF EXISTS trade_order;
DROP TABLE IF EXISTS item;
DROP TABLE IF EXISTS sys_user;

SET FOREIGN_KEY_CHECKS = 1;

-- --------------------------------------------------------------------------
-- 用户表 sys_user
-- 对应实体: com.example.demo.entity.User
-- --------------------------------------------------------------------------
CREATE TABLE sys_user (
    id           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    student_id   VARCHAR(20)  NOT NULL COMMENT '学号',
    password     VARCHAR(64)  NOT NULL COMMENT '密码(SHA-256加密)',
    nickname     VARCHAR(50)  NOT NULL COMMENT '昵称',
    avatar       VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    role         TINYINT      NOT NULL DEFAULT 0 COMMENT '角色:0学生 1管理员',
    status       TINYINT      NOT NULL DEFAULT 0 COMMENT '状态:0待审核 1已通过 2已驳回',
    credit_score INT          NOT NULL DEFAULT 100 COMMENT '信用分',
    create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_student_id (student_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表';

-- --------------------------------------------------------------------------
-- 二手物品表 item
-- 对应实体: com.example.demo.entity.Item
-- --------------------------------------------------------------------------
CREATE TABLE item (
    id              BIGINT         NOT NULL AUTO_INCREMENT COMMENT '主键',
    seller_id       BIGINT         NOT NULL COMMENT '卖家ID(sys_user.id)',
    title           VARCHAR(100)   NOT NULL COMMENT '标题',
    description     TEXT           COMMENT '描述',
    price           DECIMAL(10, 2) NOT NULL COMMENT '价格',
    category        VARCHAR(30)    NOT NULL COMMENT '分类',
    condition_level TINYINT        NOT NULL DEFAULT 3 COMMENT '新旧程度1-5',
    images          VARCHAR(1000)  DEFAULT NULL COMMENT '图片URL,逗号分隔',
    status          TINYINT        NOT NULL DEFAULT 0 COMMENT '0待审核 1已通过 2已驳回 3已售出 4已下架',
    create_time     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_seller (seller_id),
    KEY idx_category (category),
    KEY idx_status (status)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '二手物品表';

-- --------------------------------------------------------------------------
-- 交易订单表 trade_order
-- 对应实体: com.example.demo.entity.TradeOrder
-- --------------------------------------------------------------------------
CREATE TABLE trade_order (
    id          BIGINT         NOT NULL AUTO_INCREMENT COMMENT '主键',
    item_id     BIGINT         NOT NULL COMMENT '物品ID(item.id)',
    buyer_id    BIGINT         NOT NULL COMMENT '买家ID(sys_user.id)',
    seller_id   BIGINT         NOT NULL COMMENT '卖家ID(sys_user.id)',
    price       DECIMAL(10, 2) NOT NULL COMMENT '成交价格',
    status      TINYINT        NOT NULL DEFAULT 0 COMMENT '0待付款 1待发货 2待收货 3已完成 4已取消',
    create_time DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_item (item_id),
    KEY idx_buyer (buyer_id),
    KEY idx_seller (seller_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '交易订单表';

-- --------------------------------------------------------------------------
-- 交易评价表 trade_review
-- 对应实体: com.example.demo.entity.TradeReview
-- --------------------------------------------------------------------------
CREATE TABLE trade_review (
    id          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    order_id    BIGINT      NOT NULL COMMENT '订单ID(trade_order.id)',
    reviewer_id BIGINT      NOT NULL COMMENT '评价人ID',
    reviewee_id BIGINT      NOT NULL COMMENT '被评价人ID',
    rating      TINYINT     NOT NULL COMMENT '评分1-5',
    content     VARCHAR(500) DEFAULT NULL COMMENT '评价内容',
    create_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_reviewer (order_id, reviewer_id),
    KEY idx_reviewee (reviewee_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '交易评价表';

-- --------------------------------------------------------------------------
-- 互助需求表 help_request
-- 对应实体: com.example.demo.entity.HelpRequest
-- --------------------------------------------------------------------------
CREATE TABLE help_request (
    id          BIGINT         NOT NULL AUTO_INCREMENT COMMENT '主键',
    user_id     BIGINT         NOT NULL COMMENT '发布者ID(sys_user.id)',
    title       VARCHAR(100)   NOT NULL COMMENT '标题',
    description TEXT           COMMENT '描述',
    reward      DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '酬劳',
    order_note  VARCHAR(500)   DEFAULT NULL COMMENT '订单备注(接单后仅接单者可见)',
    status      TINYINT        NOT NULL DEFAULT 0 COMMENT '0待审核 1待接单 2进行中 3已完成 4已取消 5已下架',
    create_time DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_user (user_id),
    KEY idx_status (status)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '互助需求表';

-- --------------------------------------------------------------------------
-- 互助接单表 help_order
-- 对应实体: com.example.demo.entity.HelpOrder
-- --------------------------------------------------------------------------
CREATE TABLE help_order (
    id          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键',
    request_id  BIGINT   NOT NULL COMMENT '需求ID(help_request.id)',
    helper_id   BIGINT   NOT NULL COMMENT '接单者ID(sys_user.id)',
    status      TINYINT  NOT NULL DEFAULT 1 COMMENT '1进行中 2已完成',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_request (request_id),
    KEY idx_helper (helper_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '互助接单表';

-- --------------------------------------------------------------------------
-- 互助评价表 help_review
-- 对应实体: com.example.demo.entity.HelpReview
-- --------------------------------------------------------------------------
CREATE TABLE help_review (
    id            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    help_order_id BIGINT       NOT NULL COMMENT '互助接单ID(help_order.id)',
    reviewer_id   BIGINT       NOT NULL COMMENT '评价人ID',
    reviewee_id   BIGINT       NOT NULL COMMENT '被评价人ID',
    rating        TINYINT      NOT NULL COMMENT '评分1-5',
    content       VARCHAR(500) DEFAULT NULL COMMENT '评价内容',
    create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_help_reviewer (help_order_id, reviewer_id),
    KEY idx_reviewee (reviewee_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '互助评价表';

-- --------------------------------------------------------------------------
-- 系统通知表 notification
-- 对应实体: com.example.demo.entity.Notification
-- --------------------------------------------------------------------------
CREATE TABLE notification (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    user_id     BIGINT       NOT NULL COMMENT '接收用户ID(sys_user.id)',
    title       VARCHAR(100) NOT NULL COMMENT '通知标题',
    content     VARCHAR(500) NOT NULL COMMENT '通知内容',
    type        VARCHAR(30)  NOT NULL COMMENT '通知类型',
    is_read     TINYINT      NOT NULL DEFAULT 0 COMMENT '0未读 1已读',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_user (user_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统通知表';

-- --------------------------------------------------------------------------
-- 举报表 report
-- 对应实体: com.example.demo.entity.Report
-- --------------------------------------------------------------------------
CREATE TABLE report (
    id            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    reporter_id   BIGINT       NOT NULL COMMENT '举报人ID(sys_user.id)',
    target_type   VARCHAR(20)  NOT NULL COMMENT '目标类型:ITEM/USER/HELP',
    target_id     BIGINT       NOT NULL COMMENT '目标ID',
    reason        VARCHAR(500) NOT NULL COMMENT '举报原因',
    status        TINYINT      NOT NULL DEFAULT 0 COMMENT '0待处理 1已处理',
    handle_result VARCHAR(500) DEFAULT NULL COMMENT '处理结果',
    create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_reporter (reporter_id),
    KEY idx_status (status)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '举报表';

-- --------------------------------------------------------------------------
-- 私信表 message
-- 对应实体: com.example.demo.entity.Message
-- --------------------------------------------------------------------------
CREATE TABLE message (
    id          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    sender_id   BIGINT        NOT NULL COMMENT '发送者ID(sys_user.id)',
    receiver_id BIGINT        NOT NULL COMMENT '接收者ID(sys_user.id)',
    content     VARCHAR(1000) NOT NULL COMMENT '消息内容',
    is_read     TINYINT       NOT NULL DEFAULT 0 COMMENT '0未读 1已读',
    create_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_sender (sender_id),
    KEY idx_receiver (receiver_id),
    KEY idx_pair (sender_id, receiver_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '私信表';

-- --------------------------------------------------------------------------
-- 初始化数据：默认管理员账号
-- 学号: admin   密码: admin123 (SHA-256)
-- --------------------------------------------------------------------------
INSERT INTO sys_user (student_id, password, nickname, role, status, credit_score)
VALUES ('admin',
        '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',
        '系统管理员', 1, 1, 100);
