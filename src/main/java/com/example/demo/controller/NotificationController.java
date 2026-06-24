package com.example.demo.controller;

import com.example.demo.common.PageResult;
import com.example.demo.common.Result;
import com.example.demo.entity.Notification;
import com.example.demo.service.NotificationQueryService;
import org.springframework.web.bind.annotation.*;

/** 通知接口 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationQueryService notificationQueryService;

    public NotificationController(NotificationQueryService notificationQueryService) {
        this.notificationQueryService = notificationQueryService;
    }

    @GetMapping
    public PageResult<Notification> list(@RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "20") int size) {
        return notificationQueryService.list(page, size);
    }

    @PostMapping("/{id:\\d+}/read")
    public Result<Void> markRead(@PathVariable Long id) {
        notificationQueryService.markRead(id);
        return Result.ok(null);
    }

    @PostMapping("/read-all")
    public Result<Void> markAllRead() {
        notificationQueryService.markAllRead();
        return Result.ok(null);
    }

    @GetMapping("/unread-count")
    public Result<Long> unreadCount() {
        return Result.ok(notificationQueryService.countUnread());
    }
}
