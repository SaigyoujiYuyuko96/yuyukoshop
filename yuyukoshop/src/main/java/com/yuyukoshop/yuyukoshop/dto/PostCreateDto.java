package com.yuyukoshop.yuyukoshop.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class PostCreateDto {

    @NotNull(message = "版块ID不能为空")
    private Long forumId;

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;
}
