package com.yuyukoshop.yuyukoshop.controller;

import com.yuyukoshop.yuyukoshop.common.Result;
import com.yuyukoshop.yuyukoshop.security.CustomUserDetails;
import com.yuyukoshop.yuyukoshop.service.UserFavoriteService;
import com.yuyukoshop.yuyukoshop.vo.FavoriteVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/favorites/products")
public class UserFavoriteController {

    @Autowired
    private UserFavoriteService userFavoriteService;

    @GetMapping
    public Result<List<FavoriteVo>> getFavorites() {
        Long userId = getCurrentUserId();
        List<FavoriteVo> favorites = userFavoriteService.getFavorites(userId);
        return Result.success(favorites);
    }

    @PostMapping("/{productId}")
    public Result<Object> addFavorite(@PathVariable Long productId) {
        Long userId = getCurrentUserId();
        userFavoriteService.addFavorite(userId, productId);
        return Result.ok("收藏成功");
    }

    @DeleteMapping("/{productId}")
    public Result<Object> removeFavorite(@PathVariable Long productId) {
        Long userId = getCurrentUserId();
        userFavoriteService.removeFavorite(userId, productId);
        return Result.ok("取消收藏成功");
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getId();
    }
}
