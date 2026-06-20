package com.example.demo.controller;

import com.example.demo.common.PageResult;
import com.example.demo.common.Result;
import com.example.demo.dto.MessageRequest;
import com.example.demo.entity.Message;
import com.example.demo.service.MessageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** 私信接口 */
@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public Result<Message> send(@RequestBody MessageRequest req) {
        return Result.ok(messageService.send(req));
    }

    @GetMapping("/contacts")
    public Result<List<Message>> contacts() {
        return Result.ok(messageService.contacts());
    }

    @GetMapping("/conversation/{peerId}")
    public PageResult<Message> conversation(@PathVariable Long peerId,
                                            @RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "50") int size) {
        return messageService.conversation(peerId, page, size);
    }
}
