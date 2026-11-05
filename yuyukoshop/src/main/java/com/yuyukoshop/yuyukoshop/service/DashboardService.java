package com.yuyukoshop.yuyukoshop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yuyukoshop.yuyukoshop.entity.*;
import com.yuyukoshop.yuyukoshop.mapper.*;
import com.yuyukoshop.yuyukoshop.vo.DashboardVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private PostService postService;

    public DashboardVo getDashboard() {
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        // 总用户数
        Long totalUsers = userMapper.selectCount(null);

        // 总商品数（上架的）
        Long totalProducts = productMapper.selectCount(
                new LambdaQueryWrapper<Product>().eq(Product::getStatus, 1)
        );

        // 总订单数
        Long totalOrders = orderMapper.selectCount(null);

        // 今日订单数
        Long todayOrders = orderMapper.selectCount(
                new LambdaQueryWrapper<Order>()
                        .ge(Order::getCreateTime, todayStart)
                        .le(Order::getCreateTime, todayEnd)
        );

        // 总销售额（已支付订单）
        BigDecimal totalSales = calculateTotalSales();

        // 今日销售额
        BigDecimal todaySales = calculateTodaySales(todayStart);

        // 待处理订单数（已支付待发货）
        Long pendingOrders = orderMapper.selectCount(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getStatus, 1)
        );

        // 待处理退款数
        Long pendingRefunds = orderMapper.selectCount(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getStatus, 4)
        );

        // 总帖子数
        Long totalPosts = postMapper.selectCount(
                new LambdaQueryWrapper<Post>().eq(Post::getStatus, 1)
        );

        // 今日帖子数
        Long totalPostsToday = postMapper.selectCount(
                new LambdaQueryWrapper<Post>()
                        .eq(Post::getStatus, 1)
                        .ge(Post::getCreateTime, todayStart)
                        .le(Post::getCreateTime, todayEnd)
        );

        // 最近10条订单
        List<Order> recentOrders = orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                        .orderByDesc(Order::getCreateTime)
                        .last("LIMIT 10")
        );

        List<Map<String, Object>> recentOrderList = recentOrders.stream()
                .map(order -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", order.getId());
                    map.put("orderNo", order.getOrderNo());
                    map.put("totalAmount", order.getTotalAmount());
                    map.put("status", order.getStatus());
                    map.put("createTime", order.getCreateTime());
                    return map;
                })
                .collect(Collectors.toList());

        // 最近7天销售趋势
        List<Map<String, Object>> salesTrend = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime dayStart = LocalDateTime.of(date, LocalTime.MIN);

            BigDecimal daySales = calculateDaySales(dayStart);

            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", date.toString());
            dayData.put("sales", daySales);
            salesTrend.add(dayData);
        }

        return DashboardVo.builder()
                .totalUsers(totalUsers)
                .totalProducts(totalProducts)
                .totalOrders(totalOrders)
                .todayOrders(todayOrders)
                .totalSales(totalSales != null ? totalSales : BigDecimal.ZERO)
                .todaySales(todaySales != null ? todaySales : BigDecimal.ZERO)
                .pendingOrders(pendingOrders)
                .pendingRefunds(pendingRefunds)
                .totalPosts(totalPosts)
                .totalPostsToday(totalPostsToday)
                .recentOrders(recentOrderList)
                .salesTrend(salesTrend)
                .build();
    }

    private BigDecimal calculateTotalSales() {
        List<Order> paidOrders = orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getStatus, 2) // 已完成
                        .or()
                        .eq(Order::getStatus, 3) // 已发货
                        .or()
                        .eq(Order::getStatus, 1) // 已支付未发货
        );

        return paidOrders.stream()
                .map(Order::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateTodaySales(LocalDateTime dayStart) {
        LocalDateTime dayEnd = dayStart.with(LocalTime.MAX);

        List<Order> paidOrders = orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                        .ge(Order::getCreateTime, dayStart)
                        .le(Order::getCreateTime, dayEnd)
                        .in(Order::getStatus, Arrays.asList(1, 2, 3))
        );

        return paidOrders.stream()
                .map(Order::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateDaySales(LocalDateTime dayStart) {
        LocalDateTime dayEnd = dayStart.with(LocalTime.MAX);

        List<Order> paidOrders = orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                        .ge(Order::getCreateTime, dayStart)
                        .le(Order::getCreateTime, dayEnd)
                        .in(Order::getStatus, Arrays.asList(1, 2, 3))
        );

        return paidOrders.stream()
                .map(Order::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
