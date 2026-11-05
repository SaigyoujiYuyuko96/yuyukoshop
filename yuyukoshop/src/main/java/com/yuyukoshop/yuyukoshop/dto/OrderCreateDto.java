package com.yuyukoshop.yuyukoshop.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class OrderCreateDto {

    @NotNull(message = "收货地址ID不能为空")
    private Long addressId;

    private String remark;
}
