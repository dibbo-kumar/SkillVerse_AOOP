package com.skillverse.model;

import java.time.LocalDateTime;

public class WorkerWallet {

    private Long id;
    private Long workerId;
    private Double balance;
    private Double totalEarnings;
    private Double totalPlatformFees;
    private Double totalWithdrawals;
    private Double outstandingFees;
    private LocalDateTime updatedAt;

    public WorkerWallet() {
        this.balance = 0.0;
        this.totalEarnings = 0.0;
        this.totalPlatformFees = 0.0;
        this.totalWithdrawals = 0.0;
        this.outstandingFees = 0.0;
        this.updatedAt = LocalDateTime.now();
    }

    public WorkerWallet(Long id, Long workerId, Double balance, Double totalEarnings) {
        this();
        this.id = id;
        this.workerId = workerId;
        this.balance = balance;
        this.totalEarnings = totalEarnings;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getWorkerId() { return workerId; }
    public void setWorkerId(Long workerId) { this.workerId = workerId; }

    public Double getBalance() { return balance; }
    public void setBalance(Double balance) { this.balance = balance; }

    public Double getTotalEarnings() { return totalEarnings; }
    public void setTotalEarnings(Double totalEarnings) { this.totalEarnings = totalEarnings; }

    public Double getTotalPlatformFees() { return totalPlatformFees; }
    public void setTotalPlatformFees(Double totalPlatformFees) { this.totalPlatformFees = totalPlatformFees; }

    public Double getTotalWithdrawals() { return totalWithdrawals; }
    public void setTotalWithdrawals(Double totalWithdrawals) { this.totalWithdrawals = totalWithdrawals; }

    public Double getOutstandingFees() { return outstandingFees; }
    public void setOutstandingFees(Double outstandingFees) { this.outstandingFees = outstandingFees; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
