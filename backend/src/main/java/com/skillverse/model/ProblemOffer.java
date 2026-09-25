package com.skillverse.model;

import java.time.LocalDateTime;

public class ProblemOffer {

    private Long id;
    private Long problemPostId;
    private Long workerId;
    private Double proposedPrice;
    private String message;
    private String estimatedArrival;
    private String status; // PENDING, ACCEPTED, DECLINED
    private LocalDateTime createdAt;

    public ProblemOffer() {
        this.status = "PENDING";
        this.createdAt = LocalDateTime.now();
    }

    public ProblemOffer(Long id, Long problemPostId, Long workerId, Double proposedPrice, String message, String estimatedArrival) {
        this();
        this.id = id;
        this.problemPostId = problemPostId;
        this.workerId = workerId;
        this.proposedPrice = proposedPrice;
        this.message = message;
        this.estimatedArrival = estimatedArrival;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProblemPostId() { return problemPostId; }
    public void setProblemPostId(Long problemPostId) { this.problemPostId = problemPostId; }

    public Long getWorkerId() { return workerId; }
    public void setWorkerId(Long workerId) { this.workerId = workerId; }

    public Double getProposedPrice() { return proposedPrice; }
    public void setProposedPrice(Double proposedPrice) { this.proposedPrice = proposedPrice; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getEstimatedArrival() { return estimatedArrival; }
    public void setEstimatedArrival(String estimatedArrival) { this.estimatedArrival = estimatedArrival; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
