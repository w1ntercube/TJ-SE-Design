package cn.edu.tongji.instrument.controller;

import cn.edu.tongji.instrument.dto.ProductDTO;
import cn.edu.tongji.instrument.entity.Carts;
import cn.edu.tongji.instrument.entity.User;
import cn.edu.tongji.instrument.entity.Product;
import cn.edu.tongji.instrument.service.CartsService;
import cn.edu.tongji.instrument.service.UserService;
import cn.edu.tongji.instrument.service.ProductService;

import cn.edu.tongji.instrument.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/carts")
public class CartsController {

    @Autowired
    private CartsService cartsService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    // 添加商品到购物车
    @PostMapping
    public ResponseEntity<String> addCart(
            @RequestParam("userId") Long userId,
            @RequestParam("productId") Long productId,
            @RequestParam(value = "quantity", defaultValue = "1") int quantity) {

        // 验证用户是否存在
        User user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("用户不存在");
        }

        // 验证商品是否存在
        Optional<Product> optionalProduct = productService.getProductById(productId);
        if (optionalProduct.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("商品不存在");
        }
        // 提取 Optional 中的实际 Product 对象
        Product product = optionalProduct.get();
        // 添加商品到购物车
        cartsService.addToCart(user, product, quantity);

        return ResponseEntity.ok("商品已成功添加到购物车");
    }
    // 根据用户ID查询收藏的商品
    @GetMapping("/{userId}")
    public ResponseEntity<List<ProductDTO>> getFavoritesByUserId(@PathVariable("userId") Long userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<ProductDTO> favoriteProducts = new ArrayList<>(cartsService.getFavoriteProductsByUser(user));

        return ResponseEntity.ok(favoriteProducts);
    }




    // 移除收藏的商品
    @DeleteMapping("/{userId}/{productId}")
    public ResponseEntity<String> removeFromFavorites(
            @PathVariable("userId") Long userId,
            @PathVariable("productId") Long productId) {

        // 验证用户是否存在
        User user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("用户不存在");
        }

        // 验证商品是否存在
        Optional<Product> optionalProduct = productService.getProductById(productId);
        if (optionalProduct.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("商品不存在");
        }

        Product product = optionalProduct.get();
        boolean removed = cartsService.removeFromCart(user, product);
        if (removed) {
            return ResponseEntity.ok("商品已成功从收藏移除");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("移除失败，商品可能不在收藏列表中");
        }
    }
}
