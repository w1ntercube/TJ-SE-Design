package cn.edu.tongji.instrument.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "rental_orders")
@DiscriminatorValue("RENTAL")
public class RentalOrder extends Order {

    // Getters and Setters
    @Column(name = "product_id", nullable = false)
    private Long productId;

    // 用户实际租借开始时间或计划开始时间
    @Column(name = "rental_start", nullable = true) // 允许为NULL
    private LocalDate rentalStart;

    // 用户实际租借结束时间或计划结束时间
    @Column(name = "rental_end", nullable = true) // 允许为NULL
    private LocalDate rentalEnd;

    @Column(name = "rental_duration_days", nullable = false)
    private Integer rentalDurationDays;

    @Column(name = "deposit", nullable = false, precision = 10, scale = 2)
    private BigDecimal deposit;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;
}
