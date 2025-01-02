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
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private RentalOrderRepository rentalOrderRepository;


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

    // 查询指定用户的全部订单
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public List<Order> getOrdersByUserAndFilters(Long userId, String orderType, OrderStatus orderStatus) {
        List<Order> orders = new ArrayList<>();

        if (orderType == null && orderStatus == null) {
            // 如果种类和状态都为 null，查询所有订单
            orders.addAll(purchaseOrderRepository.findByUserId(userId));
            orders.addAll(rentalOrderRepository.findByUserId(userId));
        } else if (orderType == null) {
            // 如果只指定了状态，则查询所有类型的订单中匹配状态的订单
            orders.addAll(purchaseOrderRepository.findByUserIdAndOrderStatus(userId, orderStatus));
            orders.addAll(rentalOrderRepository.findByUserIdAndOrderStatus(userId, orderStatus));
        } else if ("PURCHASE".equalsIgnoreCase(orderType)) {
            // 如果指定了购买订单
            if (orderStatus != null) {
                orders.addAll(purchaseOrderRepository.findByUserIdAndOrderStatus(userId, orderStatus));
            } else {
                orders.addAll(purchaseOrderRepository.findByUserId(userId));
            }
        } else if ("RENTAL".equalsIgnoreCase(orderType)) {
            // 如果指定了租赁订单
            if (orderStatus != null) {
                orders.addAll(rentalOrderRepository.findByUserIdAndOrderStatus(userId, orderStatus));
            } else {
                orders.addAll(rentalOrderRepository.findByUserId(userId));
            }
        } else {
            throw new IllegalArgumentException("Unsupported order type: " + orderType);
        }

        return orders;
    }


}
