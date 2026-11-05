package com.yuyukoshop.yuyukoshop.controller;

import com.yuyukoshop.yuyukoshop.common.Result;
import com.yuyukoshop.yuyukoshop.entity.Order;
import com.yuyukoshop.yuyukoshop.exception.BusinessException;
import com.yuyukoshop.yuyukoshop.service.AlipayService;
import com.yuyukoshop.yuyukoshop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付控制器
 * 处理支付宝支付、回调通知、退款等操作
 */
@RestController
@RequestMapping("/api/payment")
public class PaymentController extends BaseController {

    @Autowired
    private AlipayService alipayService;

    @Autowired
    private OrderService orderService;

    /**
     * 发起支付宝支付
     * 前端调用此接口获取支付宝页面HTML，然后将HTML输出到页面即可跳转到支付宝收银台
     *
     * @param orderId 订单ID
     * @return 支付宝表单HTML字符串
     */
    @PostMapping("/alipay/{orderId}")
    public Result<String> pay(@PathVariable Long orderId) {
        Long userId = getCurrentUserId();

        // 查询订单并验证归属
        Order order = orderService.getOrderById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException("只有待支付订单可以支付");
        }

        // 调用支付宝下单，获取表单HTML
        String formHtml = alipayService.createPagePay(order);
        return Result.success(formHtml);
    }

    /**
     * 支付宝同步回调（用户支付完成后浏览器跳转）
     * ⚠️ 安全红线：前台同步跳转结果不可信，仅用于展示，不能作为支付成功依据
     * 实际支付结果以异步通知（notify）或主动查询（query）为准
     *
     * @param request  HTTP请求
     * @param response HTTP响应
     */
    @GetMapping("/return")
    public void returnCallback(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 同步回调仅做展示用，重定向到前端订单页面
        // 前端应通过查询接口确认订单真实支付状态
        String outTradeNo = request.getParameter("out_trade_no");
        response.sendRedirect("/order-result?orderNo=" + outTradeNo);
    }

    /**
     * 支付宝异步通知（支付宝服务器POST通知）
     * ⚠️ 安全红线：必须先验签，确保通知来自支付宝
     * ⚠️ 安全红线：未确认不重付，在未确认支付结果前不能要求用户再次付款
     *
     * @param request  HTTP请求
     * @param response HTTP响应
     */
    @PostMapping("/notify")
    public void notifyCallback(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 1. 获取支付宝POST过来的所有参数
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            StringBuilder valueStr = new StringBuilder();
            for (int i = 0; i < values.length; i++) {
                valueStr.append(i == values.length - 1 ? values[i] : values[i] + ",");
            }
            params.put(name, valueStr.toString());
        }

        // 2. 验签
        boolean signVerified = alipayService.verifyNotify(params);

        PrintWriter out = response.getWriter();
        if (signVerified) {
            // 3. 验签通过，处理业务逻辑
            String result = alipayService.handleNotify(params);
            out.write(result);
        } else {
            // 验签失败
            out.write("failure");
        }
        out.flush();
    }

    /**
     * 查询订单支付状态
     * 当未收到异步通知时，前端可主动调用此接口查询
     *
     * @param orderId 订单ID
     * @return 订单支付状态
     */
    @GetMapping("/query/{orderId}")
    public Result<Map<String, Object>> queryPayStatus(@PathVariable Long orderId) {
        Long userId = getCurrentUserId();

        Order order = orderService.getOrderById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }

        // 如果订单已支付，直接返回
        if (order.getStatus() >= 1) {
            Map<String, Object> result = new HashMap<>();
            result.put("orderNo", order.getOrderNo());
            result.put("status", order.getStatus());
            result.put("statusText", order.getStatus() == 1 ? "已支付" :
                    order.getStatus() == 2 ? "已发货" :
                            order.getStatus() == 3 ? "已完成" :
                                    order.getStatus() == -1 ? "已取消" : "未知");
            return Result.success(result);
        }

        // 向支付宝查询交易状态
        String tradeStatus = alipayService.queryTradeStatus(order.getOrderNo());
        Map<String, Object> result = new HashMap<>();
        result.put("orderNo", order.getOrderNo());
        result.put("tradeStatus", tradeStatus);

        if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
            // 支付成功，更新订单状态
            if (order.getStatus() == 0) {
                orderService.updateOrderPaySuccess(order.getId(), "");
            }
            result.put("status", 1);
            result.put("statusText", "已支付");
        } else if ("WAIT_BUYER_PAY".equals(tradeStatus)) {
            result.put("status", 0);
            result.put("statusText", "等待支付");
        } else if ("TRADE_CLOSED".equals(tradeStatus)) {
            if (order.getStatus() == 0) {
                orderService.updateOrderClosed(order.getId());
            }
            result.put("status", -1);
            result.put("statusText", "交易关闭");
        } else {
            result.put("status", order.getStatus());
            result.put("statusText", "查询失败或交易不存在");
        }

        return Result.success(result);
    }

    /**
     * 关闭交易（取消未支付的支付宝订单）
     *
     * @param orderId 订单ID
     * @return 操作结果
     */
    @PutMapping("/close/{orderId}")
    public Result<Object> closeTrade(@PathVariable Long orderId) {
        Long userId = getCurrentUserId();

        Order order = orderService.getOrderById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException("只有待支付订单可以关闭交易");
        }

        boolean success = alipayService.closeTrade(order.getOrderNo());
        if (success) {
            orderService.updateOrderClosed(orderId);
            return Result.ok("交易已关闭");
        }
        return Result.error("关闭交易失败");
    }
}
