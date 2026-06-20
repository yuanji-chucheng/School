package com.example.demo.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 互助需求实体 */
@Data
public class HelpRequest {
    private Long id;
    private Long userId;
    private String title;
    private String description;
    private BigDecimal reward;
    /** 0待接单 1进行中 2已完成 3已取消 */
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String publisherNickname;
}
