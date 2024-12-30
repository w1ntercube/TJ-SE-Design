package cn.edu.tongji.instrument.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rental_orders")
@DiscriminatorValue("RENTAL")
public class RentalOrder extends Order {

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

    // Getters and Setters
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public LocalDateTime getRentalStart() {
        return rentalStart;
    }

    public void setRentalStart(LocalDateTime rentalStart) {
        this.rentalStart = rentalStart;
    }

    public LocalDateTime getRentalEnd() {
        return rentalEnd;
    }

    public void setRentalEnd(LocalDateTime rentalEnd) {
        this.rentalEnd = rentalEnd;
    }

    public Integer getRentalDurationDays() {
        return rentalDurationDays;
    }

    public void setRentalDurationDays(Integer rentalDurationDays) {
        this.rentalDurationDays = rentalDurationDays;
    }

    public BigDecimal getDeposit() {
        return deposit;
    }

    public void setDeposit(BigDecimal deposit) {
        this.deposit = deposit;
    }
}
