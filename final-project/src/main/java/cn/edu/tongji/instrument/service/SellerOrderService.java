package cn.edu.tongji.instrument.service;

import cn.edu.tongji.instrument.entity.*;
import cn.edu.tongji.instrument.entity.enums.OrderStatus;
import cn.edu.tongji.instrument.repository.ProductRepository;
import cn.edu.tongji.instrument.repository.PurchaseOrderRepository;
import cn.edu.tongji.instrument.repository.RentalOrderRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SellerOrderService {

    private final ProductRepository productRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final RentalOrderRepository rentalOrderRepository;

    public SellerOrderService(
            ProductRepository productRepository,
            PurchaseOrderRepository purchaseOrderRepository,
            RentalOrderRepository rentalOrderRepository
    ) {
        this.productRepository = productRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.rentalOrderRepository = rentalOrderRepository;
    }

    /**
     * 获取卖家订单（支持根据订单状态和订单种类筛选）
     *
     * @param sellerId 卖家 ID
     * @param orderType 可选：订单种类（"PURCHASE" 或 "RENTAL"）
     * @param orderStatus 可选：订单状态
     * @return 卖家订单列表
     */
    public List<Map<String, Object>> getSellerOrders(Long sellerId, String orderType, OrderStatus orderStatus) {
        // 查找卖家所有商品的 ID
        List<Long> productIds = productRepository.findBySellerId(sellerId)
                .stream()
                .map(Product::getId)
                .collect(Collectors.toList());

        if (productIds.isEmpty()) {
            return Collections.emptyList(); // 如果没有商品，直接返回空列表
        }

        List<Map<String, Object>> result = new ArrayList<>();

        // 查询购买订单
        if (orderType == null || "PURCHASE".equalsIgnoreCase(orderType)) {
            List<PurchaseOrder> purchaseOrders = (orderStatus == null)
                    ? purchaseOrderRepository.findByProductIdIn(productIds)
                    : purchaseOrderRepository.findByProductIdInAndOrderStatus(productIds, orderStatus);

            purchaseOrders.forEach(order -> result.add(convertPurchaseOrderToMap(order)));
        }

        // 查询租赁订单
        if (orderType == null || "RENTAL".equalsIgnoreCase(orderType)) {
            List<RentalOrder> rentalOrders = (orderStatus == null)
                    ? rentalOrderRepository.findByProductIdIn(productIds)
                    : rentalOrderRepository.findByProductIdInAndOrderStatus(productIds, orderStatus);

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
}
