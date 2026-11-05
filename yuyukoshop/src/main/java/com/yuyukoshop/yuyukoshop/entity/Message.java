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
 * 消息实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("message")
public class Message {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 发送者ID (0表示系统消息) */
    private Long senderId;
    
    /** 接收者ID */
    private Long receiverId;
    
    /** 消息内容 */
    private String content;
    
    /** 消息类型: 0-普通私信 1-系统通知 */
    private Integer type;
    
    /** 是否已读: 0-未读 1-已读 */
    private Integer isRead;
    
    /** 创建时间 */
    private LocalDateTime createTime;
}
