package cn.edu.tongji.instrument.service;

import cn.edu.tongji.instrument.entity.Order;
import cn.edu.tongji.instrument.entity.PurchaseOrder;
import cn.edu.tongji.instrument.entity.RentalOrder;
import cn.edu.tongji.instrument.entity.enums.OrderStatus;
import cn.edu.tongji.instrument.repository.OrderRepository;
import cn.edu.tongji.instrument.repository.ProductRepository;
import cn.edu.tongji.instrument.repository.PurchaseOrderRepository;
import cn.edu.tongji.instrument.repository.RentalOrderRepository;
import cn.edu.tongji.instrument.util.Md5Util;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {

    @Value("${payment.create-order}")
    private String orderUrl;

    @Value("${payment.url}")
    private String paymentUrl;

    @Value("${payment.secret-key}")
    private String secretKey;

    @Value("${payment.notify-url}")
    private String notifyUrl;

    @Value("${payment.return-url}")
    private String returnUrl;

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final RentalOrderRepository rentalOrderRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;


    public PaymentService(PurchaseOrderRepository purchaseOrderRepository,
                          RentalOrderRepository rentalOrderRepository,
                          OrderRepository orderRepository,
                          ProductRepository productRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.rentalOrderRepository = rentalOrderRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    /**
     * 创建购买订单
     */
    public Long createPurchaseOrder(Long userId,
                                    Long productId,
                                    Integer quantity,
                                    BigDecimal price,
                                    String address) {
        PurchaseOrder order = new PurchaseOrder();
        order.setUserId(userId);
        order.setProductId(productId);
        order.setQuantity(quantity);
        order.setTotalPrice(price);
        order.setOrderStatus(OrderStatus.PENDING); // 待支付
        order.setAddress(address);

        purchaseOrderRepository.save(order);
        return order.getId();
    }

    /**
     * 创建租赁订单
     */
    public Long createRentalOrder(Long userId,
                                  Long productId,
                                  Integer rentalDurationDays,
                                  BigDecimal deposit,
                                  BigDecimal price,
                                  int quantity,
                                  String address) {
        RentalOrder order = new RentalOrder();
        order.setUserId(userId);
        order.setProductId(productId);
        order.setRentalDurationDays(rentalDurationDays);
        order.setRentalStart(null); // 初始为空
        order.setRentalEnd(null);   // 初始为空
        order.setDeposit(deposit);
        order.setTotalPrice(price);
        order.setQuantity(quantity);
        order.setOrderStatus(OrderStatus.PENDING); // 待支付
        order.setAddress(address); // 设置地址

        rentalOrderRepository.save(order);
        return order.getId();
    }

    /**
     * 构建支付请求的重定向 URL
     */
    public String buildPaymentUrl(Long orderId, int type, BigDecimal price, String orderType) {
        String param = orderType; // 自定义参数区分订单类型
        int isHtml = 1; // 跳转到支付页面

        String formattedPrice = String.format("%.2f", price);

        // 计算签名
        String signData = orderId.toString() + param + type + formattedPrice + secretKey;
        // System.out.println("SignData: " + signData);
        String sign = Md5Util.getMD5Code(signData);
        // System.out.println("Sign: " + sign);

        // 构建支付请求URL
        return UriComponentsBuilder.fromHttpUrl(orderUrl)
                .queryParam("payId", orderId.toString())
                .queryParam("type", String.valueOf(type))
                .queryParam("price", formattedPrice)
                .queryParam("sign", sign)
                .queryParam("param", param)
                .queryParam("notifyUrl", notifyUrl) // 异步通知地址
                .queryParam("returnUrl", returnUrl) // 同步跳转地址
                .queryParam("isHtml", isHtml)
                .toUriString();
    }

    /**
     * 校验支付回调并更新订单状态
     */
    public boolean handlePaymentCallback(String payId, String param, int type, double price, double reallyPrice, String sign) {

        // 构建签名数据
        String signData = payId + param + type + price + reallyPrice + secretKey;
        // System.out.println("Sign Data: " + signData);

        // 计算签名
        String expectedSign = Md5Util.getMD5Code(signData);
        // System.out.println("Expected Sign: " + expectedSign);
        // System.out.println("Actual Sign: " + sign);

        // 校验签名
        if (!expectedSign.equals(sign)) {
            System.out.println("Sign verification failed for payId: " + payId);
            return false; // 签名校验失败
        }

        System.out.println("Sign verification passed for payId: " + payId);

        try {
            Long orderId = Long.parseLong(payId); // 转换 payId 为 Long 类型
            if ("purchase".equalsIgnoreCase(param)) {
                // 处理购买订单
                PurchaseOrder order = purchaseOrderRepository.findById(orderId).orElse(null);
                if (order == null) {
                    System.out.println("Purchase order not found for payId: " + payId);
                    return false;
                }
                if (order.getOrderStatus() == OrderStatus.PAID) {
                    System.out.println("Purchase order already marked as PAID. Ignoring callback.");
                    return true;
                }
                order.setOrderStatus(OrderStatus.PAID);
                purchaseOrderRepository.save(order);
            } else if ("rental".equalsIgnoreCase(param)) {
                // 处理租赁订单
                RentalOrder order = rentalOrderRepository.findById(orderId).orElse(null);
                if (order == null) {
                    System.out.println("Rental order not found for payId: " + payId);
                    return false;
                }
                if (order.getOrderStatus() == OrderStatus.PAID) {
                    System.out.println("Rental order already marked as PAID. Ignoring callback.");
                    return true;
                }
                order.setOrderStatus(OrderStatus.PAID);
                rentalOrderRepository.save(order);
            } else {
                System.out.println("Unknown param value: " + param);
                return false;
            }
        } catch (NumberFormatException ex) {
            System.out.println("Invalid payId format: " + payId);
            return false;
        }

        return true; // 回调成功处理
    }

    // 调用支付平台接口查询订单状态
    public boolean queryAndUpdateOrderStatus(String orderId) {
        RestTemplate restTemplate = new RestTemplate();

        // 构造请求参数
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("orderId", orderId);

        try {
            // 发起 POST 请求
            ResponseEntity<String> response = restTemplate.postForEntity(paymentUrl, requestParams, String.class);

            // 检查响应状态
            if (response.getStatusCode().is2xxSuccessful()) {
                String responseBody = response.getBody();
                PaymentResponse paymentResponse = parseResponse(responseBody);

                if (paymentResponse == null) {
                    System.out.println("Invalid response from payment platform for orderId: " + orderId);
                    return false;
                }

                // 查找订单
                Order order = orderRepository.findById(Long.parseLong(orderId)).orElse(null);
                if (order == null) {
                    System.out.println("Order not found for orderId: " + orderId);
                    return false;
                }

                // 更新订单状态
                if (paymentResponse.getCode() == 1) {
                    // 如果订单已支付
                    if (order.getOrderStatus() != OrderStatus.PAID) {
                        order.setOrderStatus(OrderStatus.PAID);
                        orderRepository.save(order);
                        updateStockAfterPayment(order); // 更新库存
                        System.out.println("Order " + orderId + " marked as PAID.");
                    }
                } else if (paymentResponse.getCode() == -1) {
                    // 如果订单未支付并已超时
                    if (order.getOrderStatus() == OrderStatus.PENDING &&
                            order.getCreatedAt().isBefore(LocalDateTime.now().minusMinutes(5))) {
                        order.setOrderStatus(OrderStatus.CANCELLED);
                        orderRepository.save(order);
                        System.out.println("Order " + orderId + " marked as CANCELLED due to timeout.");
                    }
                }
                return true;
            } else {
                System.out.println("Failed to query payment platform for orderId: " + orderId);
                return false;
            }
        } catch (Exception e) {
            System.out.println("Error querying payment platform: " + e.getMessage());
            return false;
        }
    }

    // 更新库存
    private void updateStockAfterPayment(Order order) {
        if (order instanceof PurchaseOrder) {
            // 处理购买订单库存更新
            PurchaseOrder purchaseOrder = (PurchaseOrder) order;
            int affectedRows = productRepository.decrementStock(purchaseOrder.getProductId(), purchaseOrder.getQuantity());
            if (affectedRows == 0) {
                throw new IllegalStateException("库存不足或商品不存在，无法更新购买库存！");
            }
        } else if (order instanceof RentalOrder) {
            // 处理租赁订单库存更新
            RentalOrder rentalOrder = (RentalOrder) order;
            int affectedRows = productRepository.decrementRentalStock(rentalOrder.getProductId(), rentalOrder.getQuantity());
            if (affectedRows == 0) {
                throw new IllegalStateException("库存不足或商品不存在，无法更新租赁库存！");
            }
        } else {
            throw new IllegalArgumentException("未知的订单类型，无法更新库存！");
        }
    }

    // 解析支付平台返回的数据
    private PaymentResponse parseResponse(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(responseBody, PaymentResponse.class);
        } catch (Exception e) {
            System.out.println("Error parsing payment response: " + e.getMessage());
            return null;
        }
    }

    // 内部类：支付平台响应映射
    private static class PaymentResponse {
        private int code; // 1: 支付成功，-1: 未支付或失败
        private String msg;
        private String data;

        // Getters and Setters
        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }
}
