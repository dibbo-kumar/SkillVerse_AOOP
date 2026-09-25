package com.skillverse.dto;

public class UserRequest {

    private String name;
    private String email;
    private String phone;
    private String role;
    private String nidNumber;
    private String address;

    public UserRequest() {}

    public UserRequest(String name, String email, String phone, String role, String address) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.address = address;
    }

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

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
