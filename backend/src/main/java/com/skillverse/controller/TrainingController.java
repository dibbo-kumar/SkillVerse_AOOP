package com.skillverse.controller;

import com.skillverse.model.Course;
import com.skillverse.model.CourseEnrollment;
import com.skillverse.model.CourseLesson;
import com.skillverse.service.TrainingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/training")
@CrossOrigin(origins = "*")
public class TrainingController {

    private final TrainingService trainingService;

    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    // --- COURSES CRUD ---

    @GetMapping("/courses")
    public ResponseEntity<List<Course>> getCourses() {
        return ResponseEntity.ok(trainingService.getAllCourses());
    }

    @GetMapping("/courses/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        return trainingService.getCourseById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/courses")
    public ResponseEntity<Course> saveCourse(@RequestBody Course course) {
        return ResponseEntity.ok(trainingService.saveCourse(course));
    }

    @PutMapping("/courses/{id}")
    public ResponseEntity<Course> updateCourse(@PathVariable Long id, @RequestBody Course details) {
        return trainingService.updateCourse(id, details)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/courses/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable Long id) {
        trainingService.deleteCourse(id);
        return ResponseEntity.ok().build();
    }

    // --- LESSONS API ---

    @GetMapping("/courses/{courseId}/lessons")
    public ResponseEntity<List<CourseLesson>> getLessons(@PathVariable Long courseId) {
        return ResponseEntity.ok(trainingService.getLessons(courseId));
    }

    @PostMapping("/courses/{courseId}/lessons")
    public ResponseEntity<CourseLesson> saveLesson(@PathVariable Long courseId, @RequestBody CourseLesson lesson) {
        try {
            return ResponseEntity.ok(trainingService.saveLesson(courseId, lesson));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/lessons/{lessonId}")
    public ResponseEntity<?> deleteLesson(@PathVariable Long lessonId) {
        trainingService.deleteLesson(lessonId);
        return ResponseEntity.ok().build();
    }

    // --- ENROLLMENTS & PROGRESS API ---

    @PostMapping("/courses/{courseId}/enroll")
    public ResponseEntity<?> enroll(@PathVariable Long courseId, @RequestParam Long userId) {
        try {
            CourseEnrollment enrollment = trainingService.enroll(courseId, userId);
            return ResponseEntity.ok(enrollment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/courses/{courseId}/enrollment")
    public ResponseEntity<?> getEnrollment(@PathVariable Long courseId, @RequestParam Long userId) {
        return trainingService.getEnrollment(courseId, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/enrollments/user/{userId}")
    public ResponseEntity<List<CourseEnrollment>> getUserEnrollments(@PathVariable Long userId) {
        return ResponseEntity.ok(trainingService.getUserEnrollments(userId));
    }

    @PutMapping("/courses/{courseId}/progress")
    public ResponseEntity<?> updateProgress(
            @PathVariable Long courseId,
            @RequestParam Long userId,
            @RequestBody Map<String, Object> payload) {
        Integer completedLessons = payload.containsKey("completedLessons") ? (Integer) payload.get("completedLessons") : null;
        Integer score = payload.containsKey("score") ? (Integer) payload.get("score") : null;
        Boolean certified = payload.containsKey("certified") ? (Boolean) payload.get("certified") : false;

        return trainingService.updateProgress(courseId, userId, completedLessons, score, certified)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
