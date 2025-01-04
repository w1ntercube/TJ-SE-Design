package cn.edu.tongji.instrument.controller;

import cn.edu.tongji.instrument.entity.*;
import cn.edu.tongji.instrument.entity.enums.OrderStatus;
import cn.edu.tongji.instrument.repository.ProductRepository;
import cn.edu.tongji.instrument.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        System.out.println("OrderController类的getAllorders方法调用了orderService的getAllOrders方法。");
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // 查询单个订单
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        System.out.println("OrderController类的getOrderById方法调用了orderService的getOrderById方法。");
        return ResponseEntity.ok(orderService.getOrderById(id));
    }


    /**
     * 根据用户ID、订单类型和订单状态筛选订单
     *
     * @param userId 用户ID
     * @param orderType 可选：订单类型（"PURCHASE" 或 "RENTAL"）
     * @param orderStatus 可选：订单状态
     * @return 筛选后的订单列表
     */
    @GetMapping("/filter")
    public ResponseEntity<List<Map<String, Object>>> getOrdersByUserAndFilters(
            @RequestParam Long userId,
            @RequestParam(required = false) String orderType,
            @RequestParam(required = false) OrderStatus orderStatus
    ) {
        List<Map<String, Object>> orders = orderService.getOrdersByUserAndFilters(userId, orderType, orderStatus);
        System.out.println("OrderController类的getOrdersByUserAndFilters方法调用了orderService的getOrdersByUserAndFilters方法。");

        return ResponseEntity.ok(orders);
    }

    // 买家确认收货
    @CrossOrigin(origins = "http://localhost:8081")
    @PatchMapping("/{id}/confirm-delivery")
    public ResponseEntity<Order> confirmOrderReceipt(@PathVariable Long id) {
        Order updatedOrder = orderService.confirmOrderDelivery(id);
        System.out.println("OrderController类的confirmOrderReceipt方法调用了orderService的confirmOrderDelivery方法。");

        return ResponseEntity.ok(updatedOrder);
    }

    // 买家申请还货
    @PatchMapping("/{id}/return")
    @CrossOrigin(origins = "http://localhost:8081")
    public ResponseEntity<Order> returnOrder(@PathVariable Long id) {
        Order updatedOrder = orderService.returnOrder(id);
        System.out.println("OrderController类的returnOrder方法调用了orderService的returnOrder方法。");

        return ResponseEntity.ok(updatedOrder);
    }

    @PostMapping("/{orderId}/pay")
    public ResponseEntity<Map<String, String>> payOrder(
            @PathVariable Long orderId,
            @RequestParam String orderType
    ) {
        Map<String, String> response = new HashMap<>();
        try {
            orderService.updateOrderStatusToPaid(orderId, orderType);
            System.out.println("OrderController类的payOrder方法调用了orderService的updateOrderStatusToPaid方法。");

            response.put("message", "订单已成功支付，库存已更新");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("error", "订单支付失败，请稍后再试");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
