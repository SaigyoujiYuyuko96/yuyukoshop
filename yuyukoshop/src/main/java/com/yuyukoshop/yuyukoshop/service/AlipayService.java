package com.yuyukoshop.yuyukoshop.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.yuyukoshop.yuyukoshop.config.AlipayConfig;
import com.yuyukoshop.yuyukoshop.entity.Order;
import com.yuyukoshop.yuyukoshop.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 支付宝支付核心服务
 * 涵盖：电脑网站支付、交易查询、退款、关闭交易
 */
@Slf4j
@Service
public class AlipayService {

    @Autowired
    private AlipayClient alipayClient;

    @Autowired
    private AlipayConfig alipayConfig;

    @Autowired
    private OrderService orderService;

    /**
     * 创建电脑网站支付请求
     * 调用 alipay.trade.page.pay 接口，返回自动提交的HTML表单
     *
     * @param order 订单信息
     * @return 自动提交的HTML表单字符串，前端直接输出即可跳转到支付宝收银台
     */
    public String createPagePay(Order order) {
        try {
            AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();

            // 设置回调地址
            request.setReturnUrl(alipayConfig.getReturnUrl());
            request.setNotifyUrl(alipayConfig.getNotifyUrl());

            // 构建业务参数
            request.setBizContent("{" +
                    "\"out_trade_no\":\"" + order.getOrderNo() + "\"," +
                    "\"total_amount\":\"" + order.getTotalAmount().toPlainString() + "\"," +
                    "\"subject\":\"" + "幽幽子商城订单-" + order.getOrderNo() + "\"," +
                    "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"," +
                    "\"timeout_express\":\"30m\"" +
                    "}");

            // 调用SDK生成表单HTML
            AlipayTradePagePayResponse response = alipayClient.pageExecute(request, "POST");
            if (response.isSuccess()) {
                log.info("支付宝下单成功，订单号：{}", order.getOrderNo());
                return response.getBody();
            } else {
                log.error("支付宝下单失败，订单号：{}，错误码：{}，错误信息：{}",
                        order.getOrderNo(), response.getCode(), response.getMsg());
                throw new BusinessException("支付宝下单失败：" + response.getMsg());
            }
        } catch (AlipayApiException e) {
            log.error("支付宝下单异常，订单号：{}", order.getOrderNo(), e);
            throw new BusinessException("支付宝下单异常：" + e.getErrMsg());
        }
    }

    /**
     * 验证支付宝异步通知签名
     * ⚠️ 安全红线：收到异步通知后必须先验签，确保通知来自支付宝
     *
     * @param params 异步通知的所有参数
     * @return 验签是否通过
     */
    public boolean verifyNotify(Map<String, String> params) {
        try {
            boolean signVerified = AlipaySignature.rsaCheckV1(
                    params,
                    alipayConfig.getAlipayPublicKey(),
                    alipayConfig.getCharset(),
                    alipayConfig.getSignType()
            );
            if (!signVerified) {
                log.warn("支付宝异步通知验签失败，参数：{}", params);
            }
            return signVerified;
        } catch (AlipayApiException e) {
            log.error("支付宝异步通知验签异常", e);
            return false;
        }
    }

