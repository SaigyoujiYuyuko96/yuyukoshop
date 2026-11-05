package com.yuyukoshop.yuyukoshop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuyukoshop.yuyukoshop.entity.Conversation;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会话Mapper
 */
@Mapper
public interface ConversationMapper extends BaseMapper<Conversation> {
}
