package com.example.demo.dto;

import lombok.Data;

@Data
public class ReportRequest {
    private String targetType;
    private Long targetId;
    private String reason;
}
