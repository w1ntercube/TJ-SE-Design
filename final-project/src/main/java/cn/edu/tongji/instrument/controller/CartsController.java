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


    // 根据用户ID查询收藏的商品
    @GetMapping("/find/{userId}")
    public ResponseEntity<List<ProductDTO>> getFavoritesByUserId(@PathVariable("userId") Long userId) {
        User user = userService.getUserById(userId);

        System.out.println("AuthController类的resetPassword方法调用了AuthService类的verifyOTP方法。");

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<ProductDTO> favoriteProducts = new ArrayList<>(cartsService.getFavoriteProductsByUser(user));

        System.out.println("AuthController类的resetPassword方法调用了cartsService类的getFavoriteProductsByUser方法。");

        List<ProductDTO> activeProducts = new ArrayList<>(); // 初始化 activeProducts
        for(ProductDTO productDTO : favoriteProducts) {
            if(productDTO.getIsActive()){
                activeProducts.add(productDTO);
            }
        }
        return ResponseEntity.ok(activeProducts);
    }
    // 改变喜欢状态
    @PutMapping("/toggle/{userId}/{productId}")
    public ResponseEntity<Boolean> toggleFavorite(
            @PathVariable("userId") Long userId,
            @PathVariable("productId") Long productId) {

        boolean isExist = cartsService.isCartsExist(userId, productId);

        System.out.println("AuthController类的toggleFavorite方法调用了cartsService类的isCartsExist方法。");

        if (isExist) {

            User user = userService.getUserById(userId);

            System.out.println("AuthController类的toggleFavorite方法调用了userService类的getUserById方法。");

            Optional<Product> optionalProduct = productService.getProductById(productId);

            System.out.println("AuthController类的toggleFavorite方法调用了productService类的getProductById方法。");

            Product product = optionalProduct.get();


            cartsService.removeFromCart(user, product);

            System.out.println("AuthController类的toggleFavorite方法调用了cartsService类的removeFromCart方法。");

            return ResponseEntity.ok(false);
        } else {
            User user = userService.getUserById(userId);

            System.out.println("AuthController类的toggleFavorite方法调用了userService类的getUserById方法。");

            Optional<Product> optionalProduct = productService.getProductById(productId);

            System.out.println("AuthController类的toggleFavorite方法调用了productService类的getProductById方法。");

            Product product = optionalProduct.get();
            // 如果商品不存在，则添加

            cartsService.addToCart(user, product);

            System.out.println("AuthController类的toggleFavorite方法调用了cartsService类的addToCart方法。");

            return ResponseEntity.ok(true);

        }
    }
    // 查看是否存在
    @GetMapping("/exists/{userId}/{productId}")
    public ResponseEntity<Boolean> checkIfCartsExists(
            @PathVariable("userId") Long userId,
            @PathVariable("productId") Long productId) {

        boolean exists = cartsService.isCartsExist(userId, productId);
        System.out.println("AuthController类的exists方法调用了cartsService类的isCartsExist方法。");

        return ResponseEntity.ok(exists);
    }

}
