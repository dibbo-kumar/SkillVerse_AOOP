package com.skillverse.controller;

import com.skillverse.dto.BookingRequest;
import com.skillverse.model.ServiceBooking;
import com.skillverse.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest request) {
        try {
            ServiceBooking saved = bookingService.createBooking(request);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<ServiceBooking>> getCustomerBookings(@PathVariable Long customerId) {
        return ResponseEntity.ok(bookingService.getCustomerBookings(customerId));
    }

    @GetMapping("/worker/{workerId}")
    public ResponseEntity<List<ServiceBooking>> getWorkerBookings(@PathVariable Long workerId) {
        return ResponseEntity.ok(bookingService.getWorkerBookings(workerId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBookingById(@PathVariable Long id) {
        return bookingService.getBookingById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/accept-price")
    public ResponseEntity<?> acceptPrice(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "CUSTOMER") String acceptedBy) {
        try {
            ServiceBooking saved = bookingService.acceptPrice(id, acceptedBy);
            return ResponseEntity.ok(saved);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/counter-offer")
    public ResponseEntity<?> counterOffer(
            @PathVariable Long id,
            @RequestParam Double price,
            @RequestParam(required = false, defaultValue = "WORKER") String offeredBy,
            @RequestParam(required = false) String status) {
        try {
            ServiceBooking saved = bookingService.counterOffer(id, price, offeredBy, status);
            return ResponseEntity.ok(saved);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/pay-advance")
    public ResponseEntity<?> payAdvance(
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload) {
        try {
            ServiceBooking saved = bookingService.payAdvance(id, payload);
            return ResponseEntity.ok(saved);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/timeout-refund")
    public ResponseEntity<?> timeoutRefund(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, Object> payload) {
        try {
            ServiceBooking saved = bookingService.timeoutRefund(id);
            return ResponseEntity.ok(saved);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateBookingStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        try {
            ServiceBooking saved = bookingService.updateStatus(id, status);
            return ResponseEntity.ok(saved);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.ok(Map.of("message", "Booking #" + id + " successfully removed."));
    }

    @PutMapping("/{id}/on-the-way")
    public ResponseEntity<?> setOnTheWay(@PathVariable Long id) {
        try {
            ServiceBooking saved = bookingService.setOnTheWay(id);
            return ResponseEntity.ok(saved);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/arrived")
    public ResponseEntity<?> setArrived(@PathVariable Long id) {
        try {
            ServiceBooking saved = bookingService.setArrived(id);
            return ResponseEntity.ok(saved);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/verify-start-otp")
    public ResponseEntity<?> verifyStartOtp(
            @PathVariable Long id,
            @RequestParam String otp) {
        try {
            ServiceBooking saved = bookingService.verifyStartOtp(id, otp);
            return ResponseEntity.ok(saved);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/upload-photos")
    public ResponseEntity<?> uploadPhotos(
            @PathVariable Long id,
            @RequestBody Map<String, String> photos) {
        try {
            ServiceBooking saved = bookingService.uploadPhotos(id, photos);
            return ResponseEntity.ok(saved);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/upload-before")
    public ResponseEntity<?> uploadBeforePhoto(
            @PathVariable Long id,
            @RequestParam String photoUrl) {
        try {
            ServiceBooking saved = bookingService.uploadBeforePhoto(id, photoUrl);
            return ResponseEntity.ok(saved);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/request-completion")
    public ResponseEntity<?> requestCompletion(@PathVariable Long id) {
        try {
            ServiceBooking saved = bookingService.requestCompletion(id);
            return ResponseEntity.ok(saved);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelBooking(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        try {
            ServiceBooking saved = bookingService.cancelBooking(id, reason);
            return ResponseEntity.ok(saved);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<?> processPayment(
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload) {
        try {
            ServiceBooking saved = bookingService.processPayment(id, payload);
            return ResponseEntity.ok(saved);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/review")
    public ResponseEntity<?> submitReview(
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload) {
        try {
            ServiceBooking saved = bookingService.submitReview(id, payload);
            return ResponseEntity.ok(saved);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/reviews/worker/{workerId}")
    public ResponseEntity<List<Map<String, Object>>> getWorkerReviews(@PathVariable Long workerId) {
        return ResponseEntity.ok(bookingService.getWorkerReviews(workerId));
    }

    @PostMapping("/{id}/claim-warranty")
    public ResponseEntity<?> claimWarranty(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, Object> payload) {
        try {
            ServiceBooking saved = bookingService.claimWarranty(id, payload);
            return ResponseEntity.ok(saved);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/accept-warranty")
    public ResponseEntity<?> acceptWarranty(@PathVariable Long id) {
        try {
            ServiceBooking saved = bookingService.acceptWarranty(id);
            return ResponseEntity.ok(saved);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/complete-warranty")
    public ResponseEntity<?> completeWarranty(@PathVariable Long id) {
        try {
            ServiceBooking saved = bookingService.completeWarranty(id);
            return ResponseEntity.ok(saved);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
