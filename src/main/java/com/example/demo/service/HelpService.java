package com.example.demo.service;

import com.example.demo.common.BusinessException;
import com.example.demo.common.PageResult;
import com.example.demo.dto.AuditRequest;
import com.example.demo.dto.HelpRequestDto;
import com.example.demo.dto.ReviewRequest;
import com.example.demo.entity.HelpOrder;
import com.example.demo.entity.HelpRequest;
import com.example.demo.entity.HelpReview;
import com.example.demo.entity.User;
import com.example.demo.mapper.HelpOrderMapper;
import com.example.demo.mapper.HelpRequestMapper;
import com.example.demo.mapper.HelpReviewMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.util.UserContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

/** 互助需求服务 */
@Service
public class HelpService {

    private final HelpRequestMapper requestMapper;
    private final HelpOrderMapper helpOrderMapper;
    private final HelpReviewMapper helpReviewMapper;
    private final UserMapper userMapper;
    private final NotificationService notificationService;

    public HelpService(HelpRequestMapper requestMapper, HelpOrderMapper helpOrderMapper,
                       HelpReviewMapper helpReviewMapper, UserMapper userMapper,
                       NotificationService notificationService) {
        this.requestMapper = requestMapper;
        this.helpOrderMapper = helpOrderMapper;
        this.helpReviewMapper = helpReviewMapper;
        this.userMapper = userMapper;
        this.notificationService = notificationService;
    }

    public HelpRequest publish(HelpRequestDto dto) {
        if (!StringUtils.hasText(dto.getTitle())) {
            throw new BusinessException("标题不能为空");
        }
        if (!StringUtils.hasText(dto.getDescription())) {
            throw new BusinessException("描述不能为空");
        }
        if (dto.getReward() == null) {
            throw new BusinessException("酬劳不能为空");
        }
        if (dto.getReward().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("酬劳不能小于0");
        }
        HelpRequest req = new HelpRequest();
        req.setUserId(UserContext.getUserId());
        req.setTitle(dto.getTitle().trim());
        req.setDescription(dto.getDescription().trim());
        req.setReward(dto.getReward());
        req.setStatus(0); // 待审核
        requestMapper.insert(req);
        return requestMapper.findById(req.getId());
    }

    public PageResult<HelpRequest> list(Integer status, Long userId, int page, int size) {
        int offset = (page - 1) * size;
        // 公开浏览默认只显示待接单
        if (status == null && userId == null) {
            status = 1;
        }
        List<HelpRequest> rows = requestMapper.search(status, userId, offset, size);
        return new PageResult<>(rows, requestMapper.countSearch(status, userId));
    }

    public HelpRequest getById(Long id) {
        return requestMapper.findById(id);
    }

    /** 接单 */
    @Transactional
    public HelpOrder accept(Long requestId) {
        HelpRequest req = requestMapper.findById(requestId);
        if (req == null || req.getStatus() != 1) {
            throw new BusinessException("需求不可接单");
        }
        if (req.getUserId().equals(UserContext.getUserId())) {
            throw new BusinessException("不能接自己的单");
        }
        HelpOrder order = new HelpOrder();
        order.setRequestId(requestId);
        order.setHelperId(UserContext.getUserId());
        order.setStatus(1);
        helpOrderMapper.insert(order);
        requestMapper.updateStatus(requestId, 2);
        notificationService.send(req.getUserId(), "互助需求已被接单", "您的需求《" + req.getTitle() + "》已被接单", "HELP");
        return helpOrderMapper.findById(order.getId());
    }

    /** 完成互助（接单者可标记完成） */
    @Transactional
    public void complete(Long requestId) {
        HelpRequest req = requestMapper.findById(requestId);
        if (req == null || req.getStatus() != 2) throw new BusinessException("状态错误");
        HelpOrder order = helpOrderMapper.findByRequestId(requestId);
        if (order == null) throw new BusinessException("接单记录不存在");
        Long uid = UserContext.getUserId();
        if (!order.getHelperId().equals(uid) && !req.getUserId().equals(uid)) {
            throw new BusinessException("无权操作");
        }
        helpOrderMapper.updateStatus(order.getId(), 2);
        requestMapper.updateStatus(requestId, 3);
        notificationService.send(req.getUserId(), "互助已完成", "需求《" + req.getTitle() + "》已由接单者标记完成", "HELP");
    }

