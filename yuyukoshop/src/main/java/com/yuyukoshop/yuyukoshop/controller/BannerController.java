package com.yuyukoshop.yuyukoshop.controller;

import com.yuyukoshop.yuyukoshop.common.PageResult;
import com.yuyukoshop.yuyukoshop.common.Result;
import com.yuyukoshop.yuyukoshop.entity.Banner;
import com.yuyukoshop.yuyukoshop.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 轮播图控制器
 */
@RestController
@RequestMapping("/api/banners")
public class BannerController {

    @Autowired
    private BannerService bannerService;

    /**
     * 获取启用的轮播图列表（公开接口，首页调用）
     */
    @GetMapping("/active")
    public Result<List<Banner>> getActiveBanners() {
        List<Banner> banners = bannerService.getActiveBanners();
        return Result.success(banners);
    }

    /**
     * 获取轮播图详情
     */
    @GetMapping("/{id}")
    public Result<Banner> getBannerById(@PathVariable Long id) {
        Banner banner = bannerService.getBannerById(id);
        return Result.success(banner);
    }

    // ==================== 管理员接口 ====================

    /**
     * 创建轮播图
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Object> createBanner(@RequestBody Banner banner) {
        bannerService.createBanner(banner);
        return Result.ok("创建成功");
    }

    /**
     * 更新轮播图
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Object> updateBanner(@PathVariable Long id, @RequestBody Banner banner) {
        bannerService.updateBanner(id, banner);
        return Result.ok("更新成功");
    }

    /**
     * 删除轮播图
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Object> deleteBanner(@PathVariable Long id) {
        bannerService.deleteBanner(id);
        return Result.ok("删除成功");
    }

    /**
     * 分页查询轮播图
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<PageResult<Banner>> getBanners(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Integer status) {
        return Result.success(PageResult.of(bannerService.getBannerPage(page, size, status)));
    }

    /**
     * 更新轮播图状态
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Object> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer status = body.get("status");
        bannerService.updateStatus(id, status);
        return Result.ok("更新成功");
    }
}
