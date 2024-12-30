package cn.edu.tongji.instrument.controller;

import cn.edu.tongji.instrument.repository.OrderRepository;
import cn.edu.tongji.instrument.repository.PurchaseOrderRepository;
import cn.edu.tongji.instrument.repository.RentalOrderRepository;

import cn.edu.tongji.instrument.service.PaymentService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;


@Controller
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
    public String createPurchaseOrder(
            @RequestParam("userId") Long userId, // 用户ID
            @RequestParam("productId") Long productId, // 商品ID
            @RequestParam("quantity") Integer  quantity, // 商品数量
            @RequestParam("type") int type, // 支付方式: 微信1/支付宝2
            @RequestParam("price") BigDecimal price, // 订单金额
            Model model
    ) {

        // 调用 Service 层创建购买订单
        Long orderId = paymentService.createPurchaseOrder(userId, productId, quantity, price);

        // 构建支付页面 URL 并返回重定向
        return paymentService.redirectToPayment(orderId, type, price, "PURCHASE");


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
        return paymentService.redirectToPayment(orderId, type, price, "RENTAL");
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

        // 调用 Service 层校验签名并更新订单状态
        boolean result = paymentService.handlePaymentCallback(payId, param, type, price, reallyPrice, sign);

        return result ? "SUCCESS" : "FAIL";
    }


}