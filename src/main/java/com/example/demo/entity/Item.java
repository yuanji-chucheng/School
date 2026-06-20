package com.example.demo.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 二手物品实体 */
@Data
public class Item {
    private Long id;
    private Long sellerId;
    private String title;
    private String description;
    private BigDecimal price;
    private String category;
    /** 新旧程度 1-5 */
    private Integer conditionLevel;
    /** 图片URL，逗号分隔 */
    private String images;
    /** 0待审核 1已通过 2已驳回 3已售出 4已下架 */
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    // 关联字段
    private String sellerNickname;
}
