package com.skillverse.dto;

/**
 * Data Transfer Object representing an individual cart item in a store order.
 */
public class CartItemDTO {
    public Long productId;
    public Integer quantity;

    public CartItemDTO() {}

    public CartItemDTO(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
