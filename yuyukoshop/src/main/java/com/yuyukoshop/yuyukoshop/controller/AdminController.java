package com.yuyukoshop.yuyukoshop.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yuyukoshop.yuyukoshop.common.PageResult;
import com.yuyukoshop.yuyukoshop.common.Result;
import com.yuyukoshop.yuyukoshop.entity.Badge;
import com.yuyukoshop.yuyukoshop.entity.Comment;
import com.yuyukoshop.yuyukoshop.entity.Order;
import com.yuyukoshop.yuyukoshop.entity.Post;
import com.yuyukoshop.yuyukoshop.entity.Product;
import com.yuyukoshop.yuyukoshop.entity.User;
import com.yuyukoshop.yuyukoshop.service.AdminService;
import com.yuyukoshop.yuyukoshop.service.BadgeService;
import com.yuyukoshop.yuyukoshop.service.DashboardService;
import com.yuyukoshop.yuyukoshop.vo.DashboardVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private BadgeService badgeService;

    @GetMapping("/dashboard")
    public Result<DashboardVo> getDashboard() {
        DashboardVo dashboard = dashboardService.getDashboard();
        return Result.success(dashboard);
    }

    @PostMapping("/products")
    public Result<Object> createProduct(@RequestBody Product product) {
        adminService.createProduct(product);
        return Result.ok("创建成功");
    }

    @PutMapping("/products/{id}")
    public Result<Object> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        adminService.updateProduct(id, product);
        return Result.ok("更新成功");
    }

    @GetMapping("/products")
    public Result<PageResult<Product>> getProducts(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        IPage<Product> result = adminService.getProductPage(page, size, keyword, status);
        return Result.success(PageResult.of(result));
    }

    @GetMapping("/orders")
    public Result<PageResult<Order>> getOrders(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Integer status) {
        IPage<Order> result = adminService.getOrderPage(page, size, status);
        return Result.success(PageResult.of(result));
    }

    @PutMapping("/orders/{id}/ship")
    public Result<Object> shipOrder(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String trackingNo = body.get("trackingNo");
        String shippingCompany = body.getOrDefault("shippingCompany", "");
        adminService.shipOrder(id, shippingCompany, trackingNo);
        return Result.ok("发货成功");
    }

    @PutMapping("/orders/{id}/process-refund")
    public Result<Object> processRefund(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        boolean approved = body.getOrDefault("approved", false);
        adminService.processRefund(id, approved);
        return Result.ok(approved ? "退款已通过" : "退款已拒绝");
    }

    @GetMapping("/users")
    public Result<PageResult<User>> getUsers(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String keyword) {
        IPage<User> result = adminService.getUserPage(page, size, keyword);
        return Result.success(PageResult.of(result));
    }

    @PutMapping("/users/{id}/status")
    public Result<Object> banUser(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer status = body.get("status");
        adminService.banUser(id, status);
        return Result.ok("操作成功");
    }

    @GetMapping("/forums/posts")
    public Result<PageResult<Post>> getPosts(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Integer status) {
        IPage<Post> result = adminService.getPostPage(page, size, status);
        return Result.success(PageResult.of(result));
    }

    @PostMapping("/forums/posts/batch-update")
    public Result<Object> batchUpdatePosts(@RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<Integer> rawIds = (List<Integer>) body.get("ids");
        List<Long> ids = rawIds != null ? rawIds.stream().map(Long::valueOf).collect(Collectors.toList()) : null;
        Integer isEssence = (Integer) body.get("isEssence");
        Integer isTop = (Integer) body.get("isTop");
        Integer status = (Integer) body.get("status");
        adminService.batchUpdatePosts(ids, isEssence, isTop, status);
        return Result.ok("操作成功");
    }

    @GetMapping("/comments")
    public Result<PageResult<Comment>> getComments(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Integer status) {
        IPage<Comment> result = adminService.getCommentPage(page, size, status);
        return Result.success(PageResult.of(result));
    }

    @PutMapping("/comments/{id}/status")
    public Result<Object> updateCommentStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer status = body.get("status");
        adminService.updateCommentStatus(id, status);
        return Result.ok("操作成功");
    }

    // ========== 勋章管理 ==========

    @GetMapping("/badges")
    public Result<List<Badge>> getBadges() {
        List<Badge> badges = badgeService.getAllBadges();
        return Result.success(badges);
    }

    @PostMapping("/badges")
    public Result<Object> createBadge(@RequestBody Badge badge) {
        badgeService.createBadge(badge);
        return Result.ok("创建成功");
    }

    @PutMapping("/badges/{id}")
    public Result<Object> updateBadge(@PathVariable Long id, @RequestBody Badge badge) {
        badgeService.updateBadge(id, badge);
        return Result.ok("更新成功");
    }

    @DeleteMapping("/badges/{id}")
    public Result<Object> deleteBadge(@PathVariable Long id) {
        badgeService.deleteBadge(id);
        return Result.ok("删除成功");
    }

    // ========== 收款码设置 ==========

    @PostMapping("/payment-qr")
    public Result<Object> uploadPaymentQr(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) return Result.error(400, "文件不能为空");
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/"))
            return Result.error(400, "只允许上传图片");
        try {
            Path dir = Paths.get("uploads/images");
            Files.createDirectories(dir);
            Path target = dir.resolve("payment_qr.png");
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return Result.ok("收款码已更新");
        } catch (IOException e) {
            return Result.error("上传失败");
        }
    }
}
