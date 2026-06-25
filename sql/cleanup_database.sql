-- ========================================
-- 数据库清理脚本
-- 保留学号2351300302（yiyi）的用户及其帖子
-- 删除其他所有用户及其相关数据
-- ========================================

-- 0. 设置字符集
SET NAMES utf8mb4;

-- 1. 先查看当前数据情况
SELECT '=== 清理前数据统计 ===' as info;
SELECT '用户表' as table_name, COUNT(*) as count FROM sys_user
UNION ALL
SELECT '物品表' as table_name, COUNT(*) as count FROM item
UNION ALL
SELECT '互助帖表' as table_name, COUNT(*) as count FROM help_request;

-- 2. 查看要保留的用户信息
SELECT id, student_id, nickname, role, status FROM sys_user WHERE student_id = '2351300302';

-- 3. 删除除了yiyi以外用户发布的所有互助帖（先删除，因为涉及外键）
DELETE FROM help_request WHERE user_id NOT IN (
    SELECT id FROM sys_user WHERE student_id = '2351300302'
);
SELECT ROW_COUNT() as '删除互助帖数量';

-- 4. 删除除了yiyi以外用户发布的所有物品
DELETE FROM item WHERE seller_id NOT IN (
    SELECT id FROM sys_user WHERE student_id = '2351300302'
);
SELECT ROW_COUNT() as '删除物品数量';

-- 5. 删除除了yiyi以外的所有用户
DELETE FROM sys_user WHERE student_id != '2351300302';
SELECT ROW_COUNT() as '删除用户数量';

-- 6. 验证清理结果
SELECT '=== 清理后数据统计 ===' as info;
SELECT '用户表' as table_name, COUNT(*) as count FROM sys_user
UNION ALL
SELECT '物品表' as table_name, COUNT(*) as count FROM item
UNION ALL
SELECT '互助帖表' as table_name, COUNT(*) as count FROM help_request;

-- 7. 查看剩余用户
SELECT id, student_id, nickname, role FROM sys_user;
