package com.skillverse.dto;

public class CourseEnrollmentRequest {

    private Long userId;
    private Long courseId;
    private String paymentStatus;
    private String paymentMethod;
    private String transactionId;
    private Double amountPaid;

    public CourseEnrollmentRequest() {}

    public CourseEnrollmentRequest(Long userId, Long courseId, String paymentStatus, String paymentMethod, Double amountPaid) {
        this.userId = userId;
        this.courseId = courseId;
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
        this.amountPaid = amountPaid;
    }

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
}
