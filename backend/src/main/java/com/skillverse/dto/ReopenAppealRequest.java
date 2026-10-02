package com.skillverse.dto;

public class ReopenAppealRequest {
    private Long userId;
    private String reason;

    public ReopenAppealRequest() {}

    public ReopenAppealRequest(Long userId, String reason) {
        this.userId = userId;
        this.reason = reason;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
