package cn.edu.tongji.instrument.service;

import cn.edu.tongji.instrument.dto.ProductDTO;
import cn.edu.tongji.instrument.entity.Product;
import cn.edu.tongji.instrument.entity.User;
import cn.edu.tongji.instrument.repository.ProductRepository;
import cn.edu.tongji.instrument.repository.UserRepository;
import cn.edu.tongji.instrument.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    // 获取所有商品
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // 获取所有可以展示的商品
    public List<Product> getSeenProducts() {
        return productRepository.findByIsActiveTrue();
    }

    // 根据ID获取商品
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    // 添加商品
    public ProductDTO addProduct(ProductDTO productDTO, MultipartFile file) throws IOException {
        // 获取卖家信息
        System.out.println(productDTO.getName());
        System.out.println(productDTO.getPrice());
        System.out.println(productDTO.getRentalPrice());
        System.out.println(productDTO.getDescription());
        System.out.println(productDTO.getStock());
        User seller = userService.getUserById(productDTO.getSellerId());
        if (seller == null) {
            throw new IllegalArgumentException("Seller not found");
        }

        // 生成商品ID和时间
        long productId = System.currentTimeMillis();

        // 上传图片并获取路径
        String imagePath = null;
        if (file != null && !file.isEmpty()) {
            imagePath = FileUploadUtil.saveFile(file, productId, "uploads/images");
        }

        // 创建商品对象
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setRentalPrice(productDTO.getRentalPrice());
        product.setStock(productDTO.getStock());
        product.setRentalStock(productDTO.getRentalStock());
        product.setSellerId(productDTO.getSellerId());
        product.setIsActive(productDTO.getIsActive());
        product.setImagePath(imagePath);
        product.setCreatedAt(LocalDateTime.now());

        // 保存商品到数据库
        Product savedProduct = productRepository.save(product);

        // 返回保存后的商品 DTO
        return new ProductDTO(savedProduct);
    }

    // 更新商品
    public Product updateProduct(Product product) {
        return productRepository.save(product);
    }

    // 删除商品
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }


    //通过卖家id找产品
    public List<Product> getProductsBySeller(Long sellerId) {
        return productRepository.findBySellerId(sellerId);
    }

    // 检查库存是否充足
    public boolean checkStock(Long productId, Integer quantity, String type) {
        // 根据类型选择库存字段
        int availableStock;
        if ("PURCHASE".equalsIgnoreCase(type)) {
            availableStock = productRepository.getStockById(productId);
        } else if ("RENTAL".equalsIgnoreCase(type)) {
            availableStock = productRepository.getRentalStockById(productId);
        } else {
            throw new IllegalArgumentException("无效的订单类型: " + type);
        }

        // 检查库存是否足够
        return quantity <= availableStock;
    }

}


