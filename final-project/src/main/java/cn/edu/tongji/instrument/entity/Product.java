package cn.edu.tongji.instrument.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price; // 售卖单价

    @Column(nullable = false)
    private Integer stock; // 售卖库存

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal rentalPrice; // 租借单价

    @Column(nullable = false)
    private Integer rentalStock; // 租借库存

    @Column(name = "seller_id", nullable = false)
    private Long sellerId; // 卖家 ID

    @Column(nullable = false)
    private Boolean isActive = true; // 商品是否有效，默认为 true

    @Column(nullable = true, length = 255)
    private String imagePath; // 商品图片路径

    @Column(nullable = false)
    private LocalDateTime createdAt; // 创建时间

    // 无参构造方法
    public Product() {}

    // 带参构造方法
    public Product(String name, String description, BigDecimal price, Integer stock, BigDecimal rentalPrice, Integer rentalStock, Long sellerId, Boolean isActive, String imagePath) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.rentalPrice = rentalPrice;
        this.rentalStock = rentalStock;
        this.sellerId = sellerId;
        this.isActive = isActive;
        this.imagePath = imagePath;
        this.createdAt = LocalDateTime.now();
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

    // 自动填充创建时间
    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
