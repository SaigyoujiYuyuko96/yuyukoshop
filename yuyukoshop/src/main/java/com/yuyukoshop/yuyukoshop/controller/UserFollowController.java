package com.yuyukoshop.yuyukoshop.controller;

import com.yuyukoshop.yuyukoshop.common.Result;
import com.yuyukoshop.yuyukoshop.security.CustomUserDetails;
import com.yuyukoshop.yuyukoshop.service.UserFollowService;
import com.yuyukoshop.yuyukoshop.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserFollowController extends BaseController {

    @Autowired
    private UserFollowService userFollowService;

    @PostMapping("/follow/{userId}")
    public Result<Object> follow(@PathVariable Long userId) {
        Long currentUserId = getCurrentUserId();
        userFollowService.follow(currentUserId, userId);
        return Result.ok("关注成功");
    }

    @DeleteMapping("/follow/{userId}")
    public Result<Object> unfollow(@PathVariable Long userId) {
        Long currentUserId = getCurrentUserId();
        userFollowService.unfollow(currentUserId, userId);
        return Result.ok("取消关注成功");
    }

    @GetMapping("/following")
    public Result<List<UserVo>> getFollowingList() {
        Long currentUserId = getCurrentUserId();
        List<UserVo> list = userFollowService.getFollowingList(currentUserId);
        return Result.success(list);
    }

    @GetMapping("/followers")
    public Result<List<UserVo>> getFollowerList() {
        Long currentUserId = getCurrentUserId();
        List<UserVo> list = userFollowService.getFollowerList(currentUserId);
        return Result.success(list);
    }

    @GetMapping("/follow/count")
    public Result<Map<String, Long>> getFollowCount() {
        Long currentUserId = getCurrentUserId();
        Long followingCount = userFollowService.getFollowingCount(currentUserId);
        Long followerCount = userFollowService.getFollowerCount(currentUserId);
        return Result.success(Map.of(
                "following", followingCount,
                "followers", followerCount
        ));
    }

    @GetMapping("/is-following/{userId}")
    public Result<Boolean> isFollowing(@PathVariable Long userId) {
        Long currentUserId = getCurrentUserId();
        boolean result = userFollowService.isFollowing(currentUserId, userId);
        return Result.success(result);
    }
}
