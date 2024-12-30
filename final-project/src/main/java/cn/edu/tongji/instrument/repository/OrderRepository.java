package cn.edu.tongji.instrument.repository;

import cn.edu.tongji.instrument.entity.Order;
import cn.edu.tongji.instrument.entity.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // 根据用户ID查询订单
    List<Order> findByUserId(Long userId);

}