    /** 取消需求 */
    public void cancel(Long requestId) {
        HelpRequest req = requestMapper.findById(requestId);
        if (req == null) throw new BusinessException("需求不存在");
        if (!req.getUserId().equals(UserContext.getUserId())) throw new BusinessException("无权取消");
        if (req.getStatus() != 1) throw new BusinessException("只能取消待接单需求");
        requestMapper.updateStatus(requestId, 4);
    }

    public HelpOrder getHelpOrder(Long requestId) {
        return helpOrderMapper.findByRequestId(requestId);
    }

    /** 我的互助接单 */
    public PageResult<HelpOrder> myHelpOrders(int page, int size) {
        int offset = (page - 1) * size;
        Long helperId = UserContext.getUserId();
        List<HelpOrder> rows = helpOrderMapper.findByHelperId(helperId, offset, size);
        return new PageResult<>(rows, helpOrderMapper.countByHelperId(helperId));
    }

    /** 管理员审核互助帖 */
    public void audit(Long id, AuditRequest req) {
        if (!UserContext.isAdmin()) throw new BusinessException(403, "需要管理员权限");
        HelpRequest helpReq = requestMapper.findById(id);
        if (helpReq == null) throw new BusinessException("互助需求不存在");
        if (helpReq.getStatus() != 0) throw new BusinessException("该需求不在待审核状态");
        helpReq.setStatus(Boolean.TRUE.equals(req.getApproved()) ? 1 : 5);
        requestMapper.update(helpReq);
        notificationService.send(helpReq.getUserId(),
                Boolean.TRUE.equals(req.getApproved()) ? "互助帖审核通过" : "互助帖审核驳回",
                "您的互助需求《" + helpReq.getTitle() + "》" + (Boolean.TRUE.equals(req.getApproved()) ? "已通过审核" : "被驳回：" + req.getReason()),
                "HELP_AUDIT");
    }

    public PageResult<HelpRequest> pendingRequests(int page, int size) {
        if (!UserContext.isAdmin()) throw new BusinessException(403, "需要管理员权限");
        int offset = (page - 1) * size;
        return new PageResult<>(requestMapper.findPending(offset, size), requestMapper.countPending());
    }

    /** 管理员下架互助帖 */
    public void offShelf(Long id) {
        if (!UserContext.isAdmin()) throw new BusinessException(403, "需要管理员权限");
        HelpRequest req = requestMapper.findById(id);
        if (req == null) throw new BusinessException("互助需求不存在");
        requestMapper.updateStatus(id, 5);
    }

    /** 互助互评 */
    @Transactional
    public void review(Long helpOrderId, ReviewRequest req) {
        HelpOrder order = helpOrderMapper.findById(helpOrderId);
        if (order == null || order.getStatus() != 2) throw new BusinessException("互助未完成");
        HelpRequest helpReq = requestMapper.findById(order.getRequestId());
        Long uid = UserContext.getUserId();
        if (!uid.equals(helpReq.getUserId()) && !uid.equals(order.getHelperId())) {
            throw new BusinessException("无权评价");
        }
        if (helpReviewMapper.findByHelpOrderAndReviewer(helpOrderId, uid) != null) {
            throw new BusinessException("已评价过");
        }
        HelpReview review = new HelpReview();
        review.setHelpOrderId(helpOrderId);
        review.setReviewerId(uid);
        review.setRevieweeId(req.getRevieweeId());
        review.setRating(req.getRating());
        review.setContent(req.getContent());
        helpReviewMapper.insert(review);
        User user = userMapper.findById(req.getRevieweeId());
        int delta = req.getRating() >= 4 ? 2 : (req.getRating() <= 2 ? -2 : 0);
        user.setCreditScore(Math.max(0, user.getCreditScore() + delta));
        userMapper.update(user);
    }
}
