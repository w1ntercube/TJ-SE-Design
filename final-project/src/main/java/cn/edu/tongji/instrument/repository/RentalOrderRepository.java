package cn.edu.tongji.instrument.repository;

import cn.edu.tongji.instrument.entity.RentalOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalOrderRepository extends JpaRepository<RentalOrder, Long> {
}
