package cn.edu.tongji.instrument.service;


import cn.edu.tongji.instrument.dto.ProductDTO;
import cn.edu.tongji.instrument.entity.User;
import cn.edu.tongji.instrument.entity.Product;
import cn.edu.tongji.instrument.entity.Carts;
import cn.edu.tongji.instrument.repository.CartsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class CartsService {

    @Autowired
    private CartsRepository cartsRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    public void addToCart(User user, Product product) {
        // 检查购物车中是否已经有该商品
        Carts existingCart = cartsRepository.findByUserAndProduct(user, product);
        if (existingCart == null) {
            // 如果不存在，则新增购物车记录
            Carts cart = new Carts();
            cart.setUser(user);
            cart.setProduct(product);
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

    public boolean isCartsExist(Long userId, Long productId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return false; // 用户不存在
        }
        // 验证商品是否存在
        Optional<Product> optionalProduct = productService.getProductById(productId);
        if (optionalProduct.isEmpty()) {
            return false; // 商品不存在
        }

        Product product = optionalProduct.get();
        Carts result = cartsRepository.findByUserAndProduct(user, product);
        // 验证商品是否在用户的收藏列表中


        return result != null;
    }

}
