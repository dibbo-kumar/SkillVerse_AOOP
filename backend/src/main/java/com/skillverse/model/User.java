package com.skillverse.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String phone;
    private String role; // CUSTOMER, WORKER, ADMIN
    private String nidNumber;
    private boolean isVerified;
    @Column(columnDefinition = "TEXT")
    private String profilePicture;
    private Double rating = 5.0;
    private Double latitude;
    private Double longitude;
    @Column(columnDefinition = "TEXT")
    private String address = "";

    private String status = "ACTIVE"; // ACTIVE, SUSPENDED, DEACTIVATED
    private String suspensionReason;
    private java.time.LocalDateTime suspendedAt;

    private Boolean reopenRequested = false;
    @Column(columnDefinition = "TEXT")
    private String reopenReason;
    private java.time.LocalDateTime reopenRequestedAt;

    private java.time.LocalDateTime createdAt = java.time.LocalDateTime.now();

    public User() {}

    public User(String name, String email, String phone, String role) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.isVerified = false;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getNidNumber() { return nidNumber; }
    public void setNidNumber(String nidNumber) { this.nidNumber = nidNumber; }

    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }

    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getStatus() { return status != null ? status : "ACTIVE"; }
    public void setStatus(String status) { this.status = status; }

    public String getSuspensionReason() { return suspensionReason; }
    public void setSuspensionReason(String suspensionReason) { this.suspensionReason = suspensionReason; }

    public java.time.LocalDateTime getSuspendedAt() { return suspendedAt; }
    public void setSuspendedAt(java.time.LocalDateTime suspendedAt) { this.suspendedAt = suspendedAt; }

    public boolean isReopenRequested() { return reopenRequested != null && reopenRequested; }
    public void setReopenRequested(Boolean reopenRequested) { this.reopenRequested = reopenRequested; }

    public String getReopenReason() { return reopenReason; }
    public void setReopenReason(String reopenReason) { this.reopenReason = reopenReason; }

    public java.time.LocalDateTime getReopenRequestedAt() { return reopenRequestedAt; }
    public void setReopenRequestedAt(java.time.LocalDateTime reopenRequestedAt) { this.reopenRequestedAt = reopenRequestedAt; }

    public java.time.LocalDateTime getCreatedAt() { return createdAt != null ? createdAt : java.time.LocalDateTime.now(); }
    public void setCreatedAt(java.time.LocalDateTime createdAt) { this.createdAt = createdAt; }
}
