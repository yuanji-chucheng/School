package com.example.demo.mapper;

import com.example.demo.entity.HelpOrder;
import org.apache.ibatis.annotations.Param;

public interface HelpOrderMapper {
    HelpOrder findById(Long id);
    HelpOrder findByRequestId(Long requestId);
    int insert(HelpOrder order);
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    java.util.List<HelpOrder> findByHelperId(@Param("helperId") Long helperId,
                                             @Param("offset") int offset,
                                             @Param("limit") int limit);
    long countByHelperId(@Param("helperId") Long helperId);
    java.util.List<HelpOrder> findByUserId(@Param("userId") Long userId,
                                           @Param("offset") int offset,
                                           @Param("limit") int limit);
    long countByUserId(@Param("userId") Long userId);
}
