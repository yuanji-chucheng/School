package com.example.demo.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String studentId;
    private String password;
    /** student 或 admin，用于区分登录入口 */
    private String loginType;
}
