package com.skillverse.dto;

public class ProblemOfferRequest {
    private Long workerId;
    private Double proposedPrice;
    private String message;
    private String estimatedArrival;

    public ProblemOfferRequest() {}

    public Long getWorkerId() {
        return workerId;
    }

    public void setWorkerId(Long workerId) {
        this.workerId = workerId;
    }

    public Double getProposedPrice() {
        return proposedPrice;
    }

    public void setProposedPrice(Double proposedPrice) {
        this.proposedPrice = proposedPrice;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEstimatedArrival() {
        return estimatedArrival;
    }

    public void setEstimatedArrival(String estimatedArrival) {
        this.estimatedArrival = estimatedArrival;
    }
}
