package com.example.demo.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * 密码工具（SHA-256 简单加密）
 */
public class PasswordUtil {

    public static String encrypt(String raw) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("密码加密失败", e);
        }
    }

    public static boolean matches(String raw, String encoded) {
        return encrypt(raw).equals(encoded);
    }
}
