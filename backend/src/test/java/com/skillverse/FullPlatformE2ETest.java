package com.skillverse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillverse.dto.*;
import com.skillverse.model.*;
import com.skillverse.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class FullPlatformE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServiceBookingRepository bookingRepository;

    @Autowired
    private ProblemPostRepository problemPostRepository;

    @Autowired
    private ProblemOfferRepository problemOfferRepository;

    @Autowired
    private ToolStoreProductRepository productRepository;

    @Autowired
    private StoreOrderRepository orderRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseEnrollmentRepository enrollmentRepository;

    @Autowired
    private WorkerWalletRepository walletRepository;

    @Autowired
    private VerificationRequestRepository verificationRequestRepository;

    private final ObjectMapper mapper = new ObjectMapper();

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        // Ensure test workers and customer are active and have no blocking active jobs or warranty claims
        List<ServiceBooking> allBookings = bookingRepository.findAll();
        for (ServiceBooking b : allBookings) {
            if (b.getWorker() != null && ("kamrul@gmail.com".equals(b.getWorker().getEmail()) || "rafiq@gmail.com".equals(b.getWorker().getEmail()))) {
                b.setStatus("COMPLETED");
                b.setWarrantyStatus("NONE");
                b.setAdvancePaid(true);
                bookingRepository.save(b);
            }
        }
        userRepository.findByEmail("kamrul@gmail.com").ifPresent(u -> {
            u.setStatus("ACTIVE");
            u.setVerified(true);
            u.setReopenRequested(false);
            u.setReopenReason(null);
            userRepository.save(u);
        });
        userRepository.findByEmail("rafiq@gmail.com").ifPresent(u -> {
            u.setStatus("ACTIVE");
            u.setVerified(true);
            u.setReopenRequested(false);
            u.setReopenReason(null);
            userRepository.save(u);
        });
        userRepository.findByEmail("anis@gmail.com").ifPresent(u -> {
            u.setStatus("ACTIVE");
            u.setVerified(true);
            u.setReopenRequested(false);
            u.setReopenReason(null);
            userRepository.save(u);
        });
    }

    @Test
    @DisplayName("CUSTOMER WORKFLOW: Search, Book, Advance Pay, Start OTP, Complete Pay, Review, Warranty Claim")
    void testCustomerEndToEndLifecycle() throws Exception {
        User customer = userRepository.findByEmail("anis@gmail.com").orElseThrow();
        User worker = userRepository.findByEmail("kamrul@gmail.com").orElseThrow();

        // 1. Customer searches nearby workers
        mockMvc.perform(get("/api/workers/nearby")
                        .param("category", "AC Repair")
                        .param("lat", "23.8759")
                        .param("lon", "90.3795")
                        .param("radius", "10.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // 2. Customer asks AI Diagnostic Assistant
        mockMvc.perform(get("/api/ai/estimate-cost")
                        .param("issueDescription", "AC is not cooling properly and making vibration noise"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.baseServiceCost").exists())
                .andExpect(jsonPath("$.totalEstimatedCost").exists());

        // 3. Customer Books a Service
        BookingRequest bookingReq = new BookingRequest();
        bookingReq.setCustomerId(customer.getId());
        bookingReq.setWorkerId(worker.getId());
        bookingReq.setServiceType("HVAC & AC");
        bookingReq.setEstimatedCost(1500.0);
        bookingReq.setDescription("Master AC inspection & deep chemical wash [Location: Sector 12, Uttara, Dhaka]");
        bookingReq.setAddress("House 14, Road 4, Sector 12, Uttara, Dhaka");

        String bookingRes = mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.startVerificationCode").exists())
                .andReturn().getResponse().getContentAsString();

        JsonNode bookingNode = mapper.readTree(bookingRes);
        long bookingId = bookingNode.get("id").asLong();
        String startOtp = bookingNode.get("startVerificationCode").asText();

        // 4. Worker accepts price
        mockMvc.perform(put("/api/bookings/" + bookingId + "/accept-price")
                        .param("acceptedBy", "WORKER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("AWAITING_ADVANCE"));

        // 5. Customer pays base advance fee
        String advancePayload = "{\"paymentMethod\":\"bKash\",\"mobileNumber\":\"01811223344\",\"amount\":300.0}";
        mockMvc.perform(post("/api/bookings/" + bookingId + "/pay-advance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(advancePayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.advancePaid").value(true));

        // 6. Worker marks On The Way -> Arrived
        mockMvc.perform(put("/api/bookings/" + bookingId + "/on-the-way"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ON_THE_WAY"));

        mockMvc.perform(put("/api/bookings/" + bookingId + "/arrived"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ARRIVED"));

        // 7. Verify Start OTP (Starts work)
        mockMvc.perform(put("/api/bookings/" + bookingId + "/verify-start-otp")
                        .param("otp", startOtp))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.startOtpVerified").value(true));

        // 8. Worker uploads before & after photos
        String photosPayload = "{\"beforePhoto\":\"https://img.com/before.jpg\",\"afterPhoto\":\"https://img.com/after.jpg\"}";
        mockMvc.perform(put("/api/bookings/" + bookingId + "/upload-photos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(photosPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.afterPhoto").value("https://img.com/after.jpg"));

        // 9. Worker requests completion
        mockMvc.perform(put("/api/bookings/" + bookingId + "/request-completion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETION_REQUESTED"));

        // 10. Customer settles final payment (Direct completion)
        String payPayload = "{\"paymentMethod\":\"bKash\",\"mobileNumber\":\"01811223344\",\"finalAmount\":1500.0}";
        mockMvc.perform(post("/api/bookings/" + bookingId + "/pay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.paymentStatus").value("PAID"));

        // 11. Customer submits Review
        String reviewPayload = "{\"rating\":5,\"comment\":\"Excellent work, AC cooling like brand new!\"}";
        mockMvc.perform(post("/api/bookings/" + bookingId + "/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reviewPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewRating").value(5));

        // 12. Customer claims 30-day warranty
        String claimPayload = "{\"description\":\"Minor water drip from drainage pipe.\" }";
        mockMvc.perform(post("/api/bookings/" + bookingId + "/claim-warranty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(claimPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.warrantyStatus").value("WARRANTY_CLAIMED"));

        // 13. Worker accepts warranty & completes
        mockMvc.perform(put("/api/bookings/" + bookingId + "/accept-warranty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.warrantyStatus").value("WARRANTY_ACCEPTED"));

        mockMvc.perform(put("/api/bookings/" + bookingId + "/complete-warranty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.warrantyStatus").value("WARRANTY_COMPLETED"));

    }

    @Test
    @DisplayName("PROBLEM POSTING WORKFLOW: Customer Posts, Worker Quotes, Accept -> Converge to ServiceBooking")
    void testProblemPostingAndBiddingFlow() throws Exception {
        User customer = userRepository.findByEmail("anis@gmail.com").orElseThrow();
        User worker = userRepository.findByEmail("kamrul@gmail.com").orElseThrow();

        // 1. Customer creates problem post
        String postPayload = String.format(
                "{\"customerId\":%d,\"serviceCategory\":\"Plumbing\",\"title\":\"Water tap leak under sink\",\"description\":\"Kitchen tap dripping\",\"budgetPrice\":600.0}",
                customer.getId()
        );
        String postRes = mockMvc.perform(post("/api/problems")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andReturn().getResponse().getContentAsString();

        long problemId = mapper.readTree(postRes).get("id").asLong();

        // 2. Worker submits offer
        String offerPayload = String.format(
                "{\"workerId\":%d,\"proposedPrice\":650.0,\"message\":\"I have Teflon tape and replacement washer ready.\",\"estimatedArrival\":\"Within 1 hour\"}",
                worker.getId()
        );
        String offerRes = mockMvc.perform(post("/api/problems/" + problemId + "/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(offerPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.proposedPrice").value(650.0))
                .andReturn().getResponse().getContentAsString();

        long offerId = mapper.readTree(offerRes).get("id").asLong();

        // 3. Customer accepts offer -> creates ServiceBooking
        String acceptRes = mockMvc.perform(put("/api/problems/offers/" + offerId + "/accept"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.bookingSource").value("POSTED_PROBLEM"))
                .andExpect(jsonPath("$.agreedCost").value(650.0))
                .andReturn().getResponse().getContentAsString();

        long createdBookingId = mapper.readTree(acceptRes).get("id").asLong();

        // Verify problem post status changed to ASSIGNED
        ProblemPost updatedPost = problemPostRepository.findById(problemId).orElseThrow();
        assertThat(updatedPost.getStatus()).isEqualTo("ASSIGNED");

    }

    @Test
    @DisplayName("WORKER VERIFICATION & ADMIN DOSSIER WORKFLOW")
    void testWorkerVerificationWorkflow() throws Exception {
        // Create fresh worker
        long ts = System.currentTimeMillis();
        String testPhone = "018" + (ts % 100000000L);
        User testWorker = new User("Test Worker Pro", "test_worker_" + ts + "@skillverse.com", testPhone, "WORKER");
        testWorker.setVerified(false);
        testWorker.setStatus("UNVERIFIED");
        User savedWorker = userRepository.save(testWorker);

        // 1. Send simulated OTP
        String otpRes = mockMvc.perform(post("/api/verification/send-phone-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"phone\":\"%s\"}", testPhone)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.simulatedOtp").exists())
                .andReturn().getResponse().getContentAsString();

        String simulatedOtp = mapper.readTree(otpRes).get("simulatedOtp").asText();

        // 2. Verify OTP
        mockMvc.perform(post("/api/verification/verify-phone-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"phone\":\"%s\",\"otp\":\"%s\"}", testPhone, simulatedOtp)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verified").value(true));

        // 3. Submit 4-step Verification Dossier
        VerificationSubmissionDto dto = new VerificationSubmissionDto();
        dto.setUserId(savedWorker.getId());
        dto.setFullName("Test Worker Pro");
        dto.setDateOfBirth("1995-05-15");
        dto.setPhone(testPhone);
        dto.setPhoneVerified(true);
        dto.setNidNumber("8899776655");
        dto.setPresentAddress("House 5, Road 2, Mirpur 10, Dhaka");
        dto.setDivision("Dhaka");
        dto.setDistrict("Dhaka");
        dto.setSkills("Electrical, AC Repair");
        dto.setExperienceYears(4);
        dto.setPayoutMethod("bKash");
        dto.setPayoutAccount("01799887766");

        String submitRes = mockMvc.perform(post("/api/verification/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn().getResponse().getContentAsString();

        long verifId = mapper.readTree(submitRes).get("id").asLong();

        // 4. Admin requests correction
        mockMvc.perform(put("/api/admin/verification/" + verifId + "/decision")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"decision\":\"CORRECTION_REQUIRED\",\"reason\":\"Please re-upload clear NID front photo.\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CORRECTION_REQUIRED"));

        // 5. Admin Approves Dossier
        mockMvc.perform(put("/api/admin/verification/" + verifId + "/decision")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"decision\":\"APPROVED\",\"reason\":\"All documents verified.\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));

        // Verify User in DB is now verified and active
        User verifiedWorker = userRepository.findById(savedWorker.getId()).orElseThrow();
        assertThat(verifiedWorker.isVerified()).isTrue();
        assertThat(verifiedWorker.getStatus()).isEqualTo("ACTIVE");

    }

    @Test
    @DisplayName("TOOL STORE & ACADEMY WORKFLOW: Orders, Inventory, Course Enrollments")
    void testStoreAndAcademyFlow() throws Exception {
        User customer = userRepository.findByEmail("anis@gmail.com").orElseThrow();
        ToolStoreProduct product = productRepository.findAll().get(0);
        int initialStock = product.getStockQuantity();

        // 1. Place Store Order
        OrderRequestDTO orderDto = new OrderRequestDTO();
        orderDto.setUserId(customer.getId());
        orderDto.setCustomerName("Anisur Rahman");
        orderDto.setPhone("01811223344");
        orderDto.setAddress("House 14, Road 4, Sector 12, Uttara, Dhaka");
        orderDto.setPaymentMethod("CASH_ON_DELIVERY");
        orderDto.setItems(List.of(new CartItemDTO(product.getId(), 2)));

        String orderRes = mockMvc.perform(post("/api/store/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(orderDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("ORDER_PLACED"))
                .andExpect(jsonPath("$.orderNumber").exists())
                .andReturn().getResponse().getContentAsString();

        long orderId = mapper.readTree(orderRes).get("id").asLong();

        // Verify stock deducted
        ToolStoreProduct updatedProduct = productRepository.findById(product.getId()).orElseThrow();
        assertThat(updatedProduct.getStockQuantity()).isEqualTo(initialStock - 2);

        // 2. Academy Course Enrollment
        Course course = courseRepository.findAll().get(0);
        String enrollPayload = String.format(
                "{\"userId\":%d,\"courseId\":%d,\"paymentMethod\":\"bKash\",\"paymentStatus\":\"SUCCESSFUL\",\"amountPaid\":500.0}",
                customer.getId(), course.getId()
        );

        String enrollRes = mockMvc.perform(post("/api/training/enroll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(enrollPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentStatus").value("SUCCESSFUL"))
                .andReturn().getResponse().getContentAsString();

        long enrollId = mapper.readTree(enrollRes).get("id").asLong();

        // 3. Update Course Progress
        mockMvc.perform(put("/api/training/enrollments/" + enrollId + "/progress")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"progressPercentage\":75,\"completedCount\":3,\"isCompleted\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.progressPercentage").value(75));

        // Restore stock
        product.setStockQuantity(initialStock);
        productRepository.save(product);
    }

    @Test
    @DisplayName("ADMIN PORTAL WORKFLOW: Overview, Finance, User Controls, Settings")
    void testAdminManagementFeatures() throws Exception {
        // 1. Overview metrics
        mockMvc.perform(get("/api/admin/overview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsers").exists())
                .andExpect(jsonPath("$.totalRevenue").exists())
                .andExpect(jsonPath("$.platformRevenue").exists());

        // 2. Finance overview
        mockMvc.perform(get("/api/admin/finance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalServiceVolume").exists())
                .andExpect(jsonPath("$.platformCommission").exists());

        // 3. User directory
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // 4. Update Platform Settings
        mockMvc.perform(put("/api/admin/settings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"platform_commission\":\"7\",\"emergency_hotline\":\"+880 1711 000000\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());

        // Reset setting
        mockMvc.perform(put("/api/admin/settings")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"platform_commission\":\"5\"}"));
    }

    @Test
    @DisplayName("SUSPENSION & APPEAL WORKFLOW: Admin Suspends -> Actions Blocked -> User Submits Appeal -> Admin Unsuspends")
    void testAccountSuspensionAppealAndReactivationFlow() throws Exception {
        User customer = userRepository.findByEmail("anis@gmail.com").orElseThrow();
        User worker = userRepository.findByEmail("kamrul@gmail.com").orElseThrow();

        // 1. Admin suspends customer account
        mockMvc.perform(put("/api/admin/users/" + customer.getId() + "/status")
                        .param("status", "SUSPENDED")
                        .param("reason", "Suspicious booking activities detected."))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.status").value("SUSPENDED"));

        // 2. Suspended customer attempts to create a booking -> MUST be blocked
        BookingRequest bookingReq = new BookingRequest();
        bookingReq.setCustomerId(customer.getId());
        bookingReq.setWorkerId(worker.getId());
        bookingReq.setServiceType("Electrical");
        bookingReq.setEstimatedCost(1200.0);
        bookingReq.setDescription("Emergency wiring repair");
        bookingReq.setAddress("Uttara Sector 12");

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.containsString("suspended")));

        // 3. Customer submits an Appeal to reopen their account
        String appealPayload = "{\"userId\":" + customer.getId() + ",\"reason\":\"I apologize for any misunderstanding. All my requests are genuine and verified.\"}";
        mockMvc.perform(post("/api/auth/appeal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(appealPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.reopenRequested").value(true))
                .andExpect(jsonPath("$.user.reopenReason").value(org.hamcrest.Matchers.containsString("genuine and verified")));

        // 4. Admin checks the Reopen Appeals list -> Customer appeal appears
        mockMvc.perform(get("/api/admin/users/reopen-requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[?(@.id == " + customer.getId() + ")]").exists());

        // 5. Admin reviews appeal and Unsuspends customer
        mockMvc.perform(put("/api/admin/users/" + customer.getId() + "/unsuspend"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.status").value("ACTIVE"))
                .andExpect(jsonPath("$.user.reopenRequested").value(false));

        // 6. Restored customer can now successfully create bookings
        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("CUSTOMER SCHEDULING: Slot Selection, Busy Conflict Blocking & 15-Minute Auto-Expiry")
    void testCustomerSchedulingSlotConflictAndAutoExpiryWorkflow() throws Exception {
        User customer = userRepository.findByEmail("anis@gmail.com").orElseThrow();
        User worker = userRepository.findByEmail("kamrul@gmail.com").orElseThrow();

        String testDate = "2026-10-15";
        String testSlot = "10:00 AM - 12:00 PM";

        // 1. Customer books worker for specific date & time slot
        BookingRequest bookingReq1 = new BookingRequest();
        bookingReq1.setCustomerId(customer.getId());
        bookingReq1.setWorkerId(worker.getId());
        bookingReq1.setServiceType("Plumbing");
        bookingReq1.setEstimatedCost(1000.0);
        bookingReq1.setPreferredDate(testDate);
        bookingReq1.setPreferredTime(testSlot);
        bookingReq1.setDescription("Kitchen pipe repair");
        bookingReq1.setAddress("Dhanmondi 27");

        String res1 = mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingReq1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.preferredDate").value(testDate))
                .andExpect(jsonPath("$.preferredTime").value(testSlot))
                .andReturn().getResponse().getContentAsString();

        Long booking1Id = mapper.readTree(res1).get("id").asLong();

        // 2. Query booked slots endpoint for this worker on this date
        mockMvc.perform(get("/api/bookings/worker/" + worker.getId() + "/booked-slots")
                        .param("date", testDate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").value(testSlot));

        // 3. Another booking attempt for the SAME worker on the SAME date & slot -> MUST be blocked with 400
        BookingRequest bookingReq2 = new BookingRequest();
        bookingReq2.setCustomerId(customer.getId());
        bookingReq2.setWorkerId(worker.getId());
        bookingReq2.setServiceType("Plumbing");
        bookingReq2.setEstimatedCost(1200.0);
        bookingReq2.setPreferredDate(testDate);
        bookingReq2.setPreferredTime(testSlot);
        bookingReq2.setDescription("Bathroom leakage repair");
        bookingReq2.setAddress("Gulshan 2");

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingReq2)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.containsString("busy in this time slot")));

        // 4. Test 15-Minute Auto-Expiry: Set createdAt to 20 minutes ago and trigger autoExpirePendingBookings
        ServiceBooking b1 = bookingRepository.findById(booking1Id).orElseThrow();
        b1.setCreatedAt(java.time.LocalDateTime.now().minusMinutes(20));
        bookingRepository.save(b1);

        // Fetching customer bookings or worker bookings will trigger autoExpirePendingBookings
        mockMvc.perform(get("/api/bookings/customer/" + customer.getId()))
                .andExpect(status().isOk());

        ServiceBooking b1Expired = bookingRepository.findById(booking1Id).orElseThrow();
        org.junit.jupiter.api.Assertions.assertEquals("CANCELLED", b1Expired.getStatus());

        // 5. Now that the expired booking is CANCELLED, the slot becomes free again!
        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingReq2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.preferredDate").value(testDate))
                .andExpect(jsonPath("$.preferredTime").value(testSlot));
    }
}
