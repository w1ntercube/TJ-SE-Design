package cn.edu.tongji.instrument.repository;

import cn.edu.tongji.instrument.entity.Product;
import cn.edu.tongji.instrument.entity.Carts;
import cn.edu.tongji.instrument.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface CartsRepository extends JpaRepository<Carts, Long> {
    Carts findByUserAndProduct(User user, Product product);
    List<Carts> findByUser(User user);
}
