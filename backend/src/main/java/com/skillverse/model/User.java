package com.skillverse.model;

import java.time.LocalDateTime;

public class User {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String role; // CUSTOMER, WORKER, ADMIN
    private String nidNumber;
    private boolean isVerified;
    private String profilePicture;
    private Double rating;
    private Double latitude;
    private Double longitude;
    private String address;
    private String status;
    private LocalDateTime createdAt;

    public User() {
        this.rating = 5.0;
        this.status = "ACTIVE";
        this.createdAt = LocalDateTime.now();
        this.isVerified = false;
    }

    public User(Long id, String name, String email, String phone, String role, String address) {
        this();
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.address = address;
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

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
