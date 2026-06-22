-- 添加订单备注字段到互助需求表
ALTER TABLE help_request ADD COLUMN IF NOT EXISTS order_note VARCHAR(500) DEFAULT NULL COMMENT '订单备注(接单后仅接单者可见)';