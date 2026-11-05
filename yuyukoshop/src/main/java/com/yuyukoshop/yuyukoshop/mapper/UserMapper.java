package com.yuyukoshop.yuyukoshop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuyukoshop.yuyukoshop.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
