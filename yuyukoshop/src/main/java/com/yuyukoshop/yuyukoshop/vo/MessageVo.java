package com.yuyukoshop.yuyukoshop.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 消息VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageVo {
    private Long id;
    
    /** 发送者ID */
    private Long senderId;
    
    /** 发送者用户名 */
    private String senderName;
    
    /** 发送者头像 */
    private String senderAvatar;
    
    /** 接收者ID */
    private Long receiverId;
    
    /** 消息内容 */
    private String content;
    
    /** 消息类型: 0-普通私信 1-系统通知 */
    private Integer type;
    
    /** 是否已读 */
    private Boolean isRead;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 是否是自己发送的 */
    private Boolean isSelf;
}
