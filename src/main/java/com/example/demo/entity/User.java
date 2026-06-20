package com.example.demo.entity;

import lombok.Data;
import java.time.LocalDateTime;

/** 用户实体 */
@Data
public class User {
    private Long id;
    private String studentId;
    private String password;
    private String nickname;
    private String avatar;
    /** 0学生 1管理员 */
    private Integer role;
    /** 0待审核 1已通过 2已驳回 */
    private Integer status;
    private Integer creditScore;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
