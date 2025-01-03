package cn.edu.tongji.instrument.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "products")
public class Product {

    // Getters and Setters
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false, length = 100)
    private String name;

    @Setter
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Setter
    @Column(nullable = false)
    private Double price;

    @Setter
    @Column(nullable = false)
    private Integer stock;

    @Setter
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal rentalPrice; // 租借单价

    @Setter
    @Column(nullable = false)
    private Integer rentalStock; // 租借库存量

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) //禁止懒加载
    private User seller;

    @Setter
    @Column(nullable = false)
    private Boolean isActive;

    @Setter
    @Column(nullable = true, length = 255)
    private String imagePath;  // 商品图片的路径

    @Setter
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // 无参构造方法
    public Product() {}

    // 带参构造方法
    public Product(String name, String description, Double price, Integer stock, BigDecimal rentalPrice, Integer rentalStock, User seller, Boolean isActive, String imagePath, LocalDateTime createdAt) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.rentalPrice = rentalPrice;
        this.rentalStock = rentalStock;
        this.seller = seller;
        this.isActive = isActive;
        this.imagePath = imagePath;
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id != null && id.equals(product.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // 自动填充创建时间（如果需要）
    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}



