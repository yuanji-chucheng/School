package com.example.demo.entity;

import lombok.Data;
import java.time.LocalDateTime;

/** 交易评价实体 */
@Data
public class TradeReview {
    private Long id;
    private Long orderId;
    private Long reviewerId;
    private Long revieweeId;
    private Integer rating;
    private String content;
    private LocalDateTime createTime;
    private String reviewerNickname;
}
