package com.yuyukoshop.yuyukoshop.controller;

import com.yuyukoshop.yuyukoshop.common.Result;
import com.yuyukoshop.yuyukoshop.dto.CartItemDto;
import com.yuyukoshop.yuyukoshop.security.CustomUserDetails;
import com.yuyukoshop.yuyukoshop.service.CartItemService;
import com.yuyukoshop.yuyukoshop.vo.CartItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartItemController extends BaseController {

    @Autowired
    private CartItemService cartItemService;

    @GetMapping
    public Result<List<CartItemVo>> getCart() {
        Long userId = getCurrentUserId();
        List<CartItemVo> items = cartItemService.getCartItems(userId);
        return Result.success(items);
    }

    @PostMapping("/items")
    public Result<Object> addCartItem(@Valid @RequestBody CartItemDto dto) {
        Long userId = getCurrentUserId();
        cartItemService.addCartItem(userId, dto);
        return Result.ok("添加成功");
    }

    @DeleteMapping("/items/{id}")
    public Result<Object> removeCartItem(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        cartItemService.removeCartItem(userId, id);
        return Result.ok("移除成功");
    }


}
