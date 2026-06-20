package com.example.demo.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private Long userId;
    private String studentId;
    private String nickname;
    private Integer role;
    private Integer status;
}
