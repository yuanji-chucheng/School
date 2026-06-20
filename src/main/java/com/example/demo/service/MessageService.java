package com.example.demo.service;

import com.example.demo.common.BusinessException;
import com.example.demo.common.PageResult;
import com.example.demo.dto.MessageRequest;
import com.example.demo.entity.Message;
import com.example.demo.mapper.MessageMapper;
import com.example.demo.util.UserContext;
import org.springframework.stereotype.Service;

import java.util.List;

/** 私信服务 */
@Service
public class MessageService {

    private final MessageMapper messageMapper;
    private final NotificationService notificationService;

    public MessageService(MessageMapper messageMapper, NotificationService notificationService) {
        this.messageMapper = messageMapper;
        this.notificationService = notificationService;
    }

    public Message send(MessageRequest req) {
        if (req.getReceiverId().equals(UserContext.getUserId())) {
            throw new BusinessException("不能给自己发消息");
        }
        Message msg = new Message();
        msg.setSenderId(UserContext.getUserId());
        msg.setReceiverId(req.getReceiverId());
        msg.setContent(req.getContent());
        msg.setIsRead(0);
        messageMapper.insert(msg);
        notificationService.send(req.getReceiverId(), "新私信", req.getContent(), "MESSAGE");
        return msg;
    }

    public PageResult<Message> conversation(Long peerId, int page, int size) {
        int offset = (page - 1) * size;
        List<Message> rows = messageMapper.findConversation(UserContext.getUserId(), peerId, offset, size);
        messageMapper.markRead(peerId, UserContext.getUserId());
        return new PageResult<>(rows, messageMapper.countConversation(UserContext.getUserId(), peerId));
    }

    public List<Message> contacts() {
        return messageMapper.findContacts(UserContext.getUserId());
    }
}
