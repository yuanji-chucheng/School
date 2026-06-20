package com.example.demo.mapper;

import com.example.demo.entity.Notification;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface NotificationMapper {
    int insert(Notification notification);
    List<Notification> findByUser(@Param("userId") Long userId,
                                  @Param("offset") int offset,
                                  @Param("limit") int limit);
    long countByUser(Long userId);
    int markRead(@Param("id") Long id, @Param("userId") Long userId);
    int markAllRead(Long userId);
}
