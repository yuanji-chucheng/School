package com.example.demo.service;

import com.example.demo.common.BusinessException;
import com.example.demo.common.PageResult;
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
        HelpRequest req = new HelpRequest();
        req.setUserId(UserContext.getUserId());
        req.setTitle(dto.getTitle());
        req.setDescription(dto.getDescription());
        req.setReward(dto.getReward());
        req.setStatus(0);
        requestMapper.insert(req);
        return requestMapper.findById(req.getId());
    }

    public PageResult<HelpRequest> list(Integer status, Long userId, int page, int size) {
        int offset = (page - 1) * size;
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
        if (req == null || req.getStatus() != 0) {
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
        requestMapper.updateStatus(requestId, 1);
        notificationService.send(req.getUserId(), "互助需求已被接单", "您的需求《" + req.getTitle() + "》已被接单", "HELP");
        return helpOrderMapper.findById(order.getId());
    }

    /** 完成互助 */
    @Transactional
    public void complete(Long requestId) {
        HelpRequest req = requestMapper.findById(requestId);
        if (req == null || req.getStatus() != 1) throw new BusinessException("状态错误");
        HelpOrder order = helpOrderMapper.findByRequestId(requestId);
        if (order == null) throw new BusinessException("接单记录不存在");
        // 发布者确认完成
        if (!req.getUserId().equals(UserContext.getUserId())) {
            throw new BusinessException("仅发布者可确认完成");
        }
        helpOrderMapper.updateStatus(order.getId(), 2);
        requestMapper.updateStatus(requestId, 2);
        notificationService.send(order.getHelperId(), "互助已完成", "需求《" + req.getTitle() + "》已完成", "HELP");
    }

    /** 取消需求 */
    public void cancel(Long requestId) {
        HelpRequest req = requestMapper.findById(requestId);
        if (req == null) throw new BusinessException("需求不存在");
        if (!req.getUserId().equals(UserContext.getUserId())) throw new BusinessException("无权取消");
        if (req.getStatus() != 0) throw new BusinessException("只能取消待接单需求");
        requestMapper.updateStatus(requestId, 3);
    }

    public HelpOrder getHelpOrder(Long requestId) {
        return helpOrderMapper.findByRequestId(requestId);
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
