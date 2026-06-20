package com.example.demo.mapper;

import com.example.demo.entity.Report;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface ReportMapper {
    int insert(Report report);
    Report findById(Long id);
    int update(Report report);
    List<Report> findPending(@Param("offset") int offset, @Param("limit") int limit);
    long countPending();
}
