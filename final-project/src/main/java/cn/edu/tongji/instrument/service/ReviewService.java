package cn.edu.tongji.instrument.service;

import cn.edu.tongji.instrument.entity.Review;
import cn.edu.tongji.instrument.dto.ReviewDTO;
import cn.edu.tongji.instrument.entity.User;
import cn.edu.tongji.instrument.repository.ProductRepository;
import cn.edu.tongji.instrument.repository.ReviewRepository;
import cn.edu.tongji.instrument.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;

    public List<ReviewDTO> getReviewsByProductId(Long productId)  {
        List<Review> reviews = reviewRepository.findByProductId(productId);
        return reviews.stream()
                .map(review -> {
                    // 根据 userId 查询用户名
                    String username = userRepository.findById(review.getUserId())
                            .map(User::getUsername) // 提取用户名
                            .orElse("Unknown User"); // 如果找不到用户，默认用户名
                    String avatarUrl = userRepository.findById(review.getUserId())
                            .map(User::getAvatarUrl)
                            .orElse("Default-avatar");
                    return new ReviewDTO(review, username, avatarUrl); // 构造 DTO
                })
                .collect(Collectors.toList());
    }

    public ReviewDTO addReview(ReviewDTO reviewDTO) {
        // 校验 userId 和 productId 是否存在
        if (!userRepository.existsById(reviewDTO.getUserId())) {
            throw new IllegalArgumentException("用户不存在");
        }

        if (!productRepository.existsById(reviewDTO.getProductId())) {
            throw new IllegalArgumentException("商品不存在");
        }
        // 将 DTO 转换为实体
        Review review = new Review();
        review.setUserId(reviewDTO.getUserId());
        review.setProductId(reviewDTO.getProductId());
        review.setComment(reviewDTO.getComment());
        review.setRating(reviewDTO.getRating());
        review.setCreatedAt(LocalDateTime.now());

        // 保存实体到数据库
        Review savedReview = reviewRepository.save(review);
        String username = userRepository.findById(review.getUserId())
                .map(User::getUsername) // 提取用户名
                .orElse("Unknown User"); // 如果找不到用户，默认用户名
        String avatarUrl = userRepository.findById(review.getUserId())
                .map(User::getAvatarUrl)
                .orElse("Default-avatar");
        // 将实体转换回 DTO 并返回
        return new ReviewDTO(savedReview, username, avatarUrl);
    }

    public Optional<Review> findByReviewIdAndUserId(Long reviewId, Long userId) {
        return reviewRepository.findByIdAndUserId(reviewId, userId);
    }

    public void delete(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }
}
