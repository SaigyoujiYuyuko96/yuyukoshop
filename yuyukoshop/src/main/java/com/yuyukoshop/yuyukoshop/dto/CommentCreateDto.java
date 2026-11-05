package com.yuyukoshop.yuyukoshop.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CommentCreateDto {

    @NotBlank(message = "评论内容不能为空")
    private String content;

    private Long parentId;
}
