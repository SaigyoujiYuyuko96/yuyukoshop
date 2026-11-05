package com.yuyukoshop.yuyukoshop.controller;

import com.yuyukoshop.yuyukoshop.common.Result;
import com.yuyukoshop.yuyukoshop.entity.Badge;
import com.yuyukoshop.yuyukoshop.service.BadgeService;
import com.yuyukoshop.yuyukoshop.vo.BadgeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/badges")
public class BadgeController extends BaseController {

    @Autowired
    private BadgeService badgeService;

    @GetMapping("/wall")
    public Result<List<BadgeVo>> getBadgeWall() {
        Long userId = getCurrentUserId();
        List<BadgeVo> badges = badgeService.getUserBadgeWall(userId);
        return Result.success(badges);
    }

    @GetMapping("/user/{userId}")
    public Result<List<BadgeVo>> getUserBadges(@PathVariable Long userId) {
        List<BadgeVo> badges = badgeService.getMyBadges(userId);
        return Result.success(badges);
    }

    @GetMapping("/my")
    public Result<List<BadgeVo>> getMyBadges() {
        Long userId = getCurrentUserId();
        List<BadgeVo> badges = badgeService.getMyBadges(userId);
        return Result.success(badges);
    }
}
