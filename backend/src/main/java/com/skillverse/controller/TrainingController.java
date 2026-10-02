package com.skillverse.controller;

import com.skillverse.model.Course;
import com.skillverse.model.CourseEnrollment;
import com.skillverse.model.CourseLesson;
import com.skillverse.service.TrainingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for Academy Training Courses, Lessons, Enrollments, and Progress.
 * Architecture Flow: User/Client -> Controller -> Service -> Repository -> Model (Entity)
 */
@RestController
@RequestMapping("/api/training")
@CrossOrigin(origins = "*")
public class TrainingController {

    private final TrainingService trainingService;

    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    /**
     * Standard list endpoint for all courses
     */
    @GetMapping("/courses")
    public ResponseEntity<List<Course>> getAll() {
        return ResponseEntity.ok(trainingService.getAllCourses());
    }

    /**
     * Standard CRUD: Get Course by ID with PathVariable
     */
    @GetMapping("/courses/{id}")
    public ResponseEntity<Course> getById(@PathVariable Long id) {
        return trainingService.getCourseById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Standard CRUD: Save Course with RequestBody
     */
    @PostMapping("/courses")
    public ResponseEntity<Course> save(@RequestBody Course course) {
        return ResponseEntity.ok(trainingService.saveCourse(course));
    }

    /**
     * Standard CRUD: Update Course with PathVariable and RequestBody
     */
    @PutMapping("/courses/{id}")
    public ResponseEntity<Course> update(@PathVariable Long id, @RequestBody Course details) {
        return trainingService.updateCourse(id, details)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Standard CRUD: Delete Course with PathVariable
     */
    @DeleteMapping("/courses/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (trainingService.deleteCourse(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // --- LESSONS CRUD ENDPOINTS ---

    @GetMapping("/courses/{courseId}/lessons")
    public ResponseEntity<List<CourseLesson>> getLessons(@PathVariable Long courseId) {
        return ResponseEntity.ok(trainingService.getLessons(courseId));
    }

    @PostMapping("/courses/{courseId}/lessons")
    public ResponseEntity<CourseLesson> addLesson(@PathVariable Long courseId, @RequestBody CourseLesson lesson) {
        return ResponseEntity.ok(trainingService.addLesson(courseId, lesson));
    }

    @PutMapping("/lessons/{lessonId}")
    public ResponseEntity<CourseLesson> updateLesson(@PathVariable Long lessonId, @RequestBody CourseLesson details) {
        return trainingService.updateLesson(lessonId, details)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/lessons/{lessonId}")
    public ResponseEntity<?> deleteLesson(@PathVariable Long lessonId) {
        if (trainingService.deleteLesson(lessonId)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // --- ENROLLMENTS & PAYMENTS API ---

    @GetMapping("/enrollments/user/{userId}")
    public ResponseEntity<List<CourseEnrollment>> getUserEnrollments(@PathVariable Long userId) {
        return ResponseEntity.ok(trainingService.getUserEnrollments(userId));
    }

    @GetMapping("/enrollments/all")
    public ResponseEntity<List<CourseEnrollment>> getAllEnrollments() {
        return ResponseEntity.ok(trainingService.getAllEnrollments());
    }

    @PostMapping("/enroll")
    public ResponseEntity<CourseEnrollment> enrollCourse(@RequestBody Map<String, Object> req) {
        Long userId = Long.valueOf(req.get("userId").toString());
        Long courseId = Long.valueOf(req.get("courseId").toString());
        String method = req.get("paymentMethod") != null ? req.get("paymentMethod").toString() : "NONE";
        String status = req.get("paymentStatus") != null ? req.get("paymentStatus").toString() : "FREE";
        String txId = req.get("transactionId") != null ? req.get("transactionId").toString() : "TXN-" + System.currentTimeMillis();
        Double price = req.get("amountPaid") != null ? Double.valueOf(req.get("amountPaid").toString()) : 0.0;

        return ResponseEntity.ok(trainingService.enrollCourse(userId, courseId, method, status, txId, price));
    }

    @PutMapping("/enrollments/{id}/progress")
    public ResponseEntity<CourseEnrollment> updateProgress(@PathVariable Long id, @RequestBody Map<String, Object> req) {
        return trainingService.updateProgress(id, req)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/enrollments/{id}/status")
    public ResponseEntity<CourseEnrollment> updateEnrollmentStatus(@PathVariable Long id, @RequestBody Map<String, Object> req) {
        String status = req.containsKey("paymentStatus") ? req.get("paymentStatus").toString() : "FREE";
        return trainingService.updateEnrollmentStatus(id, status)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user-profile/{userId}")
    public ResponseEntity<Map<String, Object>> getUserAcademyProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(trainingService.getUserAcademyProfile(userId));
    }

    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getAcademyAnalytics() {
        return ResponseEntity.ok(trainingService.getAcademyAnalytics());
    }
}
