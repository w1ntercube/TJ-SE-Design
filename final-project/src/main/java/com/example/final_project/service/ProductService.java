package com.example.final_project.service;

import com.example.final_project.entity.Product;
import com.example.final_project.entity.User;
import com.example.final_project.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // 获取所有商品
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // 根据ID获取商品
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    // 添加商品
    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    // 更新商品
    public Product updateProduct(Product product) {
        return productRepository.save(product);
    }

    // 删除商品
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }


    //通过卖家找产品
    public List<Product> getProductsBySeller(User seller) {
        return productRepository.findBySeller(seller);
    }
}


