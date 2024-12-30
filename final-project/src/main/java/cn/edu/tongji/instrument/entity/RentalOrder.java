package cn.edu.tongji.instrument.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
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

    @Column(name = "rental_start", nullable = false)
    private LocalDateTime rentalStart;

    @Column(name = "rental_end", nullable = false)
    private LocalDateTime rentalEnd;

    @Column(name = "rental_duration_days", nullable = false)
    private Integer rentalDurationDays;

    @Column(name = "deposit", nullable = false, precision = 10, scale = 2)
    private BigDecimal deposit;

}
