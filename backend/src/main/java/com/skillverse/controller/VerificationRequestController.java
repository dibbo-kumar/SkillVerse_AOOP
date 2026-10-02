package com.skillverse.controller;

import com.skillverse.dto.VerificationSubmissionDto;
import com.skillverse.model.VerificationRequest;
import com.skillverse.service.VerificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for Worker Verification Dossiers & Phone OTP Verification.
 * Architecture Flow: User/Client -> Controller -> Service -> Repository -> Model (Entity)
 */
@RestController
@RequestMapping("/api/verification")
@CrossOrigin(origins = "*")
public class VerificationRequestController {

    private final VerificationService verificationService;

    public VerificationRequestController(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    // --- 1. SEND & VERIFY PHONE OTP SIMULATOR ---

    @RequestMapping(value = "/send-phone-otp", method = { RequestMethod.POST, RequestMethod.GET })
    public ResponseEntity<?> sendPhoneOtp(@RequestParam(required = false) String phone,
                                          @RequestBody(required = false) Map<String, String> payload) {
        String phoneNum = phone;
        if ((phoneNum == null || phoneNum.isEmpty()) && payload != null) {
            phoneNum = payload.get("phone");
        }
        try {
            return ResponseEntity.ok(verificationService.sendPhoneOtp(phoneNum));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @RequestMapping(value = "/verify-phone-otp", method = { RequestMethod.POST, RequestMethod.GET })
    public ResponseEntity<?> verifyPhoneOtp(@RequestParam(required = false) String phone,
                                            @RequestParam(required = false) String otp,
                                            @RequestBody(required = false) Map<String, String> payload) {
        String phoneNum = phone;
        String otpCode = otp;
        if (payload != null) {
            if (phoneNum == null || phoneNum.isEmpty()) phoneNum = payload.get("phone");
            if (otpCode == null || otpCode.isEmpty()) otpCode = payload.get("otp");
        }
        try {
            return ResponseEntity.ok(verificationService.verifyPhoneOtp(phoneNum, otpCode));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // --- 2. SUBMIT COMPLETE VERIFICATION REQUEST ---

    /**
     * Standard CRUD: Save Verification Dossier with RequestBody
     */
    @PostMapping("/submit")
    public ResponseEntity<?> save(@RequestBody VerificationSubmissionDto dto) {
        try {
            VerificationRequest saved = verificationService.submitVerification(dto);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException | java.util.NoSuchElementException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Standard CRUD: Get Verification by ID with PathVariable
     */
    @GetMapping("/{id}")
    public ResponseEntity<VerificationRequest> getById(@PathVariable Long id) {
        return verificationService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // --- 3. GET VERIFICATION STATUS FOR WORKER ---

    @GetMapping("/worker/{userId}")
    public ResponseEntity<?> getWorkerVerification(@PathVariable Long userId) {
        return verificationService.getWorkerVerification(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<VerificationRequest>> getUserRequests(@PathVariable Long userId) {
        return ResponseEntity.ok(verificationService.getUserRequests(userId));
    }

    // --- 4. ADMIN QUEUES ---

    @GetMapping("/pending")
    public ResponseEntity<List<VerificationRequest>> getPendingRequests() {
        return ResponseEntity.ok(verificationService.getPendingRequests());
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveVerification(@PathVariable Long id) {
        return verificationService.approve(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/correction")
    public ResponseEntity<?> requestCorrection(@PathVariable Long id, @RequestParam String reason) {
        return verificationService.requestCorrection(id, reason)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectVerification(@PathVariable Long id, @RequestParam(required = false) String reason) {
        return verificationService.reject(id, reason)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
