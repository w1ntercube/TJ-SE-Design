package cn.edu.tongji.instrument.repository;

import cn.edu.tongji.instrument.entity.PurchaseOrder;
import cn.edu.tongji.instrument.entity.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    List<PurchaseOrder> findByUserId(Long userId);
    List<PurchaseOrder> findByUserIdAndOrderStatus(Long userId, OrderStatus orderStatus);

    List<PurchaseOrder> findByProductIdIn(List<Long> productIds);

    List<PurchaseOrder> findByProductIdInAndOrderStatus(List<Long> productIds, OrderStatus orderStatus);
}
