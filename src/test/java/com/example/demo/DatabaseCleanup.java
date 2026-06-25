package com.example.demo;

import java.sql.*;

public class DatabaseCleanup {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:mysql://rm-bp1gpy26lasmevofezo.mysql.rds.aliyuncs.com:3306/campus_trade?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true";
        String user = "campus_user";
        String password = "Ycc123456";

        Class.forName("com.mysql.cj.jdbc.Driver");

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            conn.setAutoCommit(false);
            Statement stmt = conn.createStatement();

            System.out.println("========== 数据库清理开始 ==========");

            // 1. 清理前统计
            System.out.println("\n--- 清理前数据统计 ---");
            printStats(stmt);

            // 2. 查看要保留的用户
            System.out.println("\n--- 要保留的用户信息 ---");
            ResultSet rs = stmt.executeQuery(
                "SELECT id, student_id, nickname, role, status FROM sys_user WHERE student_id = '2351300302'"
            );
            if (!rs.next()) {
                System.out.println("警告：未找到学号 2351300302 的用户！");
                conn.rollback();
                return;
            }
            long keepId = rs.getLong("id");
            System.out.println("ID: " + keepId + ", 学号: " + rs.getString("student_id") +
                             ", 昵称: " + rs.getString("nickname"));
            rs.close();

            // 3. 删除其他用户的互助帖
            int deletedHelp = stmt.executeUpdate(
                "DELETE FROM help_request WHERE user_id != " + keepId
            );
            System.out.println("\n删除互助帖数量: " + deletedHelp);

            // 4. 删除其他用户的物品
            int deletedItem = stmt.executeUpdate(
                "DELETE FROM item WHERE seller_id != " + keepId
            );
            System.out.println("删除物品数量: " + deletedItem);

            // 5. 删除其他用户
            int deletedUser = stmt.executeUpdate(
                "DELETE FROM sys_user WHERE id != " + keepId
            );
            System.out.println("删除用户数量: " + deletedUser);

            // 6. 提交事务
            conn.commit();

            // 7. 清理后统计
            System.out.println("\n--- 清理后数据统计 ---");
            printStats(stmt);

            System.out.println("\n========== 数据库清理完成 ==========");
        }
    }

    private static void printStats(Statement stmt) throws SQLException {
        ResultSet rs = stmt.executeQuery(
            "SELECT (SELECT COUNT(*) FROM sys_user) as u, " +
            "(SELECT COUNT(*) FROM item) as i, " +
            "(SELECT COUNT(*) FROM help_request) as h"
        );
        if (rs.next()) {
            System.out.println("用户: " + rs.getLong("u") +
                             " | 物品: " + rs.getLong("i") +
                             " | 互助帖: " + rs.getLong("h"));
        }
        rs.close();
    }
}
