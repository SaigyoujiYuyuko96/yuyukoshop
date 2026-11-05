package com.yuyukoshop.yuyukoshop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuyukoshop.yuyukoshop.dto.OrderCreateDto;
import com.yuyukoshop.yuyukoshop.dto.RefundRequestDto;
import com.yuyukoshop.yuyukoshop.entity.*;
import com.yuyukoshop.yuyukoshop.exception.BusinessException;
import com.yuyukoshop.yuyukoshop.mapper.*;
import com.yuyukoshop.yuyukoshop.vo.OrderItemVo;
import com.yuyukoshop.yuyukoshop.vo.OrderVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private CartItemMapper cartItemMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private UserAddressMapper userAddressMapper;

    @Transactional(rollbackFor = Exception.class)
    public Order createOrder(Long userId, OrderCreateDto dto) {
        UserAddress address = userAddressMapper.selectById(dto.getAddressId());
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException("收货地址不存在");
        }

        List<CartItem> cartItems = cartItemMapper.selectList(
                new LambdaQueryWrapper<CartItem>().eq(CartItem::getUserId, userId)
        );
        if (cartItems.isEmpty()) {
            throw new BusinessException("购物车为空");
        }

        // 批量查询商品信息，避免N+1问题
        List<Long> productIds = cartItems.stream().map(CartItem::getProductId).collect(Collectors.toList());
        List<Product> products = productMapper.selectBatchIds(productIds);
        
        // 构建商品ID到商品对象的映射
        java.util.Map<Long, Product> productMap = products.stream()
                .collect(java.util.stream.Collectors.toMap(Product::getId, p -> p));

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem cartItem : cartItems) {
            Product product = productMap.get(cartItem.getProductId());
            if (product == null || product.getStatus() != 1) {
                throw new BusinessException("商品不存在或已下架 " + cartItem.getProductId());
            }
            if (product.getStock() < cartItem.getQuantity()) {
                throw new BusinessException("商品库存不足: " + product.getName());
            }
            totalAmount = totalAmount.add(cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }

        String orderNo = generateOrderNo();
        Order order = Order.builder()
                .userId(userId)
                .orderNo(orderNo)
                .totalAmount(totalAmount)
                .status(0)
                .paymentMethod("ALIPAY")
                .shippingAddress(address.getProvince() + address.getCity() + address.getDistrict() + address.getDetailAddress())
                .receiverName(address.getReceiverName())
                .receiverPhone(address.getReceiverPhone())
                .remark(dto.getRemark())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        orderMapper.insert(order);

        // 批量创建订单项
        List<OrderItem> orderItems = new java.util.ArrayList<>();
        for (CartItem cartItem : cartItems) {
            Product product = productMap.get(cartItem.getProductId());
            OrderItem orderItem = OrderItem.builder()
                    .orderId(order.getId())
                    .productId(cartItem.getProductId())
                    .productName(product.getName())
                    .productThumbnail(product.getThumbnail())
                    .quantity(cartItem.getQuantity())
                    .price(cartItem.getPrice())
                    .createTime(LocalDateTime.now())
                    .build();
            orderItems.add(orderItem);

            // 更新库存和销量
            product.setStock(product.getStock() - cartItem.getQuantity());
            product.setSales(product.getSales() + cartItem.getQuantity());
            product.setUpdateTime(LocalDateTime.now());
        }
        
        // 批量插入订单项
        orderItems.forEach(orderItemMapper::insert);
        
        // 批量更新商品库存
        products.forEach(productMapper::updateById);

        cartItemMapper.delete(
                new LambdaQueryWrapper<CartItem>().eq(CartItem::getUserId, userId)
        );

        return order;
    }

    public IPage<Order> getUserOrders(Long userId, Long page, Long size) {
        Page<Order> pageParam = new Page<>(page, size);
        return orderMapper.selectPage(pageParam,
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getUserId, userId)
                        .orderByDesc(Order::getCreateTime)
        );
    }

    public OrderVo getOrderDetail(Long userId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || !userId.equals(order.getUserId())) {
            throw new BusinessException("订单不存在");
        }
        return convertToVo(order);
    }

    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long userId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || !userId.equals(order.getUserId())) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException("只有未支付订单可以取消");
        }
        order.setStatus(-1);
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);

        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId)
        );
        
        // 批量查询商品并恢复库存
        List<Long> productIds = items.stream().map(OrderItem::getProductId).collect(Collectors.toList());
        List<Product> products = productMapper.selectBatchIds(productIds);
        
        for (OrderItem item : items) {
            Product product = products.stream()
                    .filter(p -> p.getId().equals(item.getProductId()))
                    .findFirst()
                    .orElse(null);
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
                product.setSales(product.getSales() - item.getQuantity());
                product.setUpdateTime(LocalDateTime.now());
            }
        }
        
        // 批量更新商品
        products.forEach(productMapper::updateById);
    }

    @Transactional(rollbackFor = Exception.class)
    public void requestRefund(Long userId, Long orderId, RefundRequestDto dto) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || !userId.equals(order.getUserId())) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != 2 && order.getStatus() != 3) {
            throw new BusinessException("当前订单状态不能申请退款");
        }
        order.setStatus(4);
        String remark = order.getRemark();
        if (remark == null) {
            remark = "";
        }
        order.setRemark(remark + "; 退款原因 " + dto.getReason());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    private String generateOrderNo() {
        return "YYK" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%04d", (int) (Math.random() * 10000));
    }

    private OrderVo convertToVo(Order order) {
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId())
        );
        List<OrderItemVo> itemVos = items.stream().map(item -> {
            BigDecimal price = item.getPrice() != null ? item.getPrice() : BigDecimal.ZERO;
            Integer quantity = item.getQuantity() != null ? item.getQuantity() : 0;
            return OrderItemVo.builder()
                    .id(item.getId())
                    .productId(item.getProductId())
                    .productName(item.getProductName())
                    .productThumbnail(item.getProductThumbnail())
                    .quantity(quantity)
                    .price(price)
                    .subtotal(price.multiply(BigDecimal.valueOf(quantity)))
                    .build();
        }).collect(Collectors.toList());

        return OrderVo.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .statusText(getStatusText(order.getStatus()))
                .paymentMethod(order.getPaymentMethod())
                .shippingAddress(order.getShippingAddress())
                .receiverName(order.getReceiverName())
                .receiverPhone(order.getReceiverPhone())
                .shippingCompany(order.getShippingCompany())
                .trackingNo(order.getTrackingNo())
                .remark(order.getRemark())
                .payTime(order.getPayTime())
                .shipTime(order.getShipTime())
                .createTime(order.getCreateTime())
                .items(itemVos)
                .build();
    }

    private String getStatusText(Integer status) {
        switch (status) {
            case 0: return "待支付";
            case 1: return "已支付";
            case 2: return "已发货";
            case 3: return "已完成";
            case 4: return "退款中";
            case 5: return "已退款";
            case -1: return "已取消";
            default: return "未知";
        }
    }

    // ==================== 支付相关方法 ====================

    /**
     * 根据订单ID获取订单（不校验用户归属，供支付回调使用）
     */
    public Order getOrderById(Long orderId) {
        return orderMapper.selectById(orderId);
    }

    /**
     * 根据商户订单号获取订单（供支付宝异步通知使用）
     */
    public Order getOrderByOrderNo(String orderNo) {
        return orderMapper.selectOne(
                new LambdaQueryWrapper<Order>().eq(Order::getOrderNo, orderNo)
        );
    }

    /**
     * 支付成功后更新订单状态
     *
     * @param orderId  订单ID
     * @param tradeNo  支付宝交易号
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateOrderPaySuccess(Long orderId, String tradeNo) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            log.warn("支付成功回调中订单不存在：{}", orderId);
            return;
        }
        if (order.getStatus() != 0) {
            log.info("订单已处理过，跳过重复通知，订单号：{}", order.getOrderNo());
            return;
        }
        order.setStatus(1);
        order.setTradeNo(tradeNo);
        order.setPayTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
        log.info("订单支付成功，订单号：{}，支付宝交易号：{}", order.getOrderNo(), tradeNo);
    }

    /**
     * 交易关闭后更新订单状态
     *
     * @param orderId 订单ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateOrderClosed(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || order.getStatus() != 0) {
            return;
        }
        order.setStatus(-1);
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);

        // 恢复库存
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId)
        );
        List<Long> productIds = items.stream().map(OrderItem::getProductId).collect(Collectors.toList());
        List<Product> products = productMapper.selectBatchIds(productIds);

        for (OrderItem item : items) {
            Product product = products.stream()
                    .filter(p -> p.getId().equals(item.getProductId()))
                    .findFirst()
                    .orElse(null);
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
                product.setSales(product.getSales() - item.getQuantity());
                product.setUpdateTime(LocalDateTime.now());
            }
        }
        products.forEach(productMapper::updateById);
        log.info("订单交易关闭，库存已恢复，订单ID：{}", orderId);
    }
}
