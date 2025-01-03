package cn.edu.tongji.instrument.controller;

import cn.edu.tongji.instrument.entity.enums.OrderStatus;
import cn.edu.tongji.instrument.service.SellerOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders/seller")
public class SellerOrderController {

    @Autowired
    private final SellerOrderService sellerOrderService;

    public SellerOrderController(SellerOrderService sellerOrderService) {
        this.sellerOrderService = sellerOrderService;
    }

    /**
     * 获取卖家订单（支持订单状态和种类筛选）
     *
     * @param sellerId 卖家 ID
     * @param orderType 可选：订单种类（"PURCHASE" 或 "RENTAL"）
     * @param orderStatus 可选：订单状态
     * @return 卖家订单列表
     */
    @GetMapping("/filter")
    public ResponseEntity<List<Map<String, Object>>> getSellerOrders(
            @RequestParam Long sellerId,
            @RequestParam(required = false) String orderType,
            @RequestParam(required = false) OrderStatus orderStatus
    ) {
        List<Map<String, Object>> orders = sellerOrderService.getSellerOrders(sellerId, orderType, orderStatus);
        return ResponseEntity.ok(orders);
    }
}
