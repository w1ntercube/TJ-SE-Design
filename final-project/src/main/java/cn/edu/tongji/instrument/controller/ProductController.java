package cn.edu.tongji.instrument.controller;

import cn.edu.tongji.instrument.entity.Product;
import cn.edu.tongji.instrument.entity.User;
import cn.edu.tongji.instrument.service.UserService;
import cn.edu.tongji.instrument.service.ProductService;
import cn.edu.tongji.instrument.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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



    // 获取所有商品
    @GetMapping("/all")
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    // 根据商品ID获取商品
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        return product.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 添加商品
    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        Product savedProduct = productService.addProduct(product);
        return ResponseEntity.ok(savedProduct);
    }

    // 更新商品
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        product.setId(id);
        Product updatedProduct = productService.updateProduct(product);
        return ResponseEntity.ok(updatedProduct);
    }

    // 下架商品
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        try {
            // 查找商品
            Optional<Product> productOptional = productService.getProductById(id);
            if (!productOptional.isPresent()) {
                return ResponseEntity.status(404).body("Product not found");
            }

            Product product = productOptional.get();

            // 删除商品图片
            String imagePath = product.getImagePath();
            System.out.println("准备删除的图片路径："+imagePath);
            if (imagePath != null && !imagePath.isEmpty()) {
                FileUploadUtil.deleteFile(imagePath);  // 删除图片文件
            }

            // 删除商品记录
            productService.deleteProduct(id);

            return ResponseEntity.ok("Product deleted successfully");
        }

        catch (IOException e) {

            return ResponseEntity.status(500).body("Error deleting product: " + e.getMessage());
        }
    }



    // 上传商品（包括商品图片）
    @PostMapping("/upload")
    public ResponseEntity<String> uploadProductWithImage(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") double price,
            @RequestParam("stock") int stock,
            @RequestParam("seller_name") String sellerName,
            @RequestParam("image") MultipartFile imageFile) {

        try {
            // 获取卖家信息，假设通过卖家名称查找卖家ID
            User seller = userService.findByUsername(sellerName);
            if (seller == null) {
                return ResponseEntity.status(400).body("Seller not found");
            }

            // 生成商品ID（使用时间戳或UUID）
            long productId = System.currentTimeMillis();  // 使用时间戳生成唯一商品ID

            // 处理图片上传
            String imageFileName = FileUploadUtil.saveFile(imageFile, productId, "uploads/images");  // 传入目录路径

            // 将时间戳转换为 LocalDateTime
            LocalDateTime createdAt = LocalDateTime.ofEpochSecond(productId / 1000, 0, ZoneOffset.UTC);

            // 创建商品对象
            Product product = new Product();
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setStock(stock);
            product.setSeller(seller);  // 设置卖家
            product.setIsActive(true);  // 默认商品激活
            product.setImagePath(imageFileName);  // 保存图片路径
            product.setCreatedAt(createdAt);  // 设置创建时间

            // 保存商品到数据库
            Product savedProduct = productService.addProduct(product);

            return ResponseEntity.ok("Product uploaded successfully with ID: " + savedProduct.getId());
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error uploading image: " + e.getMessage());
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
        List<Product> products = productService.getProductsBySeller(seller);
        return ResponseEntity.ok(products);  // 返回商品列表
    }
}


