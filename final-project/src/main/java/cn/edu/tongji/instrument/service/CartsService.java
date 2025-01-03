package cn.edu.tongji.instrument.service;


import cn.edu.tongji.instrument.dto.ProductDTO;
import cn.edu.tongji.instrument.entity.User;
import cn.edu.tongji.instrument.entity.Product;
import cn.edu.tongji.instrument.entity.Carts;
import cn.edu.tongji.instrument.repository.CartsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class CartsService {
    @Autowired
    private CartsRepository cartsRepository;

    public void addToCart(User user, Product product, int quantity) {
        // 检查购物车中是否已经有该商品
        Carts existingCart = cartsRepository.findByUserAndProduct(user, product);
        if (existingCart != null) {
            // 如果存在，则更新数量
            existingCart.setQuantity(existingCart.getQuantity() + quantity);
            cartsRepository.save(existingCart);
        } else {
            // 如果不存在，则新增购物车记录
            Carts cart = new Carts();
            cart.setUser(user);
            cart.setProduct(product);
            cart.setQuantity(quantity);
            cartsRepository.save(cart);
        }
    }
    public List<ProductDTO> getFavoriteProductsByUser(User user) {
        List<Carts> carts = cartsRepository.findByUser(user);
        return carts.stream()
                .map(cart -> new ProductDTO(cart.getProduct()))
                .collect(Collectors.toList());
    }


    // 从收藏列表中移除商品
    public boolean removeFromCart(User user, Product product) {
        Carts cart = cartsRepository.findByUserAndProduct(user, product);
        if (cart != null) {
            // 删除该记录
            cartsRepository.delete(cart);
            return true;
        }
        return false;
    }

}
