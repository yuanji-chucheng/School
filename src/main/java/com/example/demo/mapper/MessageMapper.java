package com.example.demo.mapper;

import com.example.demo.entity.Message;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface MessageMapper {
    int insert(Message message);
    List<Message> findConversation(@Param("userId") Long userId,
                                   @Param("peerId") Long peerId,
                                   @Param("offset") int offset,
                                   @Param("limit") int limit);
    long countConversation(@Param("userId") Long userId, @Param("peerId") Long peerId);
    List<Message> findContacts(@Param("userId") Long userId);
    int markRead(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId);
    long countUnread(@Param("userId") Long userId);
    int markAllRead(@Param("userId") Long userId);
}
