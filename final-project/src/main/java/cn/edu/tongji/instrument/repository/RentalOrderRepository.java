package cn.edu.tongji.instrument.repository;

import cn.edu.tongji.instrument.entity.RentalOrder;
import cn.edu.tongji.instrument.entity.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentalOrderRepository extends JpaRepository<RentalOrder, Long> {
    // 查询某用户的租赁订单
    List<RentalOrder> findByUserId(Long userId);

    // 查询某用户的特定状态的租赁订单
    List<RentalOrder> findByUserIdAndOrderStatus(Long userId, OrderStatus orderStatus);

    List<RentalOrder> findByProductIdIn(List<Long> productIds);
    List<RentalOrder> findByProductIdInAndOrderStatus(List<Long> productIds, OrderStatus orderStatus);

}