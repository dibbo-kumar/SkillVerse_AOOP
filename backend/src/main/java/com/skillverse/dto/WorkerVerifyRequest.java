package com.skillverse.dto;

/**
 * Data Transfer Object for Worker NID Quick Verification.
 */
public class WorkerVerifyRequest {
    private String nid;
    private String frontPhoto;

    public WorkerVerifyRequest() {}

    public WorkerVerifyRequest(String nid, String frontPhoto) {
        this.nid = nid;
        this.frontPhoto = frontPhoto;
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public String getFrontPhoto() {
        return frontPhoto;
    }

    public void setFrontPhoto(String frontPhoto) {
        this.frontPhoto = frontPhoto;
    }
}
