package com.example.demo.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String studentId;
    private String password;
}
