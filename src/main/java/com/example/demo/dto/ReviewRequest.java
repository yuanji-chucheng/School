package com.example.demo.dto;

import lombok.Data;

@Data
public class ReviewRequest {
    private Long orderId;
    private Long revieweeId;
    private Integer rating;
    private String content;
}
