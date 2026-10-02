package com.skillverse.dto;

/**
 * Data Transfer Object for Worker Wallet Withdrawal.
 */
public class WithdrawalRequest {
    private Long workerId;
    private Double amount;
    private String paymentMethod;
    private String accountNumber;

    public WithdrawalRequest() {}

    public WithdrawalRequest(Long workerId, Double amount, String paymentMethod, String accountNumber) {
        this.workerId = workerId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.accountNumber = accountNumber;
    }

    public Long getWorkerId() {
        return workerId;
    }

    public void setWorkerId(Long workerId) {
        this.workerId = workerId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
}
