package com.skillverse.controller;

import com.skillverse.dto.CourseEnrollmentRequest;
import com.skillverse.model.CourseEnrollment;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/course-enrollments")
@CrossOrigin(origins = "*")
public class CourseEnrollmentController {

    // In-memory list to store course enrollments (No Database)
    private List<CourseEnrollment> enrollmentList = new ArrayList<>();
    private Long nextId = 1L;

    public CourseEnrollmentController() {
        // Sample starter data
        CourseEnrollment e1 = new CourseEnrollment(nextId++, 1L, 1L, "SUCCESSFUL", "BKASH", 1500.0);
        e1.setProgressPercentage(45);
        enrollmentList.add(e1);
    }

    // 1. getAll - uses @RequestParam
    @GetMapping
    public List<CourseEnrollment> getAll(@RequestParam(required = false) Long userId) {
        if (userId == null) {
            return enrollmentList;
        }

        List<CourseEnrollment> result = new ArrayList<>();
        for (CourseEnrollment enrollment : enrollmentList) {
            if (enrollment.getUserId() != null && enrollment.getUserId().equals(userId)) {
                result.add(enrollment);
            }
        }
        return result;
    }

    // 2. getById - uses @PathVariable
    @GetMapping("/{id}")
    public CourseEnrollment getById(@PathVariable Long id) {
        for (CourseEnrollment enrollment : enrollmentList) {
            if (enrollment.getId().equals(id)) {
                return enrollment;
            }
        }
        return null;
    }

    // 3. save - uses @RequestBody with CourseEnrollmentRequest object
    @PostMapping
    public CourseEnrollment save(@RequestBody CourseEnrollmentRequest request) {
        CourseEnrollment enrollment = new CourseEnrollment();
        enrollment.setId(nextId++);
        enrollment.setUserId(request.getUserId());
        enrollment.setCourseId(request.getCourseId());
        enrollment.setPaymentStatus(request.getPaymentStatus() != null ? request.getPaymentStatus() : "SUCCESSFUL");
        enrollment.setPaymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "BKASH");
        enrollment.setTransactionId(request.getTransactionId() != null ? request.getTransactionId() : "TRX" + System.currentTimeMillis() % 100000);
        enrollment.setAmountPaid(request.getAmountPaid() != null ? request.getAmountPaid() : 0.0);
        enrollment.setEnrolledAt(LocalDateTime.now());

        enrollmentList.add(enrollment);
        return enrollment;
    }

    // 4. upload - uses @RequestParam
    @PostMapping("/{id}/upload")
    public CourseEnrollment upload(@PathVariable Long id, @RequestParam String certificateUrl) {
        for (CourseEnrollment enrollment : enrollmentList) {
            if (enrollment.getId().equals(id)) {
                enrollment.setCertificateUrl(certificateUrl);
                enrollment.setIsCompleted(true);
                enrollment.setProgressPercentage(100);
                return enrollment;
            }
        }
        return null;
    }

    // 5. delete - uses @PathVariable
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        for (int i = 0; i < enrollmentList.size(); i++) {
            if (enrollmentList.get(i).getId().equals(id)) {
                enrollmentList.remove(i);
                return "CourseEnrollment deleted successfully";
            }
        }
        return "CourseEnrollment not found";
    }
}
