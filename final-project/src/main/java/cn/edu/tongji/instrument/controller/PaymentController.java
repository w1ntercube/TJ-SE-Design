package cn.edu.tongji.instrument.controller;

import cn.edu.tongji.instrument.util.Md5Util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PaymentController {

    @Value("${payment.url}")
    private String paymentUrl;

    @Value("${payment.secret-key}")
    private String secretKey;

    // 提交支付请求
    @PostMapping("/api/pay")
    public String createOrder(
            @RequestParam("type") int type, // 支付方式: 微信1/支付宝2
            @RequestParam("price") BigDecimal price, // 订单金额
            Model model
    ) {
        // 生成商户订单号
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
        return "redirect:" + requestUrl;
    }

    // 回调接口
    @GetMapping("/callback")
    @ResponseBody
    public String handleCallback(
            @RequestParam("payId") String payId,
            @RequestParam("param") String param,
            @RequestParam("type") int type,
            @RequestParam("price") BigDecimal price,
            @RequestParam("reallyPrice") BigDecimal reallyPrice,
            @RequestParam("sign") String sign
    ) {
        // 校验签名
        String signData = payId + param + type + price + reallyPrice + secretKey;
        String expectedSign = Md5Util.md5(signData);

        if (!expectedSign.equals(sign)) {
            return "FAIL: Invalid signature";
        }

        // 处理订单逻辑（例如更新订单状态）
        // TODO: 在此处理订单完成逻辑
        return "SUCCESS";
    }

}