package com.skillverse.dto;

/**
 * Data Transfer Object for Price Negotiation / Counter Offers.
 */
public class CounterOfferRequest {
    private Double price;
    private String offeredBy;
    private String status;

    public CounterOfferRequest() {}

    public CounterOfferRequest(Double price, String offeredBy, String status) {
        this.price = price;
        this.offeredBy = offeredBy;
        this.status = status;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getOfferedBy() {
        return offeredBy;
    }

    public void setOfferedBy(String offeredBy) {
        this.offeredBy = offeredBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
