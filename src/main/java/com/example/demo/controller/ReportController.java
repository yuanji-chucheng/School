package com.example.demo.controller;

import com.example.demo.common.PageResult;
import com.example.demo.common.Result;
import com.example.demo.dto.ReportRequest;
import com.example.demo.entity.Report;
import com.example.demo.service.ReportService;
import org.springframework.web.bind.annotation.*;

/** 举报接口 */
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping
    public Result<Void> submit(@RequestBody ReportRequest req) {
        reportService.submit(req);
        return Result.ok("举报已提交", null);
    }

    /** 管理员：待处理举报 */
    @GetMapping("/pending")
    public PageResult<Report> pending(@RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        return reportService.pending(page, size);
    }

    /** 管理员：处理举报 */
    @PostMapping("/{id:\\d+}/handle")
    public Result<Void> handle(@PathVariable Long id, @RequestParam String result) {
        reportService.handle(id, result);
        return Result.ok(null);
    }
}
