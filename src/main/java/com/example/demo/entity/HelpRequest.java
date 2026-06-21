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
    private String orderNote;
    /** 0待审核 1待接单 2进行中 3已完成 4已取消 5已下架/已驳回 */
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String publisherNickname;
}
