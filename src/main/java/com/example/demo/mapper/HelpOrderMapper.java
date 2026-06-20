package com.example.demo.mapper;

import com.example.demo.entity.HelpOrder;
import org.apache.ibatis.annotations.Param;

public interface HelpOrderMapper {
    HelpOrder findById(Long id);
    HelpOrder findByRequestId(Long requestId);
    int insert(HelpOrder order);
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}
