package com.example.final_project.repository;

import com.example.final_project.entity.Product;
import com.example.final_project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>
{
    // 根据卖家查找商品
    List<Product> findBySeller(User seller);

}

