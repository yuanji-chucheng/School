package com.example.demo.controller;

import com.example.demo.common.PageResult;
import com.example.demo.common.Result;
import com.example.demo.dto.AuditRequest;
import com.example.demo.dto.ItemRequest;
import com.example.demo.entity.Item;
import com.example.demo.service.ItemService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/** 二手物品接口 */
@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    /** 发布物品 */
    @PostMapping
    public Result<Item> publish(@RequestBody ItemRequest req) {
        return Result.ok(itemService.publish(req));
    }

    /** 搜索/列表（公开） */
    @GetMapping
    public PageResult<Item> search(@RequestParam(required = false) String category,
                                   @RequestParam(required = false) String keyword,
                                   @RequestParam(required = false) BigDecimal minPrice,
                                   @RequestParam(required = false) BigDecimal maxPrice,
                                   @RequestParam(required = false) Integer status,
                                   @RequestParam(required = false) Long sellerId,
                                   @RequestParam(required = false) String priceSort,
                                   @RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "12") int size) {
        return itemService.search(category, keyword, minPrice, maxPrice, status, sellerId, priceSort, page, size);
    }

    /** 物品详情（公开） */
    @GetMapping("/{id:\\d+}")
    public Result<Item> detail(@PathVariable Long id) {
        return Result.ok(itemService.getById(id));
    }

    @PutMapping("/{id:\\d+}")
    public Result<Item> update(@PathVariable Long id, @RequestBody ItemRequest req) {
        return Result.ok(itemService.update(id, req));
    }

    @PostMapping("/{id:\\d+}/off-shelf")
    public Result<Void> offShelf(@PathVariable Long id) {
        itemService.offShelf(id);
        return Result.ok(null);
    }

    /** 管理员：待审核物品 */
    @GetMapping("/pending")
    public PageResult<Item> pending(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int size) {
        return itemService.pendingItems(page, size);
    }

    /** 管理员：审核物品 */
    @PostMapping("/{id:\\d+}/audit")
    public Result<Void> audit(@PathVariable Long id, @RequestBody AuditRequest req) {
        itemService.audit(id, req);
        return Result.ok(null);
    }
}
