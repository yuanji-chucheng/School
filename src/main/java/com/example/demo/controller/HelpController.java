package com.example.demo.controller;

import com.example.demo.common.PageResult;
import com.example.demo.common.Result;
import com.example.demo.dto.AuditRequest;
import com.example.demo.dto.HelpRequestDto;
import com.example.demo.dto.ReviewRequest;
import com.example.demo.entity.HelpOrder;
import com.example.demo.entity.HelpRequest;
import com.example.demo.service.HelpService;
import org.springframework.web.bind.annotation.*;

/** 互助需求接口 */
@RestController
@RequestMapping("/api/help")
public class HelpController {

    private final HelpService helpService;

    public HelpController(HelpService helpService) {
        this.helpService = helpService;
    }

    @PostMapping
    public Result<HelpRequest> publish(@RequestBody HelpRequestDto dto) {
        return Result.ok(helpService.publish(dto));
    }

    /** 列表（公开浏览待接单） */
    @GetMapping
    public PageResult<HelpRequest> list(@RequestParam(required = false) Integer status,
                                        @RequestParam(required = false) Long userId,
                                        @RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        return helpService.list(status, userId, page, size);
    }

    /** 我的互助接单 */
    @GetMapping("/my-orders")
    public PageResult<HelpOrder> myOrders(@RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        return helpService.myHelpOrders(page, size);
    }

    /** 管理员：待审核互助帖 */
    @GetMapping("/pending")
    public PageResult<HelpRequest> pending(@RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "10") int size) {
        return helpService.pendingRequests(page, size);
    }

    @PutMapping("/{id:\\d+}")
    public Result<HelpRequest> update(@PathVariable Long id, @RequestBody HelpRequestDto dto) {
        return Result.ok(helpService.update(id, dto));
    }

    @GetMapping("/{id:\\d+}")
    public Result<HelpRequest> detail(@PathVariable Long id) {
        return Result.ok(helpService.getById(id));
    }

    @PostMapping("/{id:\\d+}/accept")
    public Result<HelpOrder> accept(@PathVariable Long id) {
        return Result.ok(helpService.accept(id));
    }

    @PostMapping("/{id:\\d+}/complete")
    public Result<Void> complete(@PathVariable Long id) {
        helpService.complete(id);
        return Result.ok(null);
    }

    @PostMapping("/{id:\\d+}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        helpService.cancel(id);
        return Result.ok(null);
    }

    @PostMapping("/{id:\\d+}/audit")
    public Result<Void> audit(@PathVariable Long id, @RequestBody AuditRequest req) {
        helpService.audit(id, req);
        return Result.ok(null);
    }

    @GetMapping("/{id:\\d+}/order")
    public Result<HelpOrder> getOrder(@PathVariable Long id) {
        return Result.ok(helpService.getHelpOrder(id));
    }

    @PostMapping("/order/{orderId}/review")
    public Result<Void> review(@PathVariable Long orderId, @RequestBody ReviewRequest req) {
        helpService.review(orderId, req);
        return Result.ok(null);
    }
}
