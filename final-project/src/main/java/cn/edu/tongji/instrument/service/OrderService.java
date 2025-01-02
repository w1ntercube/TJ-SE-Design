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

    // 根据用户ID和可选条件（订单类型和状态）筛选订单
    public List<Order> getOrdersByUserAndFilters(Long userId, String orderType, OrderStatus orderStatus) {
        if (orderStatus == null && orderType == null) {
            return new ArrayList<>(orderRepository.findByUserId(userId));
        } else if ("PURCHASE".equalsIgnoreCase(orderType)) {
            // 查询购买订单
            if (orderStatus != null) {
                return new ArrayList<>(purchaseOrderRepository.findByUserIdAndOrderStatus(userId, orderStatus));
            } else {
                return new ArrayList<>(purchaseOrderRepository.findByUserId(userId));
            }
        } else if ("RENTAL".equalsIgnoreCase(orderType)) {
            // 查询租赁订单
            if (orderStatus != null) {
                return new ArrayList<>(rentalOrderRepository.findByUserIdAndOrderStatus(userId, orderStatus));
            } else {
                return new ArrayList<>(rentalOrderRepository.findByUserId(userId));
            }
        } else if (orderType == null) {
            // 如果 orderType 为 null，需要同时查询两类订单并合并
            List<Order> orders = new ArrayList<>(purchaseOrderRepository.findByUserId(userId));
            orders.addAll(rentalOrderRepository.findByUserId(userId));
            return orders;
        } else {
            throw new IllegalArgumentException("Unsupported order type: " + orderType);
        }
    }

}
