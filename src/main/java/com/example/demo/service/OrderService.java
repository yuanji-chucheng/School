package com.example.demo.service;

import com.example.demo.common.BusinessException;
import com.example.demo.common.PageResult;
import com.example.demo.dto.ReviewRequest;
import com.example.demo.entity.Item;
import com.example.demo.entity.TradeOrder;
import com.example.demo.entity.TradeReview;
import com.example.demo.entity.User;
import com.example.demo.mapper.ItemMapper;
import com.example.demo.mapper.TradeOrderMapper;
import com.example.demo.mapper.TradeReviewMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.util.UserContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** 交易订单服务 */
@Service
public class OrderService {

    private final TradeOrderMapper orderMapper;
    private final ItemMapper itemMapper;
    private final TradeReviewMapper reviewMapper;
    private final UserMapper userMapper;
    private final NotificationService notificationService;

    public OrderService(TradeOrderMapper orderMapper, ItemMapper itemMapper,
                        TradeReviewMapper reviewMapper, UserMapper userMapper,
                        NotificationService notificationService) {
        this.orderMapper = orderMapper;
        this.itemMapper = itemMapper;
        this.reviewMapper = reviewMapper;
        this.userMapper = userMapper;
        this.notificationService = notificationService;
    }

    /** 下单购买 */
    @Transactional
    public TradeOrder createOrder(Long itemId) {
        Item item = itemMapper.findById(itemId);
        if (item == null || item.getStatus() != 1) {
            throw new BusinessException("物品不可购买");
        }
        if (item.getSellerId().equals(UserContext.getUserId())) {
            throw new BusinessException("不能购买自己的物品");
        }
        TradeOrder order = new TradeOrder();
        order.setItemId(itemId);
        order.setBuyerId(UserContext.getUserId());
        order.setSellerId(item.getSellerId());
        order.setPrice(item.getPrice());
        order.setStatus(0); // 待付款
        orderMapper.insert(order);
        notificationService.send(item.getSellerId(), "新订单", "有人下单购买《" + item.getTitle() + "》", "ORDER");
        return orderMapper.findById(order.getId());
    }

    /** 订单状态流转 */
    @Transactional
    public TradeOrder updateStatus(Long orderId, Integer newStatus) {
        TradeOrder order = orderMapper.findById(orderId);
        if (order == null) throw new BusinessException("订单不存在");
        Long uid = UserContext.getUserId();
        validateTransition(order, newStatus, uid);
        orderMapper.updateStatus(orderId, newStatus);
        // 完成时标记物品已售出
        if (newStatus == 3) {
            Item item = itemMapper.findById(order.getItemId());
            item.setStatus(3);
            itemMapper.update(item);
        }
        // 取消时恢复物品
        if (newStatus == 4) {
            Item item = itemMapper.findById(order.getItemId());
            if (item.getStatus() == 3) item.setStatus(1);
            else item.setStatus(1);
            itemMapper.update(item);
        }
        String msg = switch (newStatus) {
            case 1 -> "买家已付款，请发货";
            case 2 -> "卖家已发货，请确认收货";
            case 3 -> "交易已完成";
            case 4 -> "订单已取消";
            default -> "订单状态更新";
        };
        Long notifyUser = uid.equals(order.getBuyerId()) ? order.getSellerId() : order.getBuyerId();
        notificationService.send(notifyUser, "订单状态变更", msg, "ORDER");
        return orderMapper.findById(orderId);
    }

    private void validateTransition(TradeOrder order, Integer newStatus, Long uid) {
        int cur = order.getStatus();
        switch (newStatus) {
            case 1 -> { // 付款：买家操作
                if (cur != 0 || !uid.equals(order.getBuyerId())) throw new BusinessException("无法付款");
            }
            case 2 -> { // 发货：卖家操作
                if (cur != 1 || !uid.equals(order.getSellerId())) throw new BusinessException("无法发货");
            }
            case 3 -> { // 收货：买家操作
                if (cur != 2 || !uid.equals(order.getBuyerId())) throw new BusinessException("无法确认收货");
            }
            case 4 -> { // 取消：仅待付款，买家操作
                if (cur != 0 || !uid.equals(order.getBuyerId())) throw new BusinessException("只能取消待付款订单");
            }
            default -> throw new BusinessException("无效状态");
        }
    }

    public PageResult<TradeOrder> myOrders(int page, int size) {
        int offset = (page - 1) * size;
        List<TradeOrder> rows = orderMapper.findByUser(UserContext.getUserId(), offset, size);
        return new PageResult<>(rows, orderMapper.countByUser(UserContext.getUserId()));
    }

    public TradeOrder getById(Long id) {
        return orderMapper.findById(id);
    }

    /** 交易互评 */
    @Transactional
    public void review(ReviewRequest req) {
        TradeOrder order = orderMapper.findById(req.getOrderId());
        if (order == null || order.getStatus() != 3) {
            throw new BusinessException("订单未完成，无法评价");
        }
        Long uid = UserContext.getUserId();
        if (!uid.equals(order.getBuyerId()) && !uid.equals(order.getSellerId())) {
            throw new BusinessException("无权评价");
        }
        if (reviewMapper.findByOrderAndReviewer(req.getOrderId(), uid) != null) {
            throw new BusinessException("已评价过");
        }
        TradeReview review = new TradeReview();
        review.setOrderId(req.getOrderId());
        review.setReviewerId(uid);
        review.setRevieweeId(req.getRevieweeId());
        review.setRating(req.getRating());
        review.setContent(req.getContent());
        reviewMapper.insert(review);
        // 更新被评价人信用分
        User user = userMapper.findById(req.getRevieweeId());
        int delta = req.getRating() >= 4 ? 2 : (req.getRating() <= 2 ? -2 : 0);
        user.setCreditScore(Math.max(0, user.getCreditScore() + delta));
        userMapper.update(user);
    }

    public List<TradeReview> getReviews(Long orderId) {
        return reviewMapper.findByOrderId(orderId);
    }
}
