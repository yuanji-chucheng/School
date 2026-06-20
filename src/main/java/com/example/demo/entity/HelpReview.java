package com.example.demo.entity;

import lombok.Data;
import java.time.LocalDateTime;

/** 互助评价实体 */
@Data
public class HelpReview {
    private Long id;
    private Long helpOrderId;
    private Long reviewerId;
    private Long revieweeId;
    private Integer rating;
    private String content;
    private LocalDateTime createTime;
}
