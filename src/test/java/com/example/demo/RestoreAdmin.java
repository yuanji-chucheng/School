package com.example.demo;

import java.sql.*;

public class RestoreAdmin {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:mysql://rm-bp1gpy26lasmevofezo.mysql.rds.aliyuncs.com:3306/campus_trade?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true";
        String user = "campus_user";
        String password = "Ycc123456";

        Class.forName("com.mysql.cj.jdbc.Driver");

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            Statement stmt = conn.createStatement();

            System.out.println("========== 检查并恢复管理员账号 ==========");

            // 检查当前用户
            ResultSet rs = stmt.executeQuery("SELECT id, student_id, nickname, role FROM sys_user");
            System.out.println("\n--- 当前所有用户 ---");
            boolean hasAdmin = false;
            while (rs.next()) {
                long id = rs.getLong("id");
                String sid = rs.getString("student_id");
                String nick = rs.getString("nickname");
                int role = rs.getInt("role");
                System.out.println("ID: " + id + ", 学号: " + sid + ", 昵称: " + nick + ", 角色: " + (role == 1 ? "管理员" : "学生"));
                if (role == 1) hasAdmin = true;
            }
            rs.close();

            if (hasAdmin) {
                System.out.println("\n管理员账号已存在，无需恢复。");
                return;
            }

            // 创建管理员账号
            // 默认密码: admin123 (MD5: 0192023a7bbd73250516f069df18b500)
            int inserted = stmt.executeUpdate(
                "INSERT INTO sys_user (student_id, password, nickname, avatar, role, status, create_time) VALUES " +
                "('admin', '0192023a7bbd73250516f069df18b500', '管理员', 'https://ui-avatars.com/api/?name=admin&background=3498db&color=fff', 1, 1, NOW())"
            );

            if (inserted > 0) {
                System.out.println("\n✅ 管理员账号已恢复！");
                System.out.println("学号: admin");
                System.out.println("密码: admin123");
                System.out.println("角色: 管理员");
            } else {
                System.out.println("\n❌ 恢复失败");
            }

            // 再次检查
            rs = stmt.executeQuery("SELECT id, student_id, nickname, role FROM sys_user");
            System.out.println("\n--- 恢复后所有用户 ---");
            while (rs.next()) {
                long id = rs.getLong("id");
                String sid = rs.getString("student_id");
                String nick = rs.getString("nickname");
                int role = rs.getInt("role");
                System.out.println("ID: " + id + ", 学号: " + sid + ", 昵称: " + nick + ", 角色: " + (role == 1 ? "管理员" : "学生"));
            }
            rs.close();

            System.out.println("\n========== 完成 ==========");
        }
    }
}
