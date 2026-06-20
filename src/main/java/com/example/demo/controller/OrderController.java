package com.example.demo.controller;

import com.example.demo.common.PageResult;
import com.example.demo.common.Result;
import com.example.demo.dto.ReviewRequest;
import com.example.demo.entity.TradeOrder;
import com.example.demo.entity.TradeReview;
import com.example.demo.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** 订单接口 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /** 下单 */
    @PostMapping
    public Result<TradeOrder> create(@RequestParam Long itemId) {
        return Result.ok(orderService.createOrder(itemId));
    }

    @GetMapping
    public PageResult<TradeOrder> myOrders(@RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "10") int size) {
        return orderService.myOrders(page, size);
    }

    @GetMapping("/{id:\\d+}")
    public Result<TradeOrder> detail(@PathVariable Long id) {
        return Result.ok(orderService.getById(id));
    }

    /** 状态流转：1付款 2发货 3收货 4取消 */
    @PostMapping("/{id:\\d+}/status")
    public Result<TradeOrder> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        return Result.ok(orderService.updateStatus(id, status));
    }

    /** 交易评价 */
    @PostMapping("/review")
    public Result<Void> review(@RequestBody ReviewRequest req) {
        orderService.review(req);
        return Result.ok(null);
    }

    @GetMapping("/{id:\\d+}/reviews")
    public Result<List<TradeReview>> reviews(@PathVariable Long id) {
        return Result.ok(orderService.getReviews(id));
    }
}
