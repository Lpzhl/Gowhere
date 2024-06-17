package com.atguigu.controller;



import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.payment.page.models.AlipayTradePagePayResponse;

import com.atguigu.dao.PaymentOrder;

import com.atguigu.pojo.Bookings;

import com.atguigu.service.BookingsService;
import com.atguigu.service.TicketBookingsService;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/alipay")
public class AliPayController {

    @Autowired
    private BookingsService bookingsService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Autowired
    private TicketBookingsService ticketBookingsService;


    @GetMapping("/pay")
    public String pay(PaymentOrder paymentOrder, @RequestParam String orderType) {
        AlipayTradePagePayResponse response;
        try {
            // 设置return_url
            String returnUrl = "http://localhost:8088/alipay/payment-success";

            // 获取编码后的订单 ID
            String orderTypePrefix = orderType.equals("hotel") ? "HOTEL_" : "FLIGHT_";
            String encodedOrderId = paymentOrder.getEncodedOrderId(orderTypePrefix);

            // 发起API调用（以创建当面付收款二维码为例）
            response = Factory.Payment.Page()
                    .pay(paymentOrder.getOrderName(), encodedOrderId, String.valueOf(paymentOrder.getTotalPrice()), returnUrl);
        } catch (Exception e) {
            System.err.println("调用遭遇异常，原因：" + e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }
        return response.getBody();
    }


    @PostMapping("/notify")  // 注意这里必须是POST接口
    public String payNotify(HttpServletRequest request) throws Exception {
        if (request.getParameter("trade_status").equals("TRADE_SUCCESS")) {
            System.out.println("=========支付宝异步回调========");

            Map<String, String> params = new HashMap<>();
            Map<String, String[]> requestParams = request.getParameterMap();
            System.out.println("requestParams = " + requestParams);
            for (String name : requestParams.keySet()) {
                params.put(name, request.getParameter(name));
                System.out.println(name + " = " + request.getParameter(name));
            }
            String outTradeNo = params.get("out_trade_no");

            String[] parts = outTradeNo.split("_");
            String type = parts[0];
            System.out.println("type = " + type);
            String bookingId = parts[1];
            String name = params.get("subject");
            String price = params.get("total_amount");

            // 支付宝验签
            if (Factory.Payment.Common().verifyNotify(params)) {
                // 验签通过
                System.out.println("交易名称: " + params.get("subject"));
                System.out.println("交易状态: " + params.get("trade_status"));
                System.out.println("支付宝交易凭证号: " + params.get("trade_no"));
                System.out.println("商户订单号: " + params.get("out_trade_no"));
                System.out.println("交易金额: " + params.get("total_amount"));
                System.out.println("买家在支付宝唯一id: " + params.get("buyer_id"));
                System.out.println("买家付款时间: " + params.get("gmt_payment"));
                System.out.println("买家付款金额: " + params.get("buyer_pay_amount"));

                // 这里只是处理酒店订单 ，如果我还要处理机票订单我该怎么办

                if(type.equals("HOTEL")){
                    // 更新订单未已支付
                    bookingsService.updateBookingStatus(Integer.parseInt(bookingId), "待评价");
                    // 从Redis移除相关的过期时间数据
                    stringRedisTemplate.opsForZSet().remove("bookings", bookingId);
                    // 发送WebSocket消息
                    //String message = "支付成功，订单号：" + bookingId;
                    //myWebSocketHandler.sendMessage(message);
              /*  // 发布支付成功消息到 Redis
                stringRedisTemplate.convertAndSend("paymentSuccess", "支付成功，订单号：" + bookingId);*/
                }else if(type.equals("FLIGHT")){
                    ticketBookingsService.updateStatus(Integer.parseInt(bookingId), "待评价");

                    // 从Redis移除相关的过期时间数据
                    stringRedisTemplate.opsForZSet().remove("airBookings", bookingId);
                }

            }
        }
        return "success";
    }
    @GetMapping("/payment-success")
    public String paymentSuccess(@RequestParam Map<String, String> params) {

        System.out.println("支付成功页面被调用");

        // 从params中提取所需的参数
        String outTradeNo = params.get("out_trade_no");
        String tradeNo = params.get("trade_no");
        String totalAmount = params.get("total_amount");
        String[] parts = outTradeNo.split("_");
        String type = parts[0];
        // 根据订单号的前缀确定返回的URL
        String returnUrl = "http://localhost:5173/#/";
        if (outTradeNo != null) {
            if (type.equals("HOTEL")) {
                returnUrl += "Hote";
            } else if (type.equals("FLIGHT")) {
                returnUrl += "AirTicket";
            }
        }

        // 构建HTML响应
        return "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "<title>支付成功</title>"
                + "<style>"
                + "  body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f7f7f7; text-align: center; margin: 0; padding: 50px 0; }"
                + "  .container { background-color: #ffffff; width: 100%; max-width: 400px; margin: auto; padding: 40px; border-radius: 10px; box-shadow: 0 4px 8px rgba(0,0,0,0.15); }"
                + "  h1 { color: black; margin-bottom: 25px; }"
                + "  p { color: black; font-size: 16px; line-height: 1.6; }"
                + "  .label { color: black; margin-bottom: 5px; font-weight: bold; }"
                + "  .value { color:black; }"
                + "  .footer { margin-top: 30px; }"
                + "  .footer a { color: #27ae60; text-decoration: none; font-weight: bold; }"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<div class='container'>"
                + "<h1>支付成功！</h1>"
                + "<p class='label'>订单号</p>"
                + "<p class='value'>" + parts[1] + "</p>"
                + "<p class='label'>交易号</p>"
                + "<p class='value'>" + tradeNo + "</p>"
                + "<p class='label'>交易金额</p>"
                + "<p class='value'>" + totalAmount + " 元</p>"
                + "<div class='footer'>"
                + "<a href='" + returnUrl + "'>返回首页</a>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";
    }


}
