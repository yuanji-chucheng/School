package com.example.demo.service;

import com.example.demo.common.PageResult;
import com.example.demo.entity.Notification;
import com.example.demo.mapper.NotificationMapper;
import com.example.demo.util.UserContext;
import org.springframework.stereotype.Service;

import java.util.List;

/** 通知查询服务 */
@Service
public class NotificationQueryService {

    private final NotificationMapper notificationMapper;

    public NotificationQueryService(NotificationMapper notificationMapper) {
        this.notificationMapper = notificationMapper;
    }

    public PageResult<Notification> list(int page, int size) {
        int offset = (page - 1) * size;
        List<Notification> rows = notificationMapper.findByUser(UserContext.getUserId(), offset, size);
        return new PageResult<>(rows, notificationMapper.countByUser(UserContext.getUserId()));
    }

    public void markRead(Long id) {
        notificationMapper.markRead(id, UserContext.getUserId());
    }

    public void markAllRead() {
        notificationMapper.markAllRead(UserContext.getUserId());
    }
}
