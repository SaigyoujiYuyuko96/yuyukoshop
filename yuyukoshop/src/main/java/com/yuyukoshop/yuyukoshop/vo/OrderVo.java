package com.yuyukoshop.yuyukoshop.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderVo {

    private Long id;
    private String orderNo;
    private BigDecimal totalAmount;
    private Integer status;
    private String statusText;
    private String paymentMethod;
    private String shippingAddress;
    private String receiverName;
    private String receiverPhone;
    private String shippingCompany;
    private String trackingNo;
    private String remark;
    private LocalDateTime payTime;
    private LocalDateTime shipTime;
    private LocalDateTime createTime;
    private List<OrderItemVo> items;
}
