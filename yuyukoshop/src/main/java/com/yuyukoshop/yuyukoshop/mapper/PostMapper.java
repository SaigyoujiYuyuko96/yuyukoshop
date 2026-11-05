package com.yuyukoshop.yuyukoshop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuyukoshop.yuyukoshop.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface PostMapper extends BaseMapper<Post> {

    @Update("UPDATE post SET view_count = view_count + 1 WHERE id = #{id}")
    void incrementViewCount(@Param("id") Long id);

    @Update("UPDATE post SET like_count = like_count + 1 WHERE id = #{id}")
    void incrementLikeCount(@Param("id") Long id);

    @Update("UPDATE post SET like_count = like_count - 1 WHERE id = #{id}")
    void decrementLikeCount(@Param("id") Long id);

    @Update("UPDATE post SET comment_count = comment_count + 1 WHERE id = #{id}")
    void incrementCommentCount(@Param("id") Long id);
}
