package cn.edu.tongji.instrument.controller;

import cn.edu.tongji.instrument.dto.ProductDTO;
import cn.edu.tongji.instrument.entity.Product;
import cn.edu.tongji.instrument.entity.User;
import cn.edu.tongji.instrument.service.UserService;
import cn.edu.tongji.instrument.service.ProductService;
import cn.edu.tongji.instrument.util.FileUploadUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;  // 用于通过卖家名称获取卖家


    // 获取所有商品及对应卖家信息
    @GetMapping("/all-with-sellers")
    public ResponseEntity<List<Map<String, Object>>> getAllProductsWithSellers() {
        List<Product> products = productService.getSeenProducts();
        List<Map<String, Object>> result = products.stream().map(product -> {
            User seller = userService.getUserById(product.getSellerId());
            return Map.of(
                    "product", product,
                    "seller", seller
            );
        }).toList();

        return ResponseEntity.ok(result);
    }


    // 根据商品ID获取卖家信息
    @GetMapping("/getSeller/{id}")
    public ResponseEntity<User> getUserByProductId(@PathVariable Long id) {
        // 查询商品
        Optional<Product> productOptional = productService.getProductById(id);

        // 如果商品不存在，返回 404
        if (productOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // 获取商品对象
        Product product = productOptional.get();

        // 通过商品的 sellerId 获取卖家信息
        User seller = userService.getUserById(product.getSellerId());

        // 如果卖家信息不存在，返回 404
        if (seller == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // 返回卖家信息
        return ResponseEntity.ok(seller);
    }


    // 根据商品id获取商品信息
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        Optional<Product> productOptional = productService.getProductById(id);
        if (productOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(productOptional.get());
    }

    // 下架商品
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        // 查找商品
        Optional<Product> productOptional = productService.getProductById(id);
        if (productOptional.isEmpty()) {
            return ResponseEntity.status(404).body("Product not found");
        }

        Product product = productOptional.get();

        product.setIsActive(false); // 增加库存
        productService.updateProduct(product);

        return ResponseEntity.ok("Product deleted successfully");
    }



    // 上传商品（包括商品图片）
    @PostMapping("/addProduct")
    public ResponseEntity<ProductDTO> uploadProductWithImage(
            HttpServletRequest request,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "price") BigDecimal price,
            @RequestParam(value = "rental_price") BigDecimal rentalPrice,
            @RequestParam(value = "stock") int stock,
            @RequestParam(value = "rental_stock") int rentalStock,
            @RequestParam(value = "description") String description,
            @RequestParam(value = "seller_id") Long sellerId,
            @RequestParam(value = "is_active") Boolean isActive,
            @RequestParam(value = "file", required = false) MultipartFile file) {

        System.out.println("Content-Type: " + request.getContentType());
        System.out.println("Name: " + name);
        System.out.println("Price: " + price);
        System.out.println("File: " + (file != null ? file.getOriginalFilename() : "No file uploaded"));

        ProductDTO productDTO = new ProductDTO();
        productDTO.setName(name);
        productDTO.setPrice(price);
        productDTO.setRentalPrice(rentalPrice);
        productDTO.setStock(stock);
        productDTO.setRentalStock(rentalStock);
        productDTO.setDescription(description);
        productDTO.setSellerId(sellerId);
        productDTO.setIsActive(isActive);

        // 校验逻辑
        if (productDTO.getName() == null || productDTO.getName().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        try {
            ProductDTO savedProduct = productService.addProduct(productDTO, file);
            return ResponseEntity.ok(savedProduct);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }



    // 根据卖家用户名查询商品
    @PostMapping("/by-seller")
    public ResponseEntity<List<Product>> getProductsBySellerName(@RequestBody Map<String, String> request) {
        // 从请求体中提取卖家名称
        String sellerName = request.get("sellerName");

        // 查找卖家
        User seller = userService.findByUsername(sellerName);
        if (seller == null) {
            return ResponseEntity.status(404).body(null);  // 如果找不到卖家，返回404
        }

        // 查找该卖家的所有商品
        List<Product> products = productService.getProductsBySeller(seller.getId());
        List<Product> activeProducts = new ArrayList<>(); // 初始化 activeProducts
        for (Product product : products) {
            if (product.getIsActive()) {
                activeProducts.add(product);
            }
        }
        return ResponseEntity.ok(activeProducts);  // 返回商品列表
    }
    // 调整出售库存
    @PostMapping("/adjustSellStock")
    public ResponseEntity<String> adjustSellStock(@RequestBody Map<String, Object> request) {
        Long productId = ((Number) request.get("productId")).longValue();
        Integer quantity = ((Number) request.get("quantity")).intValue();

        Optional<Product> productOptional = productService.getProductById(productId);
        if (productOptional.isEmpty()) {
            return ResponseEntity.status(404).body("Product not found");
        }

        Product product = productOptional.get();
        int currentStock = product.getStock();
        // 判断库存减少量是否大于当前库存
        if (quantity + currentStock < 0) {
            return ResponseEntity.status(400).body("Insufficient stock to reduce by " + quantity * -1);
        }

        product.setStock(product.getStock() + quantity); // 增加库存
        productService.updateProduct(product);

        return ResponseEntity.ok("Stock updated successfully");
    }
    // 减少库存
    @PostMapping("/adjustRentStock")
    public ResponseEntity<String> adjustRentStock(@RequestBody Map<String, Object> request) {
        Long productId = ((Number) request.get("productId")).longValue();
        Integer quantity = ((Number) request.get("quantity")).intValue();

        Optional<Product> productOptional = productService.getProductById(productId);
        if (productOptional.isEmpty()) {
            return ResponseEntity.status(404).body("Product not found");
        }

        Product product = productOptional.get();
        int currentStock = product.getRentalStock();
        // 判断库存减少量是否大于当前库存
        if (quantity + currentStock < 0) {
            return ResponseEntity.status(400).body("Insufficient stock to reduce by " + quantity * -1);
        }

        // 调整库存
        product.setRentalStock(currentStock + quantity);
        productService.updateProduct(product);

        return ResponseEntity.ok("Stock updated successfully");
    }
}


