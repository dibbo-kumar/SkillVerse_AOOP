package com.skillverse.controller;

import com.skillverse.dto.CourseRequest;
import com.skillverse.model.Course;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "*")
public class CourseController {

    // In-memory list to store courses (No Database)
    private List<Course> courseList = new ArrayList<>();
    private Long nextId = 1L;

    public CourseController() {
        // Sample starter data
        Course c1 = new Course(nextId++, "Mastering Household Electrical Safety", "Comprehensive electrical installation and diagnostics course.", "Engr. Mahmudul Hasan", "Electrical", "Intermediate", 1500.0);
        c1.setDuration("8 hours");
        c1.setLessonsCount(12);
        c1.setImage("https://images.unsplash.com/photo-1621905251189-08b45d6a269e?w=800");

        Course c2 = new Course(nextId++, "Advanced Inverter AC Servicing", "Complete PCB circuit & refrigerant charging guide.", "Tanvir Chowdhury", "HVAC", "Advanced", 2200.0);
        c2.setDuration("10 hours");
        c2.setLessonsCount(15);
        c2.setImage("https://images.unsplash.com/photo-1581092160607-ee22621dd758?w=800");

        courseList.add(c1);
        courseList.add(c2);
    }

    // 1. getAll - uses @RequestParam
    @GetMapping
    public List<Course> getAll(@RequestParam(required = false) String category) {
        if (category == null || category.isEmpty()) {
            return courseList;
        }

        List<Course> result = new ArrayList<>();
        for (Course course : courseList) {
            if (course.getCategory() != null && course.getCategory().equalsIgnoreCase(category)) {
                result.add(course);
            }
        }
        return result;
    }

    // 2. getById - uses @PathVariable
    @GetMapping("/{id}")
    public Course getById(@PathVariable Long id) {
        for (Course course : courseList) {
            if (course.getId().equals(id)) {
                return course;
            }
        }
        return null;
    }

    // 3. save - uses @RequestBody with CourseRequest object
    @PostMapping
    public Course save(@RequestBody CourseRequest request) {
        Course course = new Course();
        course.setId(nextId++);
        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setInstructor(request.getInstructor());
        course.setCategory(request.getCategory());
        course.setLevel(request.getLevel());
        course.setDuration(request.getDuration());
        course.setLessonsCount(request.getLessonsCount());
        course.setPrice(request.getPrice());
        course.setIsFree(request.getIsFree() != null ? request.getIsFree() : false);

        courseList.add(course);
        return course;
    }

    // 4. upload - uses @RequestParam
    @PostMapping("/{id}/upload")
    public Course upload(@PathVariable Long id, @RequestParam String syllabusDocumentUrl) {
        for (Course course : courseList) {
            if (course.getId().equals(id)) {
                course.setSyllabusDocumentUrl(syllabusDocumentUrl);
                return course;
            }
        }
        return null;
    }

    // 5. delete - uses @PathVariable
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        for (int i = 0; i < courseList.size(); i++) {
            if (courseList.get(i).getId().equals(id)) {
                courseList.remove(i);
                return "Course deleted successfully";
            }
        }
        return "Course not found";
    }
}
