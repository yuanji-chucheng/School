package com.example.demo.mapper;

import com.example.demo.entity.HelpRequest;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface HelpRequestMapper {
    HelpRequest findById(Long id);
    int insert(HelpRequest request);
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    int update(HelpRequest request);
    List<HelpRequest> findPending(@Param("offset") int offset, @Param("limit") int limit);
    long countPending();
    List<HelpRequest> search(@Param("status") Integer status,
                             @Param("userId") Long userId,
                             @Param("offset") int offset,
                             @Param("limit") int limit);
    long countSearch(@Param("status") Integer status, @Param("userId") Long userId);
}
