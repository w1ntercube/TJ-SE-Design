package cn.edu.tongji.instrument.entity.enums;

public enum OrderStatus {
    PENDING,           // 待支付
    PAID,              // 已支付
    SHIPPED,           // 已发货
    DELIVERED,         // 已收货
    CANCELLED,         // 已取消
    RETURNED,          // 已回货（适用于租赁商品）
    MERCHANT_CONFIRMED // 商家确认（适用于租赁商品）
}
