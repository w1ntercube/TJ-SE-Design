package cn.edu.tongji.instrument.controller;

import cn.edu.tongji.instrument.entity.*;
import cn.edu.tongji.instrument.entity.enums.OrderStatus;
import cn.edu.tongji.instrument.repository.ProductRepository;
import cn.edu.tongji.instrument.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {


    private final OrderService orderService;
    private final ProductRepository productRepository;

    public OrderController(OrderService orderService, ProductRepository productRepository) {
        this.orderService = orderService;
        this.productRepository = productRepository;
    }

    // 查询所有订单
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // 查询单个订单
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    // 更新订单状态
    @PatchMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
    }

    // 根据用户订单类型和状态筛选订单
    @GetMapping("/filter")
    public ResponseEntity<List<Order>> getOrdersByUserAndFilters(
            @RequestParam Long userId,
            @RequestParam(required = false) String orderType,
            @RequestParam(required = false) OrderStatus orderStatus
    ) {
        List<Order> orders = orderService.getOrdersByUserAndFilters(userId, orderType, orderStatus);
        return ResponseEntity.ok(orders);
    }


    // 复用订单筛选接口：通过用户作为卖家的逻辑实现店铺订单查询
    @GetMapping("/seller/filter")
    public ResponseEntity<List<Order>> getSellerOrders(
            @RequestParam Long sellerId, // 卖家ID
            @RequestParam(required = false) String orderType,
            @RequestParam(required = false) OrderStatus orderStatus
    ) {
        // 查询卖家相关的商品ID列表
        List<Long> productIds = productRepository.findBySellerId(sellerId)
                .stream()
                .map(Product::getId)
                .toList();

        if (productIds.isEmpty()) {
            // 如果卖家没有商品，直接返回空列表
            return ResponseEntity.ok(Collections.emptyList());
        }

        // 使用现有的 Service 方法筛选订单
        List<Order> orders = orderService.getOrdersByProductIdsAndFilters(productIds, orderType, orderStatus);

        return ResponseEntity.ok(orders);
    }
}
