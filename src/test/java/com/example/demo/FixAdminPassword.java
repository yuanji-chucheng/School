package com.example.demo;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.*;

public class FixAdminPassword {

    public static String sha256(String raw) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("加密失败", e);
        }
    }

    public static void main(String[] args) throws Exception {
        String url = "jdbc:mysql://rm-bp1gpy26lasmevofezo.mysql.rds.aliyuncs.com:3306/campus_trade?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true";
        String user = "campus_user";
        String password = "Ycc123456";

        Class.forName("com.mysql.cj.jdbc.Driver");

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            Statement stmt = conn.createStatement();

            // 计算正确的SHA-256密码
            String correctPassword = sha256("admin123");
            System.out.println("正确的SHA-256密码: " + correctPassword);

            // 更新管理员密码
            int updated = stmt.executeUpdate(
                "UPDATE sys_user SET password = '" + correctPassword + "' WHERE student_id = 'admin'"
            );

            if (updated > 0) {
                System.out.println("✅ 管理员密码已修复！");
                System.out.println("账号: admin");
                System.out.println("密码: admin123");
            } else {
                System.out.println("❌ 未找到管理员账号");
            }
        }
    }
}
