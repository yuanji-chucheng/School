package com.example.demo.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 交易订单实体 */
@Data
public class TradeOrder {
    private Long id;
    private Long itemId;
    private Long buyerId;
    private Long sellerId;
    private BigDecimal price;
    /** 0待付款 1待发货 2待收货 3已完成 4已取消 */
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    // 关联
    private String itemTitle;
    private String buyerNickname;
    private String sellerNickname;
}
