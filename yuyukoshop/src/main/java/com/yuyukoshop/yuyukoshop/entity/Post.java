package com.yuyukoshop.yuyukoshop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("post")
public class Post {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long forumId;

    private String title;

    private String content;

    private Integer viewCount;

    private Integer likeCount;

    private Integer commentCount;

    private Integer isEssence;

    private Integer isTop;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
