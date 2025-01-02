package cn.edu.tongji.instrument.repository;

import cn.edu.tongji.instrument.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {


    List<Review> findByProductId(Long productId);

    Optional<Review> findByIdAndUserId(Long id, Long userId);
}
