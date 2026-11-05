package com.yuyukoshop.yuyukoshop.controller;

import com.yuyukoshop.yuyukoshop.common.Result;
import com.yuyukoshop.yuyukoshop.service.MessageService;
import com.yuyukoshop.yuyukoshop.vo.ConversationVo;
import com.yuyukoshop.yuyukoshop.vo.MessageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 消息控制器
 */
@RestController
@RequestMapping("/api/messages")
public class MessageController extends BaseController {

    @Autowired
    private MessageService messageService;

    /**
     * 发送私信
     */
    @PostMapping("/send")
    public Result<MessageVo> sendMessage(@RequestBody Map<String, Object> body) {
        Long receiverId = Long.valueOf(body.get("receiverId").toString());
        String content = body.get("content").toString();
        MessageVo message = messageService.sendMessage(getCurrentUserId(), receiverId, content);
        return Result.success(message);
    }

    /**
     * 获取会话列表
     */
    @GetMapping("/conversations")
    public Result<List<ConversationVo>> getConversations() {
        List<ConversationVo> conversations = messageService.getConversations(getCurrentUserId());
        return Result.success(conversations);
    }

    /**
     * 获取与某人的聊天记录
     */
    @GetMapping("/chat/{targetId}")
    public Result<List<MessageVo>> getChatHistory(
            @PathVariable Long targetId,
            @RequestParam(defaultValue = "50") Long limit) {
        List<MessageVo> messages = messageService.getChatHistory(getCurrentUserId(), targetId, limit);
        return Result.success(messages);
    }

    /**
     * 获取未读消息数
     */
    @GetMapping("/unread-count")
    public Result<Long> getUnreadCount() {
        Long count = messageService.getUnreadCount(getCurrentUserId());
        return Result.success(count);
    }
}
