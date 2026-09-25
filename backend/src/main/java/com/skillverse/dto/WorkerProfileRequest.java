package com.skillverse.dto;

public class WorkerProfileRequest {

    private Long userId;
    private String skills;
    private Integer experienceYears;
    private String serviceArea;
    private String careerLevel;
    private Double hourlyRate;
    private Double basePrice;

    public WorkerProfileRequest() {}

    public WorkerProfileRequest(Long userId, String skills, Integer experienceYears, String serviceArea, String careerLevel, Double hourlyRate, Double basePrice) {
        this.userId = userId;
        this.skills = skills;
        this.experienceYears = experienceYears;
        this.serviceArea = serviceArea;
        this.careerLevel = careerLevel;
        this.hourlyRate = hourlyRate;
        this.basePrice = basePrice;
    }

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
}
