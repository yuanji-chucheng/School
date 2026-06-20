package com.example.demo.mapper;

import com.example.demo.entity.TradeOrder;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface TradeOrderMapper {
    TradeOrder findById(Long id);
    int insert(TradeOrder order);
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    List<TradeOrder> findByUser(@Param("userId") Long userId,
                                @Param("offset") int offset,
                                @Param("limit") int limit);
    long countByUser(Long userId);
}
