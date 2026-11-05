package com.yuyukoshop.yuyukoshop.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CartItemVo {

    private Long id;
    private Long productId;
    private String productName;
    private String productThumbnail;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;
    private LocalDateTime createTime;
}
