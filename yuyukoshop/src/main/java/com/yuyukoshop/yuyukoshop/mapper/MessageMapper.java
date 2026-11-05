package com.yuyukoshop.yuyukoshop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuyukoshop.yuyukoshop.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 消息Mapper
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {
    
    /**
     * 标记消息已读
     */
    @Update("UPDATE message SET is_read = 1 WHERE receiver_id = #{receiverId} AND sender_id = #{senderId} AND is_read = 0")
    int markAsRead(@Param("receiverId") Long receiverId, @Param("senderId") Long senderId);
    
    /**
     * 标记所有消息已读
     */
    @Update("UPDATE message SET is_read = 1 WHERE receiver_id = #{receiverId} AND is_read = 0")
    int markAllAsRead(@Param("receiverId") Long receiverId);
}
