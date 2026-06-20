package com.example.demo.service;

import com.example.demo.common.BusinessException;
import com.example.demo.common.PageResult;
import com.example.demo.dto.ReportRequest;
import com.example.demo.entity.Report;
import com.example.demo.mapper.ReportMapper;
import com.example.demo.util.UserContext;
import org.springframework.stereotype.Service;

import java.util.List;

/** 举报服务 */
@Service
public class ReportService {

    private final ReportMapper reportMapper;

    public ReportService(ReportMapper reportMapper) {
        this.reportMapper = reportMapper;
    }

    public void submit(ReportRequest req) {
        Report report = new Report();
        report.setReporterId(UserContext.getUserId());
        report.setTargetType(req.getTargetType());
        report.setTargetId(req.getTargetId());
        report.setReason(req.getReason());
        report.setStatus(0);
        reportMapper.insert(report);
    }

    public PageResult<Report> pending(int page, int size) {
        if (!UserContext.isAdmin()) throw new BusinessException(403, "需要管理员权限");
        int offset = (page - 1) * size;
        List<Report> rows = reportMapper.findPending(offset, size);
        return new PageResult<>(rows, reportMapper.countPending());
    }

    public void handle(Long id, String result) {
        if (!UserContext.isAdmin()) throw new BusinessException(403, "需要管理员权限");
        Report report = reportMapper.findById(id);
        if (report == null) throw new BusinessException("举报不存在");
        report.setStatus(1);
        report.setHandleResult(result);
        reportMapper.update(report);
    }
}
