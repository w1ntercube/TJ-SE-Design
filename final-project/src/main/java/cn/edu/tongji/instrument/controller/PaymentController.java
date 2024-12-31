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
       /* // 生成商户订单号
        String payId = String.valueOf(System.currentTimeMillis());
        String param = "customParam"; // 可选参数
        int isHtml = 1; // 跳转到支付页面

        // 计算签名
        String signData = payId + param + type + price + secretKey;
        String sign = Md5Util.md5(signData);

        // 构建支付请求参数
        Map<String, String> params = new HashMap<>();
        params.put("payId", payId);
        params.put("type", String.valueOf(type));
        params.put("price", price.toString());
        params.put("sign", sign);
        params.put("param", param);
        params.put("isHtml", String.valueOf(isHtml));

        // 构建支付请求URL
        String requestUrl = UriComponentsBuilder.fromHttpUrl(paymentUrl)
                .queryParam("payId", params.get("payId"))
                .queryParam("type", params.get("type"))
                .queryParam("price", params.get("price"))
                .queryParam("sign", params.get("sign"))
                .queryParam("param", params.get("param"))
                .queryParam("isHtml", params.get("isHtml"))
                .toUriString();

        // 重定向到支付页面
        return "redirect:" + requestUrl;*/
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
    @ResponseBody
    public String handleCallback(
            @RequestParam("payId") Long payId,
            @RequestParam("param") String param,
            @RequestParam("type") int type,
            @RequestParam("price") BigDecimal price,
            @RequestParam("reallyPrice") BigDecimal reallyPrice,
            @RequestParam("sign") String sign
    ) {
        try {
            System.out.println("收到支付回调，参数如下：");
            System.out.println("payId: " + payId);
            System.out.println("param: " + param);
            System.out.println("type: " + type);
            System.out.println("price: " + price);
            System.out.println("reallyPrice: " + reallyPrice);
            System.out.println("sign: " + sign);

            // 调用 Service 校验签名并更新订单状态
            boolean result = paymentService.handlePaymentCallback(payId, param, type, price, reallyPrice, sign);

            // 返回处理结果
            if (result) {
                System.out.println("支付成功，订单号：" + payId);
                return "SUCCESS";
            } else {
                System.out.println("支付失败，订单号：" + payId);
                return "FAIL";
            }
        } catch (Exception ex) {
            System.err.println("回调处理异常：" + ex.getMessage());
            ex.printStackTrace();
            return "FAIL";
        }
    }


}