package com.example.demo.service;

import com.example.demo.entity.Notification;
import com.example.demo.mapper.NotificationMapper;
import org.springframework.stereotype.Service;

/** 通知服务 */
@Service
public class NotificationService {

    private final NotificationMapper notificationMapper;

    public NotificationService(NotificationMapper notificationMapper) {
        this.notificationMapper = notificationMapper;
    }

    /** 发送系统通知 */
    public void send(Long userId, String title, String content, String type) {
        Notification n = new Notification();
        n.setUserId(userId);
        n.setTitle(title);
        n.setContent(content);
        n.setType(type);
        n.setIsRead(0);
        notificationMapper.insert(n);
    }
}
