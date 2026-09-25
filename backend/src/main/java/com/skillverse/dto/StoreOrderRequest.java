package com.skillverse.dto;

public class StoreOrderRequest {

    private String orderNumber;
    private Long userId;
    private Double totalAmount;
    private String customerName;
    private String phone;
    private String address;
    private String paymentMethod;
    private String orderItemsSummary;

    public StoreOrderRequest() {}

    public StoreOrderRequest(String orderNumber, Long userId, Double totalAmount, String customerName, String phone, String address, String paymentMethod) {
        this.orderNumber = orderNumber;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.customerName = customerName;
        this.phone = phone;
        this.address = address;
        this.paymentMethod = paymentMethod;
    }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getOrderItemsSummary() { return orderItemsSummary; }
    public void setOrderItemsSummary(String orderItemsSummary) { this.orderItemsSummary = orderItemsSummary; }
}
