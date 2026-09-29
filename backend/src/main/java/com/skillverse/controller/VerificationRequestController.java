package com.skillverse.controller;

import com.skillverse.dto.VerificationSubmissionRequest;
import com.skillverse.model.VerificationRequest;
import com.skillverse.service.VerificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/verification")
@CrossOrigin(origins = "*")
public class VerificationRequestController {

    private final VerificationService verificationService;

    public VerificationRequestController(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @RequestMapping(value = "/send-phone-otp", method = { RequestMethod.POST, RequestMethod.GET })
    public ResponseEntity<?> sendPhoneOtp(
            @RequestParam(required = false) String phone,
            @RequestBody(required = false) Map<String, String> payload) {
        String phoneNum = phone;
        if ((phoneNum == null || phoneNum.isEmpty()) && payload != null) {
            phoneNum = payload.get("phone");
        }
        try {
            Map<String, Object> res = verificationService.sendPhoneOtp(phoneNum);
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @RequestMapping(value = "/verify-phone-otp", method = { RequestMethod.POST, RequestMethod.GET })
    public ResponseEntity<?> verifyPhoneOtp(
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String otp,
            @RequestBody(required = false) Map<String, String> payload) {
        String phoneNum = phone;
        String otpCode = otp;
        if ((phoneNum == null || phoneNum.isEmpty()) && payload != null) {
            phoneNum = payload.get("phone");
        }
        if ((otpCode == null || otpCode.isEmpty()) && payload != null) {
            otpCode = payload.get("otp");
        }
        try {
            Map<String, Object> res = verificationService.verifyPhoneOtp(phoneNum, otpCode);
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submitVerification(@RequestBody VerificationSubmissionRequest dto) {
        try {
            VerificationRequest saved = verificationService.submitVerification(dto);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/status/{userId}")
    public ResponseEntity<?> getStatus(@PathVariable Long userId) {
        return verificationService.getLatestStatus(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<VerificationRequest>> getAllRequests(@RequestParam(required = false) String status) {
        return ResponseEntity.ok(verificationService.getAllRequests(status));
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<VerificationRequest> getRequestById(@PathVariable Long id) {
        return verificationService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/admin/review/{id}")
    public ResponseEntity<?> reviewRequest(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String remarks,
            @RequestParam(required = false) Long reviewerId) {
        return verificationService.reviewRequest(id, status, remarks, reviewerId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<?> deleteRequest(@PathVariable Long id) {
        verificationService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Verification request deleted"));
    }
}
