package com.skillverse.controller;

import com.skillverse.model.AuditLog;
import com.skillverse.model.PlatformSetting;
import com.skillverse.model.ServiceBooking;
import com.skillverse.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getPlatformStats() {
        return ResponseEntity.ok(adminService.getPlatformStats());
    }

    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> getUsers(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(adminService.getUsers(role, status, search));
    }

    @PutMapping("/users/{id}/status")
    public ResponseEntity<?> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        return adminService.updateUserStatus(id, payload)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<ServiceBooking>> getBookings(@RequestParam(required = false) String status) {
        return ResponseEntity.ok(adminService.getBookings(status));
    }

    @PutMapping("/bookings/{id}/dispute")
    public ResponseEntity<?> resolveDispute(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        return adminService.resolveDispute(id, payload)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/settings")
    public ResponseEntity<List<PlatformSetting>> getSettings() {
        return ResponseEntity.ok(adminService.getSettings());
    }

    @PutMapping("/settings/{key}")
    public ResponseEntity<?> updateSetting(@PathVariable String key, @RequestBody Map<String, String> payload) {
        return adminService.updateSetting(key, payload)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<List<AuditLog>> getAuditLogs() {
        return ResponseEntity.ok(adminService.getAuditLogs());
    }

    @GetMapping("/finances")
    public ResponseEntity<Map<String, Object>> getFinances() {
        return ResponseEntity.ok(adminService.getFinances());
    }
}
