package cn.edu.tongji.instrument.demo.paymentdemo.controller;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

@Controller("paymentDemoController")
@RequestMapping("/payment-demo") // 添加基础路径前缀
@ConditionalOnProperty(name = "demo.enabled", havingValue = "true") // 只有在配置文件中设置 demo.enabled=true 时才加载
public class PaymentController {

    private static final String PAYMENT_URL = "https://2218466.pay.lanjingzf.com/createOrder";
    private static final String SECRET_KEY = "db28171e41c0d68e51e1ce5e26f6962f";

    // 首页，选择支付方式和输入金额
    @GetMapping("/")
    public String index() {
        return "index";
    }

    // 提交支付请求
    @PostMapping("/pay")
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
        String signData = payId + param + type + price + SECRET_KEY;
        String sign = md5(signData);

        // 构建支付请求参数
        Map<String, String> params = new HashMap<>();
        params.put("payId", payId);
        params.put("type", String.valueOf(type));
        params.put("price", price.toString());
        params.put("sign", sign);
        params.put("param", param);
        params.put("isHtml", String.valueOf(isHtml));

        // 构建支付请求URL
        String requestUrl = UriComponentsBuilder.fromHttpUrl(PAYMENT_URL)
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
        String signData = payId + param + type + price + reallyPrice + SECRET_KEY;
        String expectedSign = md5(signData);

        if (!expectedSign.equals(sign)) {
            return "FAIL: Invalid signature";
        }

        // 处理订单逻辑（例如更新订单状态）
        // TODO: 在此处理订单完成逻辑
        return "SUCCESS";
    }

    // 工具方法: MD5加密
    private String md5(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(data.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : array) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}