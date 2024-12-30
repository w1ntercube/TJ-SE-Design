package cn.edu.tongji.instrument.controller;

import cn.edu.tongji.instrument.dto.ReviewDTO;
import cn.edu.tongji.instrument.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @PostMapping
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
}
