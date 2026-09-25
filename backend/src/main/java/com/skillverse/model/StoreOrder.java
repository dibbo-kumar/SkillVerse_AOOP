package com.skillverse.model;

import java.time.LocalDateTime;

public class StoreOrder {

    private Long id;
    private String orderNumber;
    private Long userId;
    private Double totalAmount;
    private Double subtotal;
    private Double deliveryFee;
    private Double discount;
    private String customerName;
    private String phone;
    private String address;
    private String paymentMethod;
    private String paymentStatus;
    private String orderStatus;
    private String orderItemsSummary;
    private String deliveryReceiptUrl;
    private LocalDateTime placedAt;

    public StoreOrder() {
        this.deliveryFee = 60.0;
        this.discount = 0.0;
        this.paymentStatus = "PENDING";
        this.orderStatus = "ORDER_PLACED";
        this.placedAt = LocalDateTime.now();
    }

    public StoreOrder(Long id, String orderNumber, Long userId, Double totalAmount, String customerName, String phone, String address, String paymentMethod) {
        this();
        this.id = id;
        this.orderNumber = orderNumber;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.subtotal = totalAmount;
        this.customerName = customerName;
        this.phone = phone;
        this.address = address;
        this.paymentMethod = paymentMethod;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }

    public Double getDeliveryFee() { return deliveryFee; }
    public void setDeliveryFee(Double deliveryFee) { this.deliveryFee = deliveryFee; }

    public Double getDiscount() { return discount; }
    public void setDiscount(Double discount) { this.discount = discount; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }

    public String getOrderItemsSummary() { return orderItemsSummary; }
    public void setOrderItemsSummary(String orderItemsSummary) { this.orderItemsSummary = orderItemsSummary; }

    public String getDeliveryReceiptUrl() { return deliveryReceiptUrl; }
    public void setDeliveryReceiptUrl(String deliveryReceiptUrl) { this.deliveryReceiptUrl = deliveryReceiptUrl; }

    public LocalDateTime getPlacedAt() { return placedAt; }
    public void setPlacedAt(LocalDateTime placedAt) { this.placedAt = placedAt; }
}
