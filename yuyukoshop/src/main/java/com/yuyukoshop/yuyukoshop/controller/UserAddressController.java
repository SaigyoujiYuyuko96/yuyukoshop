package com.yuyukoshop.yuyukoshop.controller;

import com.yuyukoshop.yuyukoshop.common.Result;
import com.yuyukoshop.yuyukoshop.dto.UserAddressDto;
import com.yuyukoshop.yuyukoshop.entity.UserAddress;
import com.yuyukoshop.yuyukoshop.security.CustomUserDetails;
import com.yuyukoshop.yuyukoshop.service.UserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/users/addresses")
public class UserAddressController {

    @Autowired
    private UserAddressService userAddressService;

    @GetMapping
    public Result<List<UserAddress>> getAddresses() {
        Long userId = getCurrentUserId();
        List<UserAddress> addresses = userAddressService.getAddresses(userId);
        return Result.success(addresses);
    }

    @PostMapping
    public Result<Object> addAddress(@Valid @RequestBody UserAddressDto dto) {
        Long userId = getCurrentUserId();
        userAddressService.addAddress(userId, dto);
        return Result.ok("添加成功");
    }

    @PutMapping("/{id}")
    public Result<Object> updateAddress(@PathVariable Long id, @Valid @RequestBody UserAddressDto dto) {
        Long userId = getCurrentUserId();
        userAddressService.updateAddress(userId, id, dto);
        return Result.ok("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<Object> deleteAddress(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        userAddressService.deleteAddress(userId, id);
        return Result.ok("删除成功");
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getId();
    }
}
