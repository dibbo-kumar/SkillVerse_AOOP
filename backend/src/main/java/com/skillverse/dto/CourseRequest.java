package com.skillverse.dto;

public class CourseRequest {

    private String title;
    private String description;
    private String instructor;
    private String category;
    private String level;
    private String duration;
    private Integer lessonsCount;
    private Double price;
    private Boolean isFree;

    public CourseRequest() {}

    public CourseRequest(String title, String description, String instructor, String category, String level, Double price) {
        this.title = title;
        this.description = description;
        this.instructor = instructor;
        this.category = category;
        this.level = level;
        this.price = price;
    }

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

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Boolean getIsFree() { return isFree; }
    public void setIsFree(Boolean isFree) { this.isFree = isFree; }
}
