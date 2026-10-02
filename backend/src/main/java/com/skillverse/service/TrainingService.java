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
    private final com.skillverse.repository.UserRepository userRepository;

    public TrainingService(CourseRepository courseRepository,
                           CourseLessonRepository lessonRepository,
                           CourseEnrollmentRepository enrollmentRepository,
                           com.skillverse.repository.UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    public Course saveCourse(Course course) {
        if (course.getEnrollmentCount() == null) course.setEnrollmentCount(0);
        if (course.getRating() == null) course.setRating(5.0);
        if (course.getLessonsCount() == null) course.setLessonsCount(0);
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
            c.setRating(details.getRating());
            c.setEnrollmentCount(details.getEnrollmentCount());
            c.setIsFree(details.getIsFree());
            c.setPrice(details.getPrice());
            c.setImage(details.getImage());
            c.setIsPublished(details.getIsPublished());
            c.setLanguage(details.getLanguage());
            c.setCertificateAvailable(details.getCertificateAvailable());
            c.setWhatYouWillLearn(details.getWhatYouWillLearn());
            c.setUpdatedAt(LocalDateTime.now());
            return courseRepository.save(c);
        });
    }

    public boolean deleteCourse(Long id) {
        if (courseRepository.existsById(id)) {
            lessonRepository.findByCourseIdOrderByLessonOrderAsc(id)
                    .forEach(l -> lessonRepository.deleteById(l.getId()));
            courseRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<CourseLesson> getLessons(Long courseId) {
        return lessonRepository.findByCourseIdOrderByLessonOrderAsc(courseId);
    }

    public CourseLesson addLesson(Long courseId, CourseLesson lesson) {
        lesson.setCourseId(courseId);
        CourseLesson saved = lessonRepository.save(lesson);
        courseRepository.findById(courseId).ifPresent(c -> {
            c.setLessonsCount(lessonRepository.findByCourseIdOrderByLessonOrderAsc(courseId).size());
            courseRepository.save(c);
        });
        return saved;
    }

    public Optional<CourseLesson> updateLesson(Long lessonId, CourseLesson details) {
        return lessonRepository.findById(lessonId).map(l -> {
            l.setModuleTitle(details.getModuleTitle());
            l.setLessonTitle(details.getLessonTitle());
            l.setDescription(details.getDescription());
            l.setYoutubeVideoId(details.getYoutubeVideoId());
            l.setDuration(details.getDuration());
            l.setLessonOrder(details.getLessonOrder());
            if (details.getIsFreePreview() != null) l.setIsFreePreview(details.getIsFreePreview());
            return lessonRepository.save(l);
        });
    }

    public boolean deleteLesson(Long lessonId) {
        Optional<CourseLesson> lessonOpt = lessonRepository.findById(lessonId);
        if (lessonOpt.isPresent()) {
            Long cId = lessonOpt.get().getCourseId();
            lessonRepository.deleteById(lessonId);
            courseRepository.findById(cId).ifPresent(c -> {
                c.setLessonsCount(lessonRepository.findByCourseIdOrderByLessonOrderAsc(cId).size());
                courseRepository.save(c);
            });
            return true;
        }
        return false;
    }

    public List<CourseEnrollment> getUserEnrollments(Long userId) {
        return enrollmentRepository.findByUserId(userId);
    }

    public List<CourseEnrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    public CourseEnrollment enrollCourse(Long userId, Long courseId, String method, String status, String txId, Double price) {
        if (userId != null) {
            userRepository.findById(userId).ifPresent(u -> {
                if ("SUSPENDED".equalsIgnoreCase(u.getStatus())) {
                    throw new IllegalStateException("Your account has been suspended by Administrator. You cannot enroll in courses while suspended.");
                }
            });
        }
        Optional<CourseEnrollment> existing = enrollmentRepository.findByUserIdAndCourseId(userId, courseId);
        CourseEnrollment enrollment;
        if (existing.isPresent()) {
            enrollment = existing.get();
            enrollment.setPaymentStatus(status);
            enrollment.setPaymentMethod(method);
            enrollment.setTransactionId(txId);
            enrollment.setAmountPaid(price);
            enrollment.setLastAccessedAt(LocalDateTime.now());
        } else {
            enrollment = new CourseEnrollment(userId, courseId, status, method, txId, price);
        }
        CourseEnrollment saved = enrollmentRepository.save(enrollment);

        if ("SUCCESSFUL".equalsIgnoreCase(status) || "FREE".equalsIgnoreCase(status)) {
            courseRepository.findById(courseId).ifPresent(c -> {
                c.setEnrollmentCount((c.getEnrollmentCount() != null ? c.getEnrollmentCount() : 0) + 1);
                courseRepository.save(c);
            });
        }

        return saved;
    }

    public Optional<CourseEnrollment> updateProgress(Long id, Map<String, Object> req) {
        return enrollmentRepository.findById(id).map(e -> {
            if (req.containsKey("completedCount")) {
                e.setCompletedLessonsCount(Integer.parseInt(req.get("completedCount").toString()));
            }
            if (req.containsKey("progressPercentage")) {
                e.setProgressPercentage(Integer.parseInt(req.get("progressPercentage").toString()));
            }
            if (req.containsKey("lastWatchedLessonId")) {
                e.setLastWatchedLessonId(Long.parseLong(req.get("lastWatchedLessonId").toString()));
            }
            if (req.containsKey("completedLessonIds")) {
                e.setCompletedLessonIds(req.get("completedLessonIds").toString());
            }
            if (req.containsKey("isCompleted")) {
                boolean comp = Boolean.parseBoolean(req.get("isCompleted").toString());
                e.setIsCompleted(comp);
                if (comp && e.getCompletedAt() == null) {
                    e.setCompletedAt(LocalDateTime.now());
                }
            }
            e.setLastAccessedAt(LocalDateTime.now());
            return enrollmentRepository.save(e);
        });
    }

    public Optional<CourseEnrollment> updateEnrollmentStatus(Long id, String paymentStatus) {
        return enrollmentRepository.findById(id).map(e -> {
            e.setPaymentStatus(paymentStatus);
            return enrollmentRepository.save(e);
        });
    }

    public Map<String, Object> getUserAcademyProfile(Long userId) {
        List<CourseEnrollment> userEnrollments = enrollmentRepository.findByUserId(userId);
        long totalEnrolled = userEnrollments.size();
        long completed = userEnrollments.stream().filter(e -> Boolean.TRUE.equals(e.getIsCompleted())).count();
        long learning = userEnrollments.stream().filter(e -> !Boolean.TRUE.equals(e.getIsCompleted())).count();
        double totalSpent = userEnrollments.stream()
                .filter(e -> "SUCCESSFUL".equalsIgnoreCase(e.getPaymentStatus()))
                .mapToDouble(e -> e.getAmountPaid() != null ? e.getAmountPaid() : 0.0)
                .sum();

        Map<String, Object> res = new HashMap<>();
        res.put("userId", userId);
        res.put("coursesEnrolled", totalEnrolled);
        res.put("coursesCompleted", completed);
        res.put("currentlyLearning", learning);
        res.put("totalAmountSpent", totalSpent);
        res.put("enrollments", userEnrollments);

        return res;
    }

    public Map<String, Object> getAcademyAnalytics() {
        List<Course> courses = courseRepository.findAll();
        List<CourseEnrollment> enrollments = enrollmentRepository.findAll();

        long totalCourses = courses.size();
        long totalEnrollments = enrollments.size();
        long activeLearners = enrollments.stream().map(CourseEnrollment::getUserId).distinct().count();
        long completedCourses = enrollments.stream().filter(e -> Boolean.TRUE.equals(e.getIsCompleted())).count();

        double totalRevenue = enrollments.stream()
                .filter(e -> "SUCCESSFUL".equalsIgnoreCase(e.getPaymentStatus()))
                .mapToDouble(e -> e.getAmountPaid() != null ? e.getAmountPaid() : 0.0)
                .sum();

        Map<String, Object> res = new HashMap<>();
        res.put("totalCourses", totalCourses);
        res.put("totalEnrollments", totalEnrollments);
        res.put("activeLearners", activeLearners);
        res.put("completedCourses", completedCourses);
        res.put("totalRevenue", totalRevenue);

        return res;
    }
}
