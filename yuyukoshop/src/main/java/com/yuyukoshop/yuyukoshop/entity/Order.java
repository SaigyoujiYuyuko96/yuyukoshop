package com.yuyukoshop.yuyukoshop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("order_info")
public class Order {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String orderNo;

    private BigDecimal totalAmount;

    private Integer status;

    private String paymentMethod;

    private String tradeNo;

    private String shippingAddress;

    private String receiverName;

    private String receiverPhone;

    private String shippingCompany;

    private String trackingNo;

    private String remark;

    private LocalDateTime payTime;

    private LocalDateTime shipTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
