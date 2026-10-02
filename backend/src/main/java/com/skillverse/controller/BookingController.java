package com.skillverse.controller;

import com.skillverse.dto.BookingRequest;
import com.skillverse.model.ServiceBooking;
import com.skillverse.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for Service Bookings and Lifecycle Management.
 * Architecture Flow: User/Client -> Controller -> Service -> Repository -> Model (Entity)
 */
@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    /**
     * Standard CRUD: Save / Create booking with RequestBody
     */
    @PostMapping
    public ResponseEntity<?> save(@RequestBody BookingRequest request) {
        try {
            ServiceBooking saved = bookingService.save(request);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Standard CRUD: Get Booking by ID with PathVariable
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return bookingService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Standard CRUD: Update Booking with PathVariable and RequestBody
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody BookingRequest request) {
        return bookingService.update(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Standard CRUD: Delete Booking with PathVariable
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (bookingService.delete(id)) {
            return ResponseEntity.ok(Map.of("message", "Booking #" + id + " successfully removed."));
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Get Customer Bookings with PathVariable
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<ServiceBooking>> getCustomerBookings(@PathVariable Long customerId) {
        return ResponseEntity.ok(bookingService.getCustomerBookings(customerId));
    }

    /**
     * Get Worker Bookings with PathVariable
     */
    @GetMapping("/worker/{workerId}")
    public ResponseEntity<List<ServiceBooking>> getWorkerBookings(@PathVariable Long workerId) {
        return ResponseEntity.ok(bookingService.getWorkerBookings(workerId));
    }

    /**
     * Accept price deal with PathVariable and RequestParam
     */
    @PutMapping("/{id}/accept-price")
    public ResponseEntity<?> acceptPrice(@PathVariable Long id,
                                         @RequestParam(required = false, defaultValue = "CUSTOMER") String acceptedBy) {
        try {
            return ResponseEntity.ok(bookingService.acceptPrice(id, acceptedBy));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Negotiate counter offer with PathVariable and RequestParam
     */
    @PutMapping("/{id}/counter-offer")
    public ResponseEntity<?> counterOffer(@PathVariable Long id,
                                          @RequestParam Double price,
                                          @RequestParam(required = false, defaultValue = "WORKER") String offeredBy,
                                          @RequestParam(required = false) String status) {
        try {
            return ResponseEntity.ok(bookingService.counterOffer(id, price, offeredBy, status));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Pay advance with PathVariable and RequestBody
     */
    @PostMapping("/{id}/pay-advance")
    public ResponseEntity<?> payAdvance(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        try {
            return ResponseEntity.ok(bookingService.payAdvance(id, payload));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Arrival timeout refund with PathVariable and RequestBody
     */
    @PostMapping("/{id}/timeout-refund")
    public ResponseEntity<?> timeoutRefund(@PathVariable Long id, @RequestBody(required = false) Map<String, Object> payload) {
        try {
            return ResponseEntity.ok(bookingService.timeoutRefund(id, payload));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Update booking status with PathVariable and RequestParam
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            return ResponseEntity.ok(bookingService.updateStatus(id, status));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Technician starts journey with PathVariable
     */
    @PutMapping("/{id}/on-the-way")
    public ResponseEntity<?> setOnTheWay(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(bookingService.setOnTheWay(id));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Technician marks arrived with PathVariable
     */
    @PutMapping("/{id}/arrived")
    public ResponseEntity<?> setArrived(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(bookingService.setArrived(id));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Verify start OTP with PathVariable and RequestParam
     */
    @PutMapping("/{id}/verify-start-otp")
    public ResponseEntity<?> verifyStartOtp(@PathVariable Long id, @RequestParam String otp) {
        try {
            return ResponseEntity.ok(bookingService.verifyStartOtp(id, otp));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Upload job photos with PathVariable and RequestBody
     */
    @PutMapping("/{id}/upload-photos")
    public ResponseEntity<?> uploadPhotos(@PathVariable Long id, @RequestBody Map<String, String> photos) {
        try {
            return ResponseEntity.ok(bookingService.uploadPhotos(id, photos));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Upload before photo with PathVariable and RequestParam
     */
    @PutMapping("/{id}/upload-before")
    public ResponseEntity<?> uploadBefore(@PathVariable Long id, @RequestParam String photoUrl) {
        try {
            return ResponseEntity.ok(bookingService.uploadBeforePhoto(id, photoUrl));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Request service completion with PathVariable
     */
    @PutMapping("/{id}/request-completion")
    public ResponseEntity<?> requestCompletion(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(bookingService.requestCompletion(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Cancel booking with PathVariable and RequestParam
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable Long id, @RequestParam(required = false) String reason) {
        try {
            return ResponseEntity.ok(bookingService.cancelBooking(id, reason));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Settle final payment with PathVariable and RequestBody
     */
    @PostMapping("/{id}/pay")
    public ResponseEntity<?> pay(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        try {
            return ResponseEntity.ok(bookingService.processPayment(id, payload));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Submit review with PathVariable and RequestBody
     */
    @PostMapping("/{id}/review")
    public ResponseEntity<?> review(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        try {
            return ResponseEntity.ok(bookingService.submitReview(id, payload));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get reviews for worker with PathVariable
     */
    @GetMapping("/reviews/worker/{workerId}")
    public ResponseEntity<List<Map<String, Object>>> getReviewsForWorker(@PathVariable Long workerId) {
        return ResponseEntity.ok(bookingService.getWorkerReviews(workerId));
    }

    /**
     * Claim warranty with PathVariable and RequestBody
     */
    @PostMapping("/{id}/claim-warranty")
    public ResponseEntity<?> claimWarranty(@PathVariable Long id, @RequestBody(required = false) Map<String, Object> payload) {
        try {
            return ResponseEntity.ok(bookingService.claimWarranty(id, payload));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Accept warranty with PathVariable
     */
    @PutMapping("/{id}/accept-warranty")
    public ResponseEntity<?> acceptWarranty(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(bookingService.acceptWarranty(id));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Complete warranty with PathVariable
     */
    @PutMapping("/{id}/complete-warranty")
    public ResponseEntity<?> completeWarranty(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(bookingService.completeWarranty(id));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
