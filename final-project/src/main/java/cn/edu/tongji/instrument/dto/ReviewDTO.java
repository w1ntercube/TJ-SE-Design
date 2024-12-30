package cn.edu.tongji.instrument.dto;

import cn.edu.tongji.instrument.entity.Review;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReviewDTO {

    private Long id;
    private Long userId;
    private Long productId;
    private int rating;
    private String comment;
    private String createdAt;

    private String username;
    private String avatarUrl;

    // 定义日期格式化器
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 构造函数：从 Review 实体中提取数据
    public ReviewDTO(Review review, String username, String avatarUrl) {
        this.id = review.getId();
        this.userId = review.getUserId();
        this.productId = review.getProductId();
        this.rating = review.getRating();
        this.comment = review.getComment();
        this.createdAt = review.getCreatedAt() != null ? review.getCreatedAt().format(formatter) : null;
        this.username = username;
        this.avatarUrl = avatarUrl;
    }
    public ReviewDTO(Review review) {
        this.id = review.getId();
        this.userId = review.getUserId();
        this.productId = review.getProductId();
        this.rating = review.getRating();
        this.comment = review.getComment();
        this.createdAt = review.getCreatedAt().toString();
    }
    public ReviewDTO() {}

    // Getters 和 Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
    public String getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getavatarUrl() {
        return avatarUrl;
    }
    public void setavatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
