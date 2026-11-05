package com.yuyukoshop.yuyukoshop.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yuyukoshop.yuyukoshop.common.PageResult;
import com.yuyukoshop.yuyukoshop.common.Result;
import com.yuyukoshop.yuyukoshop.dto.OrderCreateDto;
import com.yuyukoshop.yuyukoshop.dto.RefundRequestDto;
import com.yuyukoshop.yuyukoshop.entity.Order;
import com.yuyukoshop.yuyukoshop.security.CustomUserDetails;
import com.yuyukoshop.yuyukoshop.service.OrderService;
import com.yuyukoshop.yuyukoshop.vo.OrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/orders")
public class OrderController extends BaseController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public Result<Order> createOrder(@Valid @RequestBody OrderCreateDto dto) {
        Long userId = getCurrentUserId();
        Order order = orderService.createOrder(userId, dto);
        return Result.success(order);
    }

    @GetMapping
    public Result<PageResult<Order>> getOrders(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size) {
        Long userId = getCurrentUserId();
        IPage<Order> result = orderService.getUserOrders(userId, page, size);
        return Result.success(PageResult.of(result));
    }

    @GetMapping("/{id}")
    public Result<OrderVo> getOrderDetail(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        OrderVo orderVo = orderService.getOrderDetail(userId, id);
        return Result.success(orderVo);
    }

    @PutMapping("/{id}/cancel")
    public Result<Object> cancelOrder(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        orderService.cancelOrder(userId, id);
        return Result.ok("取消成功");
    }

    @PostMapping("/{id}/refund")
    public Result<Object> requestRefund(@PathVariable Long id, @Valid @RequestBody RefundRequestDto dto) {
        Long userId = getCurrentUserId();
        orderService.requestRefund(userId, id, dto);
        return Result.ok("退款申请已提交");
    }


}
