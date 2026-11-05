package com.yuyukoshop.yuyukoshop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yuyukoshop.yuyukoshop.entity.Conversation;
import com.yuyukoshop.yuyukoshop.entity.Message;
import com.yuyukoshop.yuyukoshop.entity.User;
import com.yuyukoshop.yuyukoshop.exception.BusinessException;
import com.yuyukoshop.yuyukoshop.mapper.ConversationMapper;
import com.yuyukoshop.yuyukoshop.mapper.MessageMapper;
import com.yuyukoshop.yuyukoshop.mapper.UserMapper;
import com.yuyukoshop.yuyukoshop.vo.ConversationVo;
import com.yuyukoshop.yuyukoshop.vo.MessageVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 消息服务
 */
@Slf4j
@Service
public class MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private ConversationMapper conversationMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 发送私信
     */
    @Transactional(rollbackFor = Exception.class)
    public MessageVo sendMessage(Long senderId, Long receiverId, String content) {
        if (senderId.equals(receiverId)) {
            throw new BusinessException("不能给自己发送私信");
        }

        User receiver = userMapper.selectById(receiverId);
        if (receiver == null) {
            throw new BusinessException("接收者不存在");
        }

        // 创建消息
        Message message = Message.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .content(content)
                .type(0) // 普通私信
                .isRead(0)
                .createTime(LocalDateTime.now())
                .build();
        messageMapper.insert(message);

        // 更新发送者的会话
        updateConversation(senderId, receiverId, content);
        // 更新接收者的会话
        updateConversation(receiverId, senderId, content);

        return convertToVo(message, senderId);
    }

    /**
     * 发送系统通知
     */
    @Transactional(rollbackFor = Exception.class)
    public void sendSystemNotification(Long receiverId, String content) {
        Message message = Message.builder()
                .senderId(0L) // 系统消息
                .receiverId(receiverId)
                .content(content)
                .type(1) // 系统通知
                .isRead(0)
                .createTime(LocalDateTime.now())
                .build();
        messageMapper.insert(message);

        // 更新会话
        Conversation conversation = conversationMapper.selectOne(
                new LambdaQueryWrapper<Conversation>()
                        .eq(Conversation::getUserId, receiverId)
                        .eq(Conversation::getTargetId, 0L)
        );
        if (conversation == null) {
            conversation = Conversation.builder()
                    .userId(receiverId)
                    .targetId(0L)
                    .lastMessage(content)
                    .lastTime(LocalDateTime.now())
                    .unreadCount(1)
                    .build();
            conversationMapper.insert(conversation);
        } else {
            conversation.setLastMessage(content);
            conversation.setLastTime(LocalDateTime.now());
            conversation.setUnreadCount(conversation.getUnreadCount() + 1);
            conversationMapper.updateById(conversation);
        }
    }

    /**
     * 获取会话列表
     */
    public List<ConversationVo> getConversations(Long userId) {
        List<Conversation> conversations = conversationMapper.selectList(
                new LambdaQueryWrapper<Conversation>()
                        .eq(Conversation::getUserId, userId)
                        .orderByDesc(Conversation::getLastTime)
        );

        return conversations.stream()
                .map(this::convertToConversationVo)
                .collect(Collectors.toList());
    }

    /**
     * 获取与某人的聊天记录
     */
    public List<MessageVo> getChatHistory(Long userId, Long targetId, Long limit) {
        List<Message> messages = messageMapper.selectList(
                new LambdaQueryWrapper<Message>()
                        .and(wrapper -> wrapper
                                .and(w -> w.eq(Message::getSenderId, userId).eq(Message::getReceiverId, targetId))
                                .or(w -> w.eq(Message::getSenderId, targetId).eq(Message::getReceiverId, userId))
                        )
                        .eq(Message::getType, targetId == 0 ? 1 : 0) // 系统消息type=1
                        .orderByDesc(Message::getCreateTime)
                        .last("LIMIT " + limit)
        );

        // 标记已读
        if (targetId == 0) {
            messageMapper.markAllAsRead(userId);
        } else {
            messageMapper.markAsRead(userId, targetId);
        }

        // 重置未读数
        Conversation conversation = conversationMapper.selectOne(
                new LambdaQueryWrapper<Conversation>()
                        .eq(Conversation::getUserId, userId)
                        .eq(Conversation::getTargetId, targetId)
        );
        if (conversation != null) {
            conversation.setUnreadCount(0);
            conversationMapper.updateById(conversation);
        }

        Collections.reverse(messages);
        return messages.stream()
                .map(m -> convertToVo(m, userId))
                .collect(Collectors.toList());
    }

    /**
     * 获取未读消息总数
     */
    public Long getUnreadCount(Long userId) {
        return messageMapper.selectCount(
                new LambdaQueryWrapper<Message>()
                        .eq(Message::getReceiverId, userId)
                        .eq(Message::getIsRead, 0)
        );
    }

    /**
     * 更新会话
     */
    private void updateConversation(Long userId, Long targetId, String content) {
        Conversation conversation = conversationMapper.selectOne(
                new LambdaQueryWrapper<Conversation>()
                        .eq(Conversation::getUserId, userId)
                        .eq(Conversation::getTargetId, targetId)
        );

        if (conversation == null) {
            conversation = Conversation.builder()
                    .userId(userId)
                    .targetId(targetId)
                    .lastMessage(content)
                    .lastTime(LocalDateTime.now())
                    .unreadCount(userId.equals(targetId) ? 0 : 1)
                    .build();
            conversationMapper.insert(conversation);
        } else {
            conversation.setLastMessage(content);
            conversation.setLastTime(LocalDateTime.now());
            if (!userId.equals(targetId)) {
                // 只有接收者才增加未读数
                conversation.setUnreadCount(conversation.getUnreadCount() + 1);
            }
            conversationMapper.updateById(conversation);
        }
    }

    private MessageVo convertToVo(Message message, Long currentUserId) {
        User sender = message.getSenderId() == 0 ? null : userMapper.selectById(message.getSenderId());
        
        return MessageVo.builder()
                .id(message.getId())
                .senderId(message.getSenderId())
                .senderName(sender != null ? sender.getNickname() : "系统通知")
                .senderAvatar(sender != null ? sender.getAvatar() : "")
                .receiverId(message.getReceiverId())
                .content(message.getContent())
                .type(message.getType())
                .isRead(message.getIsRead() == 1)
                .createTime(message.getCreateTime())
                .isSelf(message.getSenderId().equals(currentUserId))
                .build();
    }

    private ConversationVo convertToConversationVo(Conversation conversation) {
        Long targetId = conversation.getTargetId();
        String targetName;
        String targetAvatar;

        if (targetId == 0) {
            targetName = "系统通知";
            targetAvatar = "";
        } else {
            User target = userMapper.selectById(targetId);
            if (target != null) {
                targetName = target.getNickname() != null ? target.getNickname() : target.getUsername();
                targetAvatar = target.getAvatar();
            } else {
                targetName = "未知用户";
                targetAvatar = "";
            }
        }

        return ConversationVo.builder()
                .targetId(targetId)
                .targetName(targetName)
                .targetAvatar(targetAvatar)
                .lastMessage(conversation.getLastMessage())
                .lastTime(conversation.getLastTime())
                .unreadCount(conversation.getUnreadCount())
                .type(targetId == 0 ? 1 : 0)
                .build();
    }
}
