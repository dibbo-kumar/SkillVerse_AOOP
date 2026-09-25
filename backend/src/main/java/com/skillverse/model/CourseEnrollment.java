package com.skillverse.model;

import java.time.LocalDateTime;

public class CourseEnrollment {

    private Long id;
    private Long userId;
    private Long courseId;
    private String paymentStatus; // FREE, PENDING, SUCCESSFUL, FAILED
    private String paymentMethod; // BKASH, NAGAD, ROCKET, CARD, NONE
    private String transactionId;
    private Double amountPaid;
    private Integer progressPercentage;
    private Boolean isCompleted;
    private String certificateUrl;
    private LocalDateTime enrolledAt;

    public CourseEnrollment() {
        this.progressPercentage = 0;
        this.isCompleted = false;
        this.enrolledAt = LocalDateTime.now();
    }

    public CourseEnrollment(Long id, Long userId, Long courseId, String paymentStatus, String paymentMethod, Double amountPaid) {
        this();
        this.id = id;
        this.userId = userId;
        this.courseId = courseId;
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
        this.amountPaid = amountPaid;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public Double getAmountPaid() { return amountPaid; }
    public void setAmountPaid(Double amountPaid) { this.amountPaid = amountPaid; }

    public Integer getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(Integer progressPercentage) { this.progressPercentage = progressPercentage; }

    public Boolean getIsCompleted() { return isCompleted; }
    public void setIsCompleted(Boolean isCompleted) { this.isCompleted = isCompleted; }

    public String getCertificateUrl() { return certificateUrl; }
    public void setCertificateUrl(String certificateUrl) { this.certificateUrl = certificateUrl; }

    public LocalDateTime getEnrolledAt() { return enrolledAt; }
    public void setEnrolledAt(LocalDateTime enrolledAt) { this.enrolledAt = enrolledAt; }
}
