package cn.edu.tongji.instrument.controller;

import cn.edu.tongji.instrument.entity.*;
import cn.edu.tongji.instrument.entity.enums.OrderStatus;
import cn.edu.tongji.instrument.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // 创建购买订单
    @PostMapping("/purchase")
    public ResponseEntity<Order> createPurchaseOrder(
            @RequestParam Long userId,
            @RequestParam Long productId,
            @RequestParam Integer quantity,
            @RequestParam BigDecimal totalPrice
    ) {
        PurchaseOrder order = orderService.createPurchaseOrder(userId, productId, quantity, totalPrice);
        return ResponseEntity.ok(order);
    }

    // 创建租赁订单
    @PostMapping("/rental")
    public ResponseEntity<Order> createRentalOrder(
            @RequestParam Long userId,
            @RequestParam Long productId,
            @RequestParam BigDecimal totalPrice,
            @RequestParam BigDecimal deposit,
            @RequestParam LocalDateTime rentalStart,
            @RequestParam LocalDateTime rentalEnd
    ) {
        RentalOrder order = orderService.createRentalOrder(userId, productId, totalPrice, deposit, rentalStart, rentalEnd);
        return ResponseEntity.ok(order);
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

    // 查询指定用户的订单
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUserId(@PathVariable Long userId) {
        List<Order> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
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



}
