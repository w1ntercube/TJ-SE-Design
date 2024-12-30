package cn.edu.tongji.instrument.service;

import cn.edu.tongji.instrument.entity.*;
import cn.edu.tongji.instrument.entity.enums.OrderStatus;
import cn.edu.tongji.instrument.repository.OrderRepository;
import cn.edu.tongji.instrument.repository.PurchaseOrderRepository;
import cn.edu.tongji.instrument.repository.RentalOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private RentalOrderRepository rentalOrderRepository;

    // 创建购买订单
    public PurchaseOrder createPurchaseOrder(Long userId, Long productId, Integer quantity, BigDecimal totalPrice) {
        PurchaseOrder order = new PurchaseOrder();
        order.setUserId(userId);
        order.setProductId(productId);
        order.setQuantity(quantity);
        order.setTotalPrice(totalPrice);
        order.setOrderStatus(OrderStatus.PENDING);
        return purchaseOrderRepository.save(order);
    }

    // 创建租赁订单
    public RentalOrder createRentalOrder(Long userId, Long productId, BigDecimal totalPrice, BigDecimal deposit,
                                         LocalDateTime rentalStart, LocalDateTime rentalEnd) {
        RentalOrder order = new RentalOrder();
        order.setUserId(userId);
        order.setProductId(productId);
        order.setTotalPrice(totalPrice);
        order.setDeposit(deposit);
        order.setRentalStart(rentalStart);
        order.setRentalEnd(rentalEnd);
        order.setRentalDurationDays((int) rentalStart.until(rentalEnd, java.time.temporal.ChronoUnit.DAYS));
        order.setOrderStatus(OrderStatus.PENDING);
        return rentalOrderRepository.save(order);
    }

    // 查询所有订单
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // 查询单个订单
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));
    }

    // 更新订单状态
    public Order updateOrderStatus(Long id, OrderStatus status) {
        Order order = getOrderById(id);
        order.setOrderStatus(status);
        return orderRepository.save(order);
    }
}
