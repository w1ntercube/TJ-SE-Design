package cn.edu.tongji.instrument.service;

import cn.edu.tongji.instrument.entity.Review;
import cn.edu.tongji.instrument.dto.ReviewDTO;
import cn.edu.tongji.instrument.entity.User;
import cn.edu.tongji.instrument.repository.ReviewRepository;
import cn.edu.tongji.instrument.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private UserRepository userRepository;

    public List<ReviewDTO> getReviewsByProductId(Long productId)  {
        List<Review> reviews = reviewRepository.findByProductId(productId);
        return reviews.stream()
                .map(review -> {
                    // 根据 userId 查询用户名
                    String username = userRepository.findById(review.getUserId())
                            .map(User::getUsername) // 提取用户名
                            .orElse("Unknown User"); // 如果找不到用户，默认用户名
                    return new ReviewDTO(review, username); // 构造 DTO
                })
                .collect(Collectors.toList());
    }

}
