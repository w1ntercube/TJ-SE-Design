package cn.edu.tongji.instrument.controller;

import cn.edu.tongji.instrument.repository.OrderRepository;
import cn.edu.tongji.instrument.repository.PurchaseOrderRepository;
import cn.edu.tongji.instrument.repository.RentalOrderRepository;

import cn.edu.tongji.instrument.service.PaymentService;
import lombok.Getter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Getter
    private final OrderRepository orderRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final RentalOrderRepository rentalOrderRepository;
    private final PaymentService paymentService;

    public PaymentController(OrderRepository orderRepository,
                             PurchaseOrderRepository purchaseOrderRepository,
                             RentalOrderRepository rentalOrderRepository,
                             PaymentService paymentService)
    {
        this.orderRepository = orderRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.rentalOrderRepository = rentalOrderRepository;
        this.paymentService = paymentService;
    }

    // 提交支付请求（购买订单）
    @PostMapping("/purchase")
    public Map<String, String> createPurchaseOrder(
            @RequestParam("userId") Long userId,
            @RequestParam("productId") Long productId,
            @RequestParam("quantity") Integer quantity,
            @RequestParam("type") int type,
            @RequestParam("price") BigDecimal price
    ) {

        // 调用 Service 层创建购买订单
        Long orderId = paymentService.createPurchaseOrder(userId, productId, quantity, price);

        // 构建支付页面 URL
        String redirectUrl = paymentService.buildPaymentUrl(orderId, type, price, "PURCHASE");

        System.out.println(redirectUrl);

        // 返回订单信息和支付跳转链接
        Map<String, String> response = new HashMap<>();
        response.put("orderId", orderId.toString());
        response.put("type", type == 1? "微信支付" : "支付宝支付");
        response.put("price", price.toString());
        response.put("redirectUrl", redirectUrl);
        return response;

    }

    @PostMapping("/rental")
    public String createRentalOrder(
            @RequestParam("userId") Long userId,
            @RequestParam("productId") Long productId,
            @RequestParam("rentalStart") String rentalStart, // 格式: yyyy-MM-ddTHH:mm:ss
            @RequestParam("rentalEnd") String rentalEnd,
            @RequestParam("deposit") BigDecimal deposit,
            @RequestParam("price") BigDecimal price,
            @RequestParam("type") int type, // 支付方式: 微信1/支付宝2
            Model model
    ) {
        // 调用 Service 层创建租赁订单
        Long orderId = paymentService.createRentalOrder(userId, productId, rentalStart, rentalEnd, deposit, price);

        // 构建支付页面 URL 并返回重定向
        return paymentService.buildPaymentUrl(orderId, type, price, "RENTAL");
    }

    // 回调接口
    @GetMapping("/callback")
    public String callback(String payId,
                           String param,
                           int type,
                           double price,
                           double reallyPrice,
                           String sign){
        try {
            System.out.println("收到支付回调payId: " + payId);
            System.out.println("收到支付回调param: " + param);
            System.out.println("收到支付回调type: " + type);
            System.out.println("收到支付回调price: " + price);
            System.out.println("收到支付回调reallyPrice: " + reallyPrice);
            System.out.println("收到支付回调sign: " + sign);

            // 调用 Service 校验签名并更新订单状态
            boolean result = paymentService.handlePaymentCallback(payId, param, type, price, reallyPrice, sign);

            // 返回处理结果
            if (result) {
                System.out.println("支付成功，订单号：" + payId);
                return "success";
            } else {
                System.out.println("支付失败，订单号：" + payId);
                return "fail";
            }
        } catch (Exception ex) {
            System.err.println("回调处理异常：" + ex.getMessage());
            ex.printStackTrace();
            return "fail";
        }
    }

    // 查询订单状态并更新
    @GetMapping("/queryStatus")
    public Map<String, String> queryOrderStatus(@RequestParam("orderId") Long orderId) {
        boolean result = paymentService.queryAndUpdateOrderStatus(orderId);

        Map<String, String> response = new HashMap<>();
        if (result) {
            response.put("status", "success");
            response.put("message", "Order status updated successfully.");
        } else {
            response.put("status", "fail");
            response.put("message", "Failed to update order status.");
        }
        return response;
    }
}