    /**
     * 处理支付宝异步通知
     * ⚠️ 安全红线：前台支付结果不可信，必须以异步通知或查询接口获取结果为准
     *
     * @param params 异步通知参数
     * @return 处理结果（"success"或"failure"）
     */
    public String handleNotify(Map<String, String> params) {
        String outTradeNo = params.get("out_trade_no");
        String tradeStatus = params.get("trade_status");
        String tradeNo = params.get("trade_no");
        String totalAmount = params.get("total_amount");

        log.info("收到支付宝异步通知：outTradeNo={}, tradeStatus={}, tradeNo={}, totalAmount={}",
                outTradeNo, tradeStatus, tradeNo, totalAmount);

        try {
            // 根据商户订单号查询订单
            Order order = orderService.getOrderByOrderNo(outTradeNo);
            if (order == null) {
                log.warn("异步通知中的订单不存在：{}", outTradeNo);
                return "failure";
            }

            // 二次校验：核对金额是否一致
            if (!order.getTotalAmount().toPlainString().equals(totalAmount)) {
                log.warn("异步通知金额不一致，订单金额：{}，通知金额：{}",
                        order.getTotalAmount(), totalAmount);
                return "failure";
            }

            // 根据交易状态处理业务
            switch (tradeStatus) {
                case "TRADE_SUCCESS":
                case "TRADE_FINISHED":
                    // 支付成功，更新订单状态
                    if (order.getStatus() == 0) {
                        orderService.updateOrderPaySuccess(order.getId(), tradeNo);
                        log.info("订单支付成功处理完成，订单号：{}", outTradeNo);
                    }
                    return "success";

                case "WAIT_BUYER_PAY":
                    log.info("订单等待买家付款，订单号：{}", outTradeNo);
                    return "success";

                case "TRADE_CLOSED":
                    // 交易关闭（超时未支付或全额退款）
                    if (order.getStatus() == 0) {
                        orderService.updateOrderClosed(order.getId());
                        log.info("订单交易关闭，订单号：{}", outTradeNo);
                    }
                    return "success";

                default:
                    log.warn("未知的交易状态：{}，订单号：{}", tradeStatus, outTradeNo);
                    return "failure";
            }
        } catch (Exception e) {
            log.error("处理支付宝异步通知异常，订单号：{}", outTradeNo, e);
            return "failure";
        }
    }

    /**
     * 查询交易状态
     * 调用 alipay.trade.query 接口
     *
     * @param orderNo 商户订单号
     * @return 交易状态（WAIT_BUYER_PAY / TRADE_SUCCESS / TRADE_FINISHED / TRADE_CLOSED）
     */
    public String queryTradeStatus(String orderNo) {
        try {
            AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
            request.setBizContent("{\"out_trade_no\":\"" + orderNo + "\"}");

            AlipayTradeQueryResponse response = alipayClient.execute(request);
            if (response.isSuccess()) {
                String tradeStatus = response.getTradeStatus();
                log.info("查询交易状态成功，订单号：{}，状态：{}", orderNo, tradeStatus);
                return tradeStatus;
            } else {
                log.error("查询交易状态失败，订单号：{}，错误码：{}，错误信息：{}",
                        orderNo, response.getCode(), response.getMsg());
                return null;
            }
        } catch (AlipayApiException e) {
            log.error("查询交易状态异常，订单号：{}", orderNo, e);
            return null;
        }
    }

    /**
     * 发起退款
     * 调用 alipay.trade.refund 接口
     *
     * @param orderNo      商户订单号
     * @param refundAmount 退款金额
     * @param refundReason 退款原因
     * @return 退款是否成功
     */
    public boolean refund(String orderNo, BigDecimal refundAmount, String refundReason) {
        try {
            AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
            request.setBizContent("{" +
                    "\"out_trade_no\":\"" + orderNo + "\"," +
                    "\"refund_amount\":\"" + refundAmount.toPlainString() + "\"," +
                    "\"refund_reason\":\"" + refundReason + "\"" +
                    "}");

            AlipayTradeRefundResponse response = alipayClient.execute(request);
            if (response.isSuccess()) {
                log.info("退款成功，订单号：{}，退款金额：{}", orderNo, refundAmount);
                return true;
            } else {
                log.error("退款失败，订单号：{}，错误码：{}，错误信息：{}",
                        orderNo, response.getCode(), response.getMsg());
                return false;
            }
        } catch (AlipayApiException e) {
            log.error("退款异常，订单号：{}", orderNo, e);
            return false;
        }
    }

    /**
     * 关闭交易
     * 调用 alipay.trade.close 接口，用于关闭未支付的订单
     *
     * @param orderNo 商户订单号
     * @return 关闭是否成功
     */
    public boolean closeTrade(String orderNo) {
        try {
            AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
            request.setBizContent("{\"out_trade_no\":\"" + orderNo + "\"}");

            AlipayTradeCloseResponse response = alipayClient.execute(request);
            if (response.isSuccess()) {
                log.info("关闭交易成功，订单号：{}", orderNo);
                return true;
            } else {
                log.error("关闭交易失败，订单号：{}，错误码：{}，错误信息：{}",
                        orderNo, response.getCode(), response.getMsg());
                return false;
            }
        } catch (AlipayApiException e) {
            log.error("关闭交易异常，订单号：{}", orderNo, e);
            return false;
        }
    }
}
