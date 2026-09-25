package com.skillverse.dto;

public class ProblemOfferRequest {

    private Long problemPostId;
    private Long workerId;
    private Double proposedPrice;
    private String message;
    private String estimatedArrival;

    public ProblemOfferRequest() {}

    public ProblemOfferRequest(Long problemPostId, Long workerId, Double proposedPrice, String message, String estimatedArrival) {
        this.problemPostId = problemPostId;
        this.workerId = workerId;
        this.proposedPrice = proposedPrice;
        this.message = message;
        this.estimatedArrival = estimatedArrival;
    }

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
}
