package cn.edu.tongji.instrument.controller;

import cn.edu.tongji.instrument.dto.ReviewDTO;
import cn.edu.tongji.instrument.entity.Review;
import cn.edu.tongji.instrument.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByProductId(@PathVariable Long productId) {
        List<ReviewDTO> reviews = reviewService.getReviewsByProductId(productId);
        if (reviews.isEmpty()) {
            return ResponseEntity.notFound().build(); // 如果列表为空，返回 404
        }
        return ResponseEntity.ok(reviews); // 返回 ReviewDTO 列表
    }
    @PostMapping("/add")
    public ResponseEntity<ReviewDTO> addReview(@RequestBody ReviewDTO reviewDTO) {
        // 校验数据
        if (reviewDTO.getComment() == null || reviewDTO.getComment().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        // 调用服务层保存评论
        ReviewDTO savedReview = reviewService.addReview(reviewDTO);

        // 返回保存成功的评论数据
        return ResponseEntity.ok(savedReview);
    }
    // 删除评论
    @PostMapping("/delete")
    public ResponseEntity<String> deleteReview(@RequestBody Map<String, Object> requestBody) {
        // 校验请求参数
        if (!requestBody.containsKey("userId") || !requestBody.containsKey("reviewId")) {
            return ResponseEntity.badRequest().body("Missing userId or reviewId");
        }

        Long userId = ((Number) requestBody.get("userId")).longValue();
        Long reviewId = ((Number) requestBody.get("reviewId")).longValue();

        // 查找评论
        Optional<Review> reviewOptional = reviewService.findByReviewIdAndUserId(reviewId, userId);
        if (reviewOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Review not found or not owned by user");
        }

        // 删除评论
        reviewService.delete(reviewId);

        // 返回删除成功消息
        return ResponseEntity.ok("Review deleted successfully");
    }

    //返回所有评论信息
    @GetMapping("/all")
    public ResponseEntity<List<ReviewDTO>> getAllReviews() {
        List<ReviewDTO> allReviews = reviewService.getAllReviews();
        if (allReviews.isEmpty()) {
            return ResponseEntity.noContent().build(); // 如果没有评论，返回 204 No Content
        }
        return ResponseEntity.ok(allReviews); // 返回所有评论
    }

    //管理员删除评论
    @DeleteMapping("/AdminDelete/{reviewId}")
    public ResponseEntity<String> deleteReviewById(@PathVariable Long reviewId) {
        // 删除评论
        reviewService.delete(reviewId);
        // 返回删除成功消息
        return ResponseEntity.ok("Review deleted successfully");
    }

}
