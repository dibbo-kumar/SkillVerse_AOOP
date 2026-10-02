package com.skillverse.dto;

/**
 * Data Transfer Object for creating Product Reviews.
 */
public class ReviewDTO {
    public Long productId;
    public Long userId;
    public Integer rating;
    public String comment;
    public String photoUrl;

    public ReviewDTO() {}

    public ReviewDTO(Long productId, Long userId, Integer rating, String comment, String photoUrl) {
        this.productId = productId;
        this.userId = userId;
        this.rating = rating;
        this.comment = comment;
        this.photoUrl = photoUrl;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
