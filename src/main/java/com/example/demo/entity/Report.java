package com.example.demo.entity;

import lombok.Data;
import java.time.LocalDateTime;

/** 举报实体 */
@Data
public class Report {
    private Long id;
    private Long reporterId;
    private String targetType;
    private Long targetId;
    private String reason;
    /** 0待处理 1已处理 */
    private Integer status;
    private String handleResult;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String reporterNickname;
}
