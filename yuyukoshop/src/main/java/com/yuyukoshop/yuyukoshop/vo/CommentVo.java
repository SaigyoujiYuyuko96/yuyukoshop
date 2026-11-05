package com.yuyukoshop.yuyukoshop.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class CommentVo {

    private Long id;
    private Long userId;
    private String username;
    private String userAvatar;
    private String content;
    private Long parentId;
    private LocalDateTime createTime;
    private List<CommentVo> replies;
}
