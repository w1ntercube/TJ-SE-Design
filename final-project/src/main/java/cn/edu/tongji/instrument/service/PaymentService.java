package cn.edu.tongji.instrument.service;

import cn.edu.tongji.instrument.entity.PurchaseOrder;
import cn.edu.tongji.instrument.entity.RentalOrder;
import cn.edu.tongji.instrument.entity.enums.OrderStatus;
import cn.edu.tongji.instrument.repository.PurchaseOrderRepository;
import cn.edu.tongji.instrument.repository.RentalOrderRepository;
import cn.edu.tongji.instrument.util.Md5Util;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PaymentService {

    @Value("${payment.create-order}")
    private String orderUrl;

    @Value("${payment.url}")
    private String paymentUrl;

    @Value("${payment.secret-key}")
    private String secretKey;



    private final PurchaseOrderRepository purchaseOrderRepository;
    private final RentalOrderRepository rentalOrderRepository;

    public PaymentService(PurchaseOrderRepository purchaseOrderRepository, RentalOrderRepository rentalOrderRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.rentalOrderRepository = rentalOrderRepository;
    }

    /**
     * 创建购买订单
     */
    public Long createPurchaseOrder(Long userId, Long productId, Integer quantity, BigDecimal price) {
        PurchaseOrder order = new PurchaseOrder();
        order.setUserId(userId);
        order.setProductId(productId);
        order.setQuantity(quantity);
        order.setTotalPrice(price);
        order.setOrderStatus(OrderStatus.PENDING); // 待支付
        purchaseOrderRepository.save(order);
        return order.getId();
    }

    /**
     * 创建租赁订单
     */
    public Long createRentalOrder(Long userId, Long productId, String rentalStart, String rentalEnd, BigDecimal deposit, BigDecimal price) {
        RentalOrder order = new RentalOrder();
        order.setUserId(userId);
        order.setProductId(productId);
        order.setRentalStart(LocalDateTime.parse(rentalStart));
        order.setRentalEnd(LocalDateTime.parse(rentalEnd));
        order.setRentalDurationDays((int) (order.getRentalEnd().toLocalDate().toEpochDay() - order.getRentalStart().toLocalDate().toEpochDay()));
        order.setDeposit(deposit);
        order.setTotalPrice(price);
        order.setOrderStatus(OrderStatus.PENDING); // 待支付
        rentalOrderRepository.save(order);
        return order.getId();
    }

    /**
     * 构建支付请求的重定向 URL
     */
    public String redirectToPayment(Long orderId, int type, BigDecimal price, String orderType) {
        String param = orderType; // 自定义参数区分订单类型
        int isHtml = 1; // 跳转到支付页面

        // 计算签名
        String signData = orderId + param + type + price + secretKey;
        String sign = Md5Util.md5(signData);

        // 构建测试用的完整字符串
        String testCalcString = String.format(
                "payId=%d&param=%s&type=%d&price=%.2f&reallyPrice=%.2f&sign=%s",
                orderId, param, type, price, price, sign
        );

        // 打印测试用字符串
        System.out.println("Test calc String: " + testCalcString);


        // 构建支付请求URL
        String requestUrl = UriComponentsBuilder.fromHttpUrl(orderUrl)
                .queryParam("payId", orderId.toString()) // 订单ID
                .queryParam("type", String.valueOf(type)) // 支付方式
                .queryParam("price", price.toString()) // 支付金额
                .queryParam("sign", sign) // 签名
                .queryParam("param", param) // 自定义参数
                .queryParam("isHtml", isHtml) // 是否跳转支付页面
                .toUriString();

        // 返回重定向 URL
        return "redirect:" + requestUrl;
    }

    /**
     * 校验支付回调并更新订单状态
     */
    public boolean handlePaymentCallback(Long payId, String param, int type, BigDecimal price, BigDecimal reallyPrice, String sign) {

        // 构建测试用的完整字符串
        String testCallbackString = String.format(
                "payId=%d&param=%s&type=%d&price=%.2f&reallyPrice=%.2f&sign=%s",
                payId, param, type, price, reallyPrice, sign
        );

        // 打印测试用字符串
        System.out.println("Test Callback String: " + testCallbackString);

        // 校验签名
        String signData = payId + param + type + price + secretKey;
        String expectedSign = Md5Util.md5(signData);
        System.out.println("Expected Sign: " + expectedSign);

        if (!expectedSign.equals(sign)) {
            return false; // 签名校验失败
        }

        // 更新订单状态
        if ("PURCHASE".equals(param)) {
            PurchaseOrder order = purchaseOrderRepository.findById(payId).orElse(null);
            if (order == null) return false;
            order.setOrderStatus(OrderStatus.PAID);
            purchaseOrderRepository.save(order);
        } else if ("RENTAL".equals(param)) {
            RentalOrder order = rentalOrderRepository.findById(payId).orElse(null);
            if (order == null) return false;
            order.setOrderStatus(OrderStatus.PAID);
            rentalOrderRepository.save(order);
        }else{
            return false;
        }

        return true;
    }
}
