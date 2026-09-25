package com.skillverse.model;

import java.time.LocalDateTime;

public class Course {

    private Long id;
    private String title;
    private String description;
    private String instructor;
    private String category; // Communication, Electrical, HVAC, Plumbing, Safety, Smart Home
    private String level;    // Beginner, Intermediate, Advanced
    private String duration; // e.g. "6 hours"
    private Integer lessonsCount;
    private Double rating;
    private Integer enrollmentCount;
    private Boolean isFree;
    private Double price;
    private String image;
    private String syllabusDocumentUrl;
    private LocalDateTime createdAt;

    public Course() {
        this.rating = 4.8;
        this.enrollmentCount = 0;
        this.isFree = false;
        this.createdAt = LocalDateTime.now();
    }

    public Course(Long id, String title, String description, String instructor, String category, String level, Double price) {
        this();
        this.id = id;
        this.title = title;
        this.description = description;
        this.instructor = instructor;
        this.category = category;
        this.level = level;
        this.price = price;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getInstructor() { return instructor; }
    public void setInstructor(String instructor) { this.instructor = instructor; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public Integer getLessonsCount() { return lessonsCount; }
    public void setLessonsCount(Integer lessonsCount) { this.lessonsCount = lessonsCount; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public Integer getEnrollmentCount() { return enrollmentCount; }
    public void setEnrollmentCount(Integer enrollmentCount) { this.enrollmentCount = enrollmentCount; }

    public Boolean getIsFree() { return isFree; }
    public void setIsFree(Boolean isFree) { this.isFree = isFree; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getSyllabusDocumentUrl() { return syllabusDocumentUrl; }
    public void setSyllabusDocumentUrl(String syllabusDocumentUrl) { this.syllabusDocumentUrl = syllabusDocumentUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
