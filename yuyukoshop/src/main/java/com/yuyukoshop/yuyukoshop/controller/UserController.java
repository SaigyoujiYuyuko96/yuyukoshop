package com.yuyukoshop.yuyukoshop.controller;

import com.yuyukoshop.yuyukoshop.common.Result;
import com.yuyukoshop.yuyukoshop.dto.UserProfileDto;
import com.yuyukoshop.yuyukoshop.entity.User;
import com.yuyukoshop.yuyukoshop.security.CustomUserDetails;
import com.yuyukoshop.yuyukoshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public Result<User> getProfile() {
        User user = userService.getCurrentUser();
        user.setPassword(null);
        return Result.success(user);
    }

    @PutMapping("/profile")
    public Result<Object> updateProfile(@Valid @RequestBody UserProfileDto dto) {
        User user = userService.getCurrentUser();
        userService.updateProfile(user.getId(), dto);
        return Result.ok("更新成功");
    }

    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        user.setPassword(null);
        user.setEmail(null);
        user.setPhone(null);
        return Result.success(user);
    }
}
