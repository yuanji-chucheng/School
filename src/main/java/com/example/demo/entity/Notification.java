package com.example.demo.entity;

import lombok.Data;
import java.time.LocalDateTime;

/** 系统通知实体 */
@Data
public class Notification {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private String type;
    /** 0未读 1已读 */
    private Integer isRead;
    private LocalDateTime createTime;
}
