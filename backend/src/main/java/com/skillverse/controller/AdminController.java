package com.skillverse.controller;

import com.skillverse.model.*;
import com.skillverse.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for System Administration, Analytics, Financial Auditing, and Governance.
 * Architecture Flow: User/Client -> Controller -> Service -> Repository -> Model (Entity)
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // ==========================================
    // 1. OVERVIEW DASHBOARD METRICS & FEED
    // ==========================================

    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getOverview() {
        return ResponseEntity.ok(adminService.getOverview());
    }

    // ==========================================
    // 2. USER MANAGEMENT
    // ==========================================

    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> getUsers() {
        return ResponseEntity.ok(adminService.getUsers());
    }

    @PutMapping("/users/{id}/status")
    public ResponseEntity<?> updateUserStatus(@PathVariable Long id, @RequestParam String status) {
        return adminService.updateUserStatus(id, status)
                .map(user -> ResponseEntity.ok(Map.of("message", "User status updated to " + status.toUpperCase(), "user", user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/users/{id}/unsuspend")
    public ResponseEntity<?> unsuspendUser(@PathVariable Long id) {
        return adminService.updateUserStatus(id, "ACTIVE")
                .map(user -> ResponseEntity.ok(Map.of("message", "User account unsuspended and restored to ACTIVE.", "user", user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/users/reopen-requests")
    public ResponseEntity<List<User>> getReopenRequests() {
        return ResponseEntity.ok(adminService.getReopenRequests());
    }

    @GetMapping("/reopen-requests")
    public ResponseEntity<List<User>> getReopenRequestsAlt() {
        return ResponseEntity.ok(adminService.getReopenRequests());
    }

    // ==========================================
    // 3. WORKER VERIFICATION DIRECTORY
    // ==========================================

    @GetMapping("/verification/requests")
    public ResponseEntity<List<VerificationRequest>> getVerificationRequests(@RequestParam(required = false) String status) {
        return ResponseEntity.ok(adminService.getVerificationRequests(status));
    }

    @RequestMapping(value = "/verification/{id}/decision", method = {RequestMethod.PUT, RequestMethod.POST})
    public ResponseEntity<?> decideVerification(@PathVariable Long id,
                                                @RequestParam(required = false) String decision,
                                                @RequestParam(required = false) String reason,
                                                @RequestBody(required = false) Map<String, String> body) {
        try {
            return adminService.decideVerification(id, decision, reason, body)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==========================================
    // 4. BOOKINGS DIRECTORY
    // ==========================================

    @GetMapping("/bookings")
    public ResponseEntity<List<ServiceBooking>> getAllBookings(@RequestParam(required = false) String status) {
        return ResponseEntity.ok(adminService.getAllBookings(status));
    }

    // ==========================================
    // 5. FINANCE & WITHDRAWALS
    // ==========================================

    @GetMapping("/finance")
    public ResponseEntity<Map<String, Object>> getFinanceOverview() {
        return ResponseEntity.ok(adminService.getFinanceOverview());
    }

    @PutMapping("/withdrawals/{id}/approve")
    public ResponseEntity<?> approveWithdrawal(@PathVariable Long id) {
        return adminService.approveWithdrawal(id)
                .map(tx -> ResponseEntity.ok(Map.of("message", "Withdrawal approved and disbursed successfully", "transaction", tx)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/withdrawals/{id}/reject")
    public ResponseEntity<?> rejectWithdrawal(@PathVariable Long id, @RequestParam(required = false) String reason) {
        return adminService.rejectWithdrawal(id, reason)
                .map(tx -> ResponseEntity.ok(Map.of("message", "Withdrawal rejected and balance refunded", "transaction", tx)))
                .orElse(ResponseEntity.notFound().build());
    }

    // ==========================================
    // 6. ANALYTICS ENGINE
    // ==========================================

    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getAnalytics(@RequestParam(defaultValue = "30D") String period) {
        return ResponseEntity.ok(adminService.getAnalytics(period));
    }

    // ==========================================
    // 7. AUDIT LOGS & ACTIVITY
    // ==========================================

    @GetMapping("/logs")
    public ResponseEntity<List<AuditLog>> getLogs() {
        return ResponseEntity.ok(adminService.getLogs());
    }

    @PostMapping("/logs")
    public ResponseEntity<AuditLog> createLog(@RequestBody AuditLog log) {
        return ResponseEntity.ok(adminService.createLog(log));
    }

    // ==========================================
    // 8. PLATFORM SETTINGS
    // ==========================================

    @GetMapping("/settings")
    public ResponseEntity<List<PlatformSetting>> getSettings() {
        return ResponseEntity.ok(adminService.getSettings());
    }

    @PutMapping("/settings")
    public ResponseEntity<?> updateSettings(@RequestBody Map<String, String> settingsMap) {
        List<PlatformSetting> updated = adminService.updateSettings(settingsMap);
        return ResponseEntity.ok(Map.of("message", "Platform configuration updated successfully", "settings", updated));
    }
}
