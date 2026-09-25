package com.skillverse.model;

public class WorkerProfile {

    private Long id;
    private Long userId;
    private String skills; // e.g. "Plumbing, Electrical"
    private Integer experienceYears;
    private String serviceArea;
    private String careerLevel; // Beginner, Bronze, Silver, Gold, Platinum, Master
    private Double hourlyRate;
    private Double basePrice;
    private boolean isAvailable;
    private Double latitude;
    private Double longitude;

    public WorkerProfile() {
        this.basePrice = 300.0;
        this.isAvailable = true;
        this.careerLevel = "Bronze";
    }

    public WorkerProfile(Long id, Long userId, String skills, Integer experienceYears, String serviceArea, String careerLevel, Double hourlyRate) {
        this();
        this.id = id;
        this.userId = userId;
        this.skills = skills;
        this.experienceYears = experienceYears;
        this.serviceArea = serviceArea;
        this.careerLevel = careerLevel;
        this.hourlyRate = hourlyRate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

    public Integer getExperienceYears() { return experienceYears; }
    public void setExperienceYears(Integer experienceYears) { this.experienceYears = experienceYears; }

    public String getServiceArea() { return serviceArea; }
    public void setServiceArea(String serviceArea) { this.serviceArea = serviceArea; }

    public String getCareerLevel() { return careerLevel; }
    public void setCareerLevel(String careerLevel) { this.careerLevel = careerLevel; }

    public Double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(Double hourlyRate) { this.hourlyRate = hourlyRate; }

    public Double getBasePrice() { return basePrice; }
    public void setBasePrice(Double basePrice) { this.basePrice = basePrice; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
}
