package com.yuyukoshop.yuyukoshop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuyukoshop.yuyukoshop.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}
