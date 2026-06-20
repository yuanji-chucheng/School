package com.example.demo.util;

/**
 * 当前登录用户上下文（ThreadLocal）
 */
public class UserContext {
    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<Integer> ROLE = new ThreadLocal<>();

    public static void set(Long userId, Integer role) {
        USER_ID.set(userId);
        ROLE.set(role);
    }

    public static Long getUserId() {
        return USER_ID.get();
    }

    public static Integer getRole() {
        return ROLE.get();
    }

    public static boolean isAdmin() {
        return ROLE.get() != null && ROLE.get() == 1;
    }

    public static void clear() {
        USER_ID.remove();
        ROLE.remove();
    }
}
