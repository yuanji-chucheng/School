-- 增量迁移：互助帖订单备注
ALTER TABLE help_request ADD COLUMN order_note VARCHAR(500) DEFAULT NULL COMMENT '订单备注(仅接单者可见)' AFTER reward;
