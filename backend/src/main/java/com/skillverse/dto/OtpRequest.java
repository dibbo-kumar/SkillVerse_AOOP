package com.skillverse.dto;

/**
 * Data Transfer Object for sending and verifying phone OTPs.
 */
public class OtpRequest {
    private String phone;
    private String otp;
    private Long userId;

    public OtpRequest() {}

    public OtpRequest(String phone, String otp) {
        this.phone = phone;
        this.otp = otp;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
