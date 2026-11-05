package com.yuyukoshop.yuyukoshop.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderItemVo {

    private Long id;
    private Long productId;
    private String productName;
    private String productThumbnail;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subtotal;
}
