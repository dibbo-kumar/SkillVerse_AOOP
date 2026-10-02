package com.skillverse.dto;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) for creating or updating a ServiceBooking.
 */
public class BookingRequest {
    private Long customerId;
    private Long workerId;
    private String serviceType;
    private LocalDateTime scheduledTime;
    private String preferredDate;
    private String preferredTime;
    private String address;
    private String description;
    private String applianceDetails;
    private String photoUrl;
    private Double estimatedCost;
    private Double customerOfferPrice;
    private Double workerCounterPrice;
    private Double agreedCost;
    private Double basePrice;
    private String lastOfferedBy;
    private String bookingSource;
    private Long problemPostId;

    public BookingRequest() {}

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getWorkerId() {
        return workerId;
    }

    public void setWorkerId(Long workerId) {
        this.workerId = workerId;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public String getPreferredDate() {
        return preferredDate;
    }

    public void setPreferredDate(String preferredDate) {
        this.preferredDate = preferredDate;
    }

    public String getPreferredTime() {
        return preferredTime;
    }

    public void setPreferredTime(String preferredTime) {
        this.preferredTime = preferredTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getApplianceDetails() {
        return applianceDetails;
    }

    public void setApplianceDetails(String applianceDetails) {
        this.applianceDetails = applianceDetails;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Double getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(Double estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public Double getCustomerOfferPrice() {
        return customerOfferPrice;
    }

    public void setCustomerOfferPrice(Double customerOfferPrice) {
        this.customerOfferPrice = customerOfferPrice;
    }

    public Double getWorkerCounterPrice() {
        return workerCounterPrice;
    }

    public void setWorkerCounterPrice(Double workerCounterPrice) {
        this.workerCounterPrice = workerCounterPrice;
    }

    public Double getAgreedCost() {
        return agreedCost;
    }

    public void setAgreedCost(Double agreedCost) {
        this.agreedCost = agreedCost;
    }

    public Double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(Double basePrice) {
        this.basePrice = basePrice;
    }

    public String getLastOfferedBy() {
        return lastOfferedBy;
    }

    public void setLastOfferedBy(String lastOfferedBy) {
        this.lastOfferedBy = lastOfferedBy;
    }

    public String getBookingSource() {
        return bookingSource;
    }

    public void setBookingSource(String bookingSource) {
        this.bookingSource = bookingSource;
    }

    public Long getProblemPostId() {
        return problemPostId;
    }

    public void setProblemPostId(Long problemPostId) {
        this.problemPostId = problemPostId;
    }
}
