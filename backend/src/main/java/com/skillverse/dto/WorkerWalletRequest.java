package com.skillverse.dto;

public class WorkerWalletRequest {

    private Long workerId;
    private Double balance;
    private Double totalEarnings;

    public WorkerWalletRequest() {}

    public WorkerWalletRequest(Long workerId, Double balance, Double totalEarnings) {
        this.workerId = workerId;
        this.balance = balance;
        this.totalEarnings = totalEarnings;
    }

    public Long getWorkerId() { return workerId; }
    public void setWorkerId(Long workerId) { this.workerId = workerId; }

    public Double getBalance() { return balance; }
    public void setBalance(Double balance) { this.balance = balance; }

    public Double getTotalEarnings() { return totalEarnings; }
    public void setTotalEarnings(Double totalEarnings) { this.totalEarnings = totalEarnings; }
}
