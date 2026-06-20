package com.example.demo.mapper;

import com.example.demo.entity.HelpReview;
import org.apache.ibatis.annotations.Param;

public interface HelpReviewMapper {
    int insert(HelpReview review);
    HelpReview findByHelpOrderAndReviewer(@Param("helpOrderId") Long helpOrderId, @Param("reviewerId") Long reviewerId);
}
