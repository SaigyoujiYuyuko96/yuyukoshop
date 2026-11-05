package com.yuyukoshop.yuyukoshop.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class RefundRequestDto {

    @NotBlank(message = "退款原因不能为空")
    private String reason;
}
