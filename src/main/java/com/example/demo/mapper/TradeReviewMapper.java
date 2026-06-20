package com.example.demo.mapper;

import com.example.demo.entity.TradeReview;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface TradeReviewMapper {
    int insert(TradeReview review);
    TradeReview findByOrderAndReviewer(@Param("orderId") Long orderId, @Param("reviewerId") Long reviewerId);
    List<TradeReview> findByOrderId(Long orderId);
}
