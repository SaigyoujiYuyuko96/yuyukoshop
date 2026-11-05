package com.yuyukoshop.yuyukoshop.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PostVo {

    private Long id;
    private Long userId;
    private String username;
    private String userAvatar;
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
}
