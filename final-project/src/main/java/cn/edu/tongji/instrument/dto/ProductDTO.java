package cn.edu.tongji.instrument.dto;
import cn.edu.tongji.instrument.entity.Product;


import cn.edu.tongji.instrument.entity.Product;

import java.math.BigDecimal;

public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal rentalPrice;
    private int stock;
    private int rentalStock;
    private String imagePath;
    private Boolean isActive;
    private String sellerName;
    private Long sellerId;


    public ProductDTO() {
    }
    // 构造函数
    public ProductDTO(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.rentalPrice = product.getRentalPrice();
        this.stock = product.getStock();
        this.rentalStock = product.getRentalStock();
        this.imagePath = product.getImagePath();
        this.isActive = product.getIsActive();
        this.sellerId = product.getSellerId();
    }

    // Getter 和 Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getRentalPrice() {
        return rentalPrice;
    }
    public void setRentalPrice(BigDecimal rentalPrice) {
        this.rentalPrice = rentalPrice;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
    public int getRentalStock() {
        return rentalStock;
    }

    public void setRentalStock(int rentalStock) {
        this.rentalStock = rentalStock;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    public Boolean getIsActive() {
        return isActive;
    }
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    public String getSellerName() {
        return sellerName;
    }
    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }
    public Long getSellerId() {
        return sellerId;
    }
    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }
}

