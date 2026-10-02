package com.skillverse.dto;

import java.util.List;

/**
 * Data Transfer Object for Admin Review/Verification Decision.
 */
public class AdminDecisionRequest {
    private String decision;
    private String status;
    private String reason;
    private String remarks;
    private List<String> fieldsToCorrect;

    public AdminDecisionRequest() {}

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public List<String> getFieldsToCorrect() {
        return fieldsToCorrect;
    }

    public void setFieldsToCorrect(List<String> fieldsToCorrect) {
        this.fieldsToCorrect = fieldsToCorrect;
    }
}
