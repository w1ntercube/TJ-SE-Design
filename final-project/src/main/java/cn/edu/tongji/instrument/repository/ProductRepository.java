package cn.edu.tongji.instrument.repository;

import cn.edu.tongji.instrument.entity.Product;
import cn.edu.tongji.instrument.entity.PurchaseOrder;
import cn.edu.tongji.instrument.entity.User;
import cn.edu.tongji.instrument.entity.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>
{
    // 根据卖家查找商品
    List<Product> findBySellerId(Long sellerId);
    List<Product> findByIsActiveTrue();

    // 获取商品的售卖库存
    @Query("SELECT p.stock FROM Product p WHERE p.id = :id")
    int getStockById(@Param("id") Long id);

    // 获取商品的租借库存
    @Query("SELECT p.rentalStock FROM Product p WHERE p.id = :id")
    int getRentalStockById(@Param("id") Long id);

    // 减少购买库存
    @Modifying
    @Query("UPDATE Product p SET p.stock = p.stock - :quantity WHERE p.id = :productId AND p.stock >= :quantity")
    int decrementStock(@Param("productId") Long productId, @Param("quantity") int quantity);

    // 减少租赁库存
    @Modifying
    @Query("UPDATE Product p SET p.rentalStock = p.rentalStock - :quantity WHERE p.id = :productId AND p.rentalStock >= :quantity")
    int decrementRentalStock(@Param("productId") Long productId, @Param("quantity") int quantity);

}

