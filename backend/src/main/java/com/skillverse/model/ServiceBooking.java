package com.skillverse.model;

import java.time.LocalDateTime;

public class ServiceBooking {

    private Long id;
    private Long customerId;
    private Long workerId;
    private String serviceType;
    private String status; // PENDING, CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED
    private String bookingSource; // DIRECT, POSTED_PROBLEM
    private String preferredDate;
    private String preferredTime;
    private String address;
    private String description;
    private Double estimatedCost;
    private Double agreedCost;
    private String beforePhoto;
    private String afterPhoto;
    private LocalDateTime createdAt;

    public ServiceBooking() {
        this.status = "PENDING";
        this.bookingSource = "DIRECT";
        this.createdAt = LocalDateTime.now();
    }

    public ServiceBooking(Long id, Long customerId, Long workerId, String serviceType, String address, Double estimatedCost) {
        this();
        this.id = id;
        this.customerId = customerId;
        this.workerId = workerId;
        this.serviceType = serviceType;
        this.address = address;
        this.estimatedCost = estimatedCost;
        this.agreedCost = estimatedCost;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Long getWorkerId() { return workerId; }
    public void setWorkerId(Long workerId) { this.workerId = workerId; }

    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getBookingSource() { return bookingSource; }
    public void setBookingSource(String bookingSource) { this.bookingSource = bookingSource; }

    public String getPreferredDate() { return preferredDate; }
    public void setPreferredDate(String preferredDate) { this.preferredDate = preferredDate; }

    public String getPreferredTime() { return preferredTime; }
    public void setPreferredTime(String preferredTime) { this.preferredTime = preferredTime; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getEstimatedCost() { return estimatedCost; }
    public void setEstimatedCost(Double estimatedCost) { this.estimatedCost = estimatedCost; }

    public Double getAgreedCost() { return agreedCost; }
    public void setAgreedCost(Double agreedCost) { this.agreedCost = agreedCost; }

    public String getBeforePhoto() { return beforePhoto; }
    public void setBeforePhoto(String beforePhoto) { this.beforePhoto = beforePhoto; }

    public String getAfterPhoto() { return afterPhoto; }
    public void setAfterPhoto(String afterPhoto) { this.afterPhoto = afterPhoto; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
