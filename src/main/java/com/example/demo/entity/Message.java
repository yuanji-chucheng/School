package com.example.demo.entity;

import lombok.Data;
import java.time.LocalDateTime;

/** 私信实体 */
@Data
public class Message {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private String content;
    /** 0未读 1已读 */
    private Integer isRead;
    private LocalDateTime createTime;
    private String senderNickname;
    private String receiverNickname;
}
