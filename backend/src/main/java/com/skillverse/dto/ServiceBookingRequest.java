package com.skillverse.dto;

public class ServiceBookingRequest {

    private Long customerId;
    private Long workerId;
    private String serviceType;
    private String preferredDate;
    private String preferredTime;
    private String address;
    private String description;
    private Double estimatedCost;

    public ServiceBookingRequest() {}

    public ServiceBookingRequest(Long customerId, Long workerId, String serviceType, String address, Double estimatedCost) {
        this.customerId = customerId;
        this.workerId = workerId;
        this.serviceType = serviceType;
        this.address = address;
        this.estimatedCost = estimatedCost;
    }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Long getWorkerId() { return workerId; }
    public void setWorkerId(Long workerId) { this.workerId = workerId; }

    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

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
}
