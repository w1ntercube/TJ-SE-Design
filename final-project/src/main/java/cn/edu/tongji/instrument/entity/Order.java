package cn.edu.tongji.instrument.entity;

import cn.edu.tongji.instrument.entity.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Setter;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "orders")
@Inheritance(strategy = InheritanceType.JOINED) // JOINED 策略
@DiscriminatorColumn(name = "order_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Order {

    // Getters and Setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Setter
    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Setter
    @Column(name = "address", nullable = false, length = 255)
    private String address; // 新增字段：订单地址

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}
