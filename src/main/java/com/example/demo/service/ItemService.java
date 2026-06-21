package com.example.demo.service;

import com.example.demo.common.BusinessException;
import com.example.demo.common.PageResult;
import com.example.demo.dto.AuditRequest;
import com.example.demo.dto.ItemRequest;
import com.example.demo.entity.Item;
import com.example.demo.mapper.ItemMapper;
import com.example.demo.util.UserContext;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/** 二手物品服务 */
@Service
public class ItemService {

    private final ItemMapper itemMapper;
    private final NotificationService notificationService;

    public ItemService(ItemMapper itemMapper, NotificationService notificationService) {
        this.itemMapper = itemMapper;
        this.notificationService = notificationService;
    }

    public Item publish(ItemRequest req) {
        if (req.getPrice() == null || req.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("价格不能小于0");
        }
        Integer level = req.getConditionLevel() != null ? req.getConditionLevel() : 3;
        if (level < 1 || level > 5) {
            throw new BusinessException("新旧程度必须在1-5之间");
        }
        Item item = new Item();
        item.setSellerId(UserContext.getUserId());
        item.setTitle(req.getTitle());
        item.setDescription(req.getDescription());
        item.setPrice(req.getPrice());
        item.setCategory(req.getCategory());
        item.setConditionLevel(level);
        item.setImages(req.getImages());
        item.setStatus(0); // 待审核
        itemMapper.insert(item);
        return itemMapper.findById(item.getId());
    }

    public Item getById(Long id) {
        return itemMapper.findById(id);
    }

    public PageResult<Item> search(String category, String keyword,
                                   BigDecimal minPrice, BigDecimal maxPrice,
                                   Integer status, Long sellerId, String priceSort,
                                   int page, int size) {
        int offset = (page - 1) * size;
        // 公开搜索默认只显示已通过
        if (status == null && sellerId == null) {
            status = 1;
        }
        List<Item> rows = itemMapper.search(category, keyword, minPrice, maxPrice, status, sellerId, priceSort, offset, size);
        long total = itemMapper.countSearch(category, keyword, minPrice, maxPrice, status, sellerId);
        return new PageResult<>(rows, total);
    }

    public Item update(Long id, ItemRequest req) {
        Item item = itemMapper.findById(id);
        if (item == null) throw new BusinessException("物品不存在");
        if (!item.getSellerId().equals(UserContext.getUserId())) {
            throw new BusinessException("无权修改");
        }
        item.setTitle(req.getTitle());
        item.setDescription(req.getDescription());
        item.setPrice(req.getPrice());
        item.setCategory(req.getCategory());
        item.setConditionLevel(req.getConditionLevel());
        item.setImages(req.getImages());
        item.setStatus(0); // 修改后重新审核
        itemMapper.update(item);
        return itemMapper.findById(id);
    }

    public void offShelf(Long id) {
        Item item = itemMapper.findById(id);
        if (item == null) throw new BusinessException("物品不存在");
        if (!item.getSellerId().equals(UserContext.getUserId()) && !UserContext.isAdmin()) {
            throw new BusinessException("无权操作");
        }
        item.setStatus(4);
        itemMapper.update(item);
    }

    /** 管理员审核物品 */
    public void audit(Long id, AuditRequest req) {
        if (!UserContext.isAdmin()) throw new BusinessException(403, "需要管理员权限");
        Item item = itemMapper.findById(id);
        if (item == null) throw new BusinessException("物品不存在");
        item.setStatus(Boolean.TRUE.equals(req.getApproved()) ? 1 : 2);
        itemMapper.update(item);
        notificationService.send(item.getSellerId(),
                Boolean.TRUE.equals(req.getApproved()) ? "物品审核通过" : "物品审核驳回",
                "您的物品《" + item.getTitle() + "》" + (Boolean.TRUE.equals(req.getApproved()) ? "已上架" : "被驳回：" + req.getReason()),
                "ITEM_AUDIT");
    }

    public PageResult<Item> pendingItems(int page, int size) {
        if (!UserContext.isAdmin()) throw new BusinessException(403, "需要管理员权限");
        int offset = (page - 1) * size;
        return new PageResult<>(itemMapper.findPending(offset, size), itemMapper.countPending());
    }
}
