package com.yuyukoshop.yuyukoshop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 会话实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("conversation")
public class Conversation {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 用户ID */
    private Long userId;
    
    /** 对方ID (0表示系统) */
    private Long targetId;
    
    /** 最后一条消息内容 */
    private String lastMessage;
    
    /** 最后消息时间 */
    private LocalDateTime lastTime;
    
    /** 未读消息数 */
    private Integer unreadCount;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
}
