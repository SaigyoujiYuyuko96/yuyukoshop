package com.yuyukoshop.yuyukoshop.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 会话VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationVo {
    /** 对方ID */
    private Long targetId;
    
    /** 对方用户名 */
    private String targetName;
    
    /** 对方头像 */
    private String targetAvatar;
    
    /** 最后一条消息 */
    private String lastMessage;
    
    /** 最后消息时间 */
    private LocalDateTime lastTime;
    
    /** 未读消息数 */
    private Integer unreadCount;
    
    /** 消息类型: 0-普通私信 1-系统通知 */
    private Integer type;
}
