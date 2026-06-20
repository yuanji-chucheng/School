package com.example.demo.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String studentId;
    private String password;
    private String nickname;
}
