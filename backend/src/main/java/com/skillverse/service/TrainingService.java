package com.skillverse.service;

import com.skillverse.model.Course;
import com.skillverse.model.CourseEnrollment;
import com.skillverse.model.CourseLesson;
import com.skillverse.repository.CourseEnrollmentRepository;
import com.skillverse.repository.CourseLessonRepository;
import com.skillverse.repository.CourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class TrainingService {

    private final CourseRepository courseRepository;
    private final CourseLessonRepository lessonRepository;
    private final CourseEnrollmentRepository enrollmentRepository;

    public TrainingService(CourseRepository courseRepository,
                           CourseLessonRepository lessonRepository,
                           CourseEnrollmentRepository enrollmentRepository) {
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    // --- COURSES ---

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    public Course saveCourse(Course course) {
        if (course.getEnrollmentCount() == null) course.setEnrollmentCount(0);
        if (course.getRating() == null) course.setRating(5.0);
        if (course.getIsPublished() == null) course.setIsPublished(true);
        return courseRepository.save(course);
    }

    public Optional<Course> updateCourse(Long id, Course details) {
        return courseRepository.findById(id).map(c -> {
            c.setTitle(details.getTitle());
            c.setDescription(details.getDescription());
            c.setInstructor(details.getInstructor());
            c.setCategory(details.getCategory());
            c.setLevel(details.getLevel());
            c.setDuration(details.getDuration());
            c.setLessonsCount(details.getLessonsCount());
            c.setIsFree(details.getIsFree());
            c.setPrice(details.getPrice());
            c.setImage(details.getImage());
            if (details.getIsPublished() != null) c.setIsPublished(details.getIsPublished());
            return courseRepository.save(c);
        });
    }

    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    // --- LESSONS ---

    public List<CourseLesson> getLessons(Long courseId) {
        return lessonRepository.findByCourseIdOrderByLessonOrderAsc(courseId);
    }

    public Optional<CourseLesson> getLessonById(Long id) {
        return lessonRepository.findById(id);
    }

    public CourseLesson saveLesson(Long courseId, CourseLesson lesson) {
        lesson.setCourseId(courseId);
        return lessonRepository.save(lesson);
    }

    public void deleteLesson(Long lessonId) {
        lessonRepository.deleteById(lessonId);
    }

    // --- ENROLLMENTS ---

    public CourseEnrollment enroll(Long courseId, Long userId) {
        Optional<CourseEnrollment> existing = enrollmentRepository.findByUserIdAndCourseId(userId, courseId);
        if (existing.isPresent()) {
            return existing.get();
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        CourseEnrollment enrollment = new CourseEnrollment(userId, courseId, "FREE", "NONE", null, 0.0);
        enrollment.setProgressPercentage(0);
        enrollment.setCompletedLessonsCount(0);
        enrollment.setIsCompleted(false);
        enrollment.setEnrolledAt(LocalDateTime.now());

        if (course.getEnrollmentCount() == null) course.setEnrollmentCount(0);
        course.setEnrollmentCount(course.getEnrollmentCount() + 1);
        courseRepository.save(course);

        return enrollmentRepository.save(enrollment);
    }

    public Optional<CourseEnrollment> getEnrollment(Long courseId, Long userId) {
        return enrollmentRepository.findByUserIdAndCourseId(userId, courseId);
    }

    public List<CourseEnrollment> getUserEnrollments(Long userId) {
        return enrollmentRepository.findByUserId(userId);
    }

    public Optional<CourseEnrollment> updateProgress(Long courseId, Long userId, Integer completedLessons, Integer score, Boolean certified) {
        return enrollmentRepository.findByUserIdAndCourseId(userId, courseId).map(enrollment -> {
            Optional<Course> courseOpt = courseRepository.findById(courseId);
            int totalLessons = courseOpt.map(Course::getLessonsCount).filter(cnt -> cnt != null && cnt > 0).orElse(5);

            if (completedLessons != null) {
                enrollment.setCompletedLessonsCount(completedLessons);
                int progress = (int) Math.min(100, Math.round(((double) completedLessons / totalLessons) * 100));
                enrollment.setProgressPercentage(progress);
                if (progress >= 100) {
                    enrollment.setIsCompleted(true);
                    enrollment.setCompletedAt(LocalDateTime.now());
                }
            }

            if (Boolean.TRUE.equals(certified)) {
                enrollment.setIsCompleted(true);
                enrollment.setCompletedAt(LocalDateTime.now());
            }

            enrollment.setLastAccessedAt(LocalDateTime.now());
            return enrollmentRepository.save(enrollment);
        });
    }
}
