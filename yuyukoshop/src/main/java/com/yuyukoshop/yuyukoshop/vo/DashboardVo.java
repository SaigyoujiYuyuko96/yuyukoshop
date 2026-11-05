package com.yuyukoshop.yuyukoshop.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardVo {
    private Long totalUsers;
    private Long totalProducts;
    private Long totalOrders;
    private Long todayOrders;
    private BigDecimal totalSales;
    private BigDecimal todaySales;
    private Long pendingOrders;
    private Long pendingRefunds;
    private Long totalPosts;
    private Long totalPostsToday;
    private List<Map<String, Object>> recentOrders;
    private List<Map<String, Object>> salesTrend;
}
