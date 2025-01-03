package cn.edu.tongji.instrument.service;

import cn.edu.tongji.instrument.entity.*;
import cn.edu.tongji.instrument.entity.enums.OrderStatus;
import cn.edu.tongji.instrument.repository.OrderRepository;
import cn.edu.tongji.instrument.repository.ProductRepository;
import cn.edu.tongji.instrument.repository.PurchaseOrderRepository;
import cn.edu.tongji.instrument.repository.RentalOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final RentalOrderRepository rentalOrderRepository;

    public OrderService(PurchaseOrderRepository purchaseOrderRepository,
                        RentalOrderRepository rentalOrderRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.rentalOrderRepository = rentalOrderRepository;
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

    /**
     * 根据用户ID、订单类型和订单状态筛选订单
     *
     * @param userId 用户ID
     * @param orderType 可选：订单类型（"PURCHASE" 或 "RENTAL"）
     * @param orderStatus 可选：订单状态
     * @return 筛选后的订单数据列表
     */

    /**
     * 根据用户ID、订单类型和订单状态筛选订单
     */
    public List<Map<String, Object>> getOrdersByUserAndFilters(Long userId, String orderType, OrderStatus orderStatus) {
        List<Map<String, Object>> result = new ArrayList<>();

        // 处理购买订单
        if (orderType == null || "PURCHASE".equalsIgnoreCase(orderType)) {
            List<PurchaseOrder> purchaseOrders = (orderStatus == null)
                    ? purchaseOrderRepository.findByUserId(userId)
                    : purchaseOrderRepository.findByUserIdAndOrderStatus(userId, orderStatus);

            purchaseOrders.forEach(order -> result.add(convertPurchaseOrderToMap(order)));
        }

        // 处理租赁订单
        if (orderType == null || "RENTAL".equalsIgnoreCase(orderType)) {
            List<RentalOrder> rentalOrders = (orderStatus == null)
                    ? rentalOrderRepository.findByUserId(userId)
                    : rentalOrderRepository.findByUserIdAndOrderStatus(userId, orderStatus);

            rentalOrders.forEach(order -> result.add(convertRentalOrderToMap(order)));
        }

        return result;
    }

    /**
     * 转换购买订单为统一格式
     */
    private Map<String, Object> convertPurchaseOrderToMap(PurchaseOrder order) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", order.getId());
        map.put("productId", order.getProductId());
        map.put("quantity", order.getQuantity());
        map.put("totalPrice", order.getTotalPrice());
        map.put("orderStatus", order.getOrderStatus());
        map.put("address", order.getAddress());
        map.put("createdAt", order.getCreatedAt());
        map.put("orderType", "PURCHASE");

        Product product = productRepository.findById(order.getProductId()).orElse(null);
        if (product != null) {
            map.put("imagePath", product.getImagePath());
        }

        return map;
    }


    /**
     * 转换租赁订单为统一格式
     */
    private Map<String, Object> convertRentalOrderToMap(RentalOrder order) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", order.getId());
        map.put("productId", order.getProductId());
        map.put("quantity", order.getQuantity());
        map.put("totalPrice", order.getTotalPrice());
        map.put("rentalDurationDays", order.getRentalDurationDays());
        map.put("rentalStart", order.getRentalStart());
        map.put("rentalEnd", order.getRentalEnd());
        map.put("orderStatus", order.getOrderStatus());
        map.put("address", order.getAddress());
        map.put("createdAt", order.getCreatedAt());
        map.put("orderType", "RENTAL");
        map.put("deposit", order.getDeposit());

        Product product = productRepository.findById(order.getProductId()).orElse(null);
        if (product != null) {
            map.put("imagePath", product.getImagePath());
        }

        return map;
    }

    public Order confirmOrderDelivery(Long orderId) {
        Order order = getOrderById(orderId);

        // 检查当前订单状态是否为 SHIPPED
        if (!OrderStatus.SHIPPED.equals(order.getOrderStatus())) {
            throw new IllegalStateException("订单状态必须为 'SHIPPED' 才能确认收货");
        }

        // 更新订单状态为 DELIVERED
        order.setOrderStatus(OrderStatus.DELIVERED);
        return orderRepository.save(order);
    }

    // 退还订单逻辑
    public Order returnOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));

        if (!order.getOrderStatus().equals(OrderStatus.DELIVERED)) {
            throw new IllegalStateException("Only DELIVERED orders can be returned.");
        }

        order.setOrderStatus(OrderStatus.RETURNED);
        return orderRepository.save(order);
    }
}
