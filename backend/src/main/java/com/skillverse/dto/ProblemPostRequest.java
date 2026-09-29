package com.skillverse.dto;

public class ProblemPostRequest {
    private Long customerId;
    private String serviceCategory;
    private String title;
    private String description;
    private String applianceInfo;
    private String photoUrl;
    private String preferredDate;
    private String preferredTime;
    private String address;
    private Double budgetPrice;

    public ProblemPostRequest() {}

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getServiceCategory() {
        return serviceCategory;
    }

    public void setServiceCategory(String serviceCategory) {
        this.serviceCategory = serviceCategory;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getApplianceInfo() {
        return applianceInfo;
    }

    public void setApplianceInfo(String applianceInfo) {
        this.applianceInfo = applianceInfo;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
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

    public Double getBudgetPrice() {
        return budgetPrice;
    }

    public void setBudgetPrice(Double budgetPrice) {
        this.budgetPrice = budgetPrice;
    }
}
