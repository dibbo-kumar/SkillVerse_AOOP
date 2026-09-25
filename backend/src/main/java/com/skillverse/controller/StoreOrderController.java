package com.skillverse.controller;

import com.skillverse.dto.StoreOrderRequest;
import com.skillverse.model.StoreOrder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/store-orders")
@CrossOrigin(origins = "*")
public class StoreOrderController {

    // In-memory list to store orders (No Database)
    private List<StoreOrder> orderList = new ArrayList<>();
    private Long nextId = 1L;

    public StoreOrderController() {
        // Sample starter data
        StoreOrder o1 = new StoreOrder(nextId++, "ORD-2026-001", 1L, 1450.0, "Rahim Ahmed", "01711112233", "Dhanmondi, Dhaka", "BKASH");
        o1.setOrderItemsSummary("1x Digital Multimeter, 1x Screwdriver Set");
        orderList.add(o1);
    }

    // 1. getAll - uses @RequestParam
    @GetMapping
    public List<StoreOrder> getAll(@RequestParam(required = false) String orderStatus) {
        if (orderStatus == null || orderStatus.isEmpty()) {
            return orderList;
        }

        List<StoreOrder> result = new ArrayList<>();
        for (StoreOrder order : orderList) {
            if (order.getOrderStatus() != null && order.getOrderStatus().equalsIgnoreCase(orderStatus)) {
                result.add(order);
            }
        }
        return result;
    }

    // 2. getById - uses @PathVariable
    @GetMapping("/{id}")
    public StoreOrder getById(@PathVariable Long id) {
        for (StoreOrder order : orderList) {
            if (order.getId().equals(id)) {
                return order;
            }
        }
        return null;
    }

    // 3. save - uses @RequestBody with StoreOrderRequest object
    @PostMapping
    public StoreOrder save(@RequestBody StoreOrderRequest request) {
        StoreOrder order = new StoreOrder();
        order.setId(nextId++);
        order.setOrderNumber(request.getOrderNumber() != null ? request.getOrderNumber() : "ORD-" + System.currentTimeMillis());
        order.setUserId(request.getUserId());
        order.setTotalAmount(request.getTotalAmount());
        order.setSubtotal(request.getTotalAmount());
        order.setCustomerName(request.getCustomerName());
        order.setPhone(request.getPhone());
        order.setAddress(request.getAddress());
        order.setPaymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "CASH_ON_DELIVERY");
        order.setOrderItemsSummary(request.getOrderItemsSummary());

        orderList.add(order);
        return order;
    }

    // 4. upload - uses @RequestParam
    @PostMapping("/{id}/upload")
    public StoreOrder upload(@PathVariable Long id, @RequestParam String deliveryReceiptUrl) {
        for (StoreOrder order : orderList) {
            if (order.getId().equals(id)) {
                order.setDeliveryReceiptUrl(deliveryReceiptUrl);
                return order;
            }
        }
        return null;
    }

    // 5. delete - uses @PathVariable
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        for (int i = 0; i < orderList.size(); i++) {
            if (orderList.get(i).getId().equals(id)) {
                orderList.remove(i);
                return "StoreOrder deleted successfully";
            }
        }
        return "StoreOrder not found";
    }
}
