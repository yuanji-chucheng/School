package com.example.demo.entity;

import lombok.Data;
import java.time.LocalDateTime;

/** 互助接单实体 */
@Data
public class HelpOrder {
    private Long id;
    private Long requestId;
    private Long helperId;
    /** 1进行中 2已完成 */
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String helperNickname;
    private String requestTitle;
    private String requestDescription;
    private java.math.BigDecimal requestReward;
    private Integer requestStatus;
    private String publisherNickname;
    private Long publisherId;
}
