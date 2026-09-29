package com.skillverse.service;

import com.skillverse.model.*;
import com.skillverse.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminService {

    private final UserRepository userRepository;
    private final WorkerProfileRepository workerProfileRepository;
    private final ServiceBookingRepository bookingRepository;
    private final VerificationRequestRepository verificationRequestRepository;
    private final WorkerWalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;
    private final ToolStoreProductRepository productRepository;
    private final StoreOrderRepository orderRepository;
    private final CourseRepository courseRepository;
    private final CourseEnrollmentRepository enrollmentRepository;
    private final AuditLogRepository auditLogRepository;
    private final PlatformSettingRepository settingRepository;

    public AdminService(UserRepository userRepository,
                        WorkerProfileRepository workerProfileRepository,
                        ServiceBookingRepository bookingRepository,
                        VerificationRequestRepository verificationRequestRepository,
                        WorkerWalletRepository walletRepository,
                        WalletTransactionRepository transactionRepository,
                        ToolStoreProductRepository productRepository,
                        StoreOrderRepository orderRepository,
                        CourseRepository courseRepository,
                        CourseEnrollmentRepository enrollmentRepository,
                        AuditLogRepository auditLogRepository,
                        PlatformSettingRepository settingRepository) {
        this.userRepository = userRepository;
        this.workerProfileRepository = workerProfileRepository;
        this.bookingRepository = bookingRepository;
        this.verificationRequestRepository = verificationRequestRepository;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.auditLogRepository = auditLogRepository;
        this.settingRepository = settingRepository;

        initDefaultSettingsAndLogs();
    }

    private void initDefaultSettingsAndLogs() {
        if (settingRepository.count() == 0) {
            settingRepository.save(new PlatformSetting("platform_commission", "5", "Platform commission deduction percentage from worker earnings"));
            settingRepository.save(new PlatformSetting("auto_approve_workers", "false", "Whether technicians are automatically verified upon sign-up"));
            settingRepository.save(new PlatformSetting("emergency_fee_multiplier", "1.25", "Pricing multiplier applied to emergency service dispatches"));
            settingRepository.save(new PlatformSetting("min_wallet_withdrawal", "500", "Minimum balance required to initiate cashout"));
            settingRepository.save(new PlatformSetting("tool_store_delivery_fee", "60", "Flat standard delivery fee for store products"));
        }
    }

    public Map<String, Object> getPlatformStats() {
        List<User> allUsers = userRepository.findAll();
        long totalUsers = allUsers.size();
        long totalWorkers = allUsers.stream().filter(u -> "WORKER".equalsIgnoreCase(u.getRole())).count();
        long verifiedWorkers = allUsers.stream().filter(u -> "WORKER".equalsIgnoreCase(u.getRole()) && u.isVerified()).count();
        long pendingVerifications = verificationRequestRepository.findByStatus("PENDING").size();

        List<ServiceBooking> allBookings = bookingRepository.findAll();
        long totalBookings = allBookings.size();
        List<String> activeStatuses = List.of("CONFIRMED", "ON_THE_WAY", "ARRIVED", "IN_PROGRESS");
        long activeBookings = allBookings.stream().filter(b -> activeStatuses.contains(b.getStatus())).count();
        long completedBookings = allBookings.stream().filter(b -> "COMPLETED".equalsIgnoreCase(b.getStatus())).count();

        List<ServiceBooking> completedList = allBookings.stream().filter(b -> "COMPLETED".equalsIgnoreCase(b.getStatus())).collect(Collectors.toList());
        double totalGmv = completedList.stream().mapToDouble(b -> b.getFinalPaymentAmount() != null ? b.getFinalPaymentAmount() : (b.getAgreedCost() != null ? b.getAgreedCost() : 0.0)).sum();
        double totalCommissions = completedList.stream().mapToDouble(b -> b.getPlatformCommission() != null ? b.getPlatformCommission() : 0.0).sum();

        long totalStoreOrders = orderRepository.count();
        double totalStoreRevenue = orderRepository.findAll().stream().mapToDouble(o -> o.getTotalAmount() != null ? o.getTotalAmount() : 0.0).sum();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("totalWorkers", totalWorkers);
        stats.put("verifiedWorkers", verifiedWorkers);
        stats.put("pendingVerifications", pendingVerifications);
        stats.put("totalBookings", totalBookings);
        stats.put("activeBookings", activeBookings);
        stats.put("completedBookings", completedBookings);
        stats.put("totalGmv", Math.round(totalGmv * 100.0) / 100.0);
        stats.put("totalPlatformCommissions", Math.round(totalCommissions * 100.0) / 100.0);
        stats.put("totalStoreOrders", totalStoreOrders);
        stats.put("totalStoreRevenue", Math.round(totalStoreRevenue * 100.0) / 100.0);

        return stats;
    }

    public List<Map<String, Object>> getUsers(String role, String status, String search) {
        List<User> list = userRepository.findAll();

        if (role != null && !role.trim().isEmpty() && !"ALL".equalsIgnoreCase(role.trim())) {
            list = list.stream().filter(u -> role.equalsIgnoreCase(u.getRole())).collect(Collectors.toList());
        }

        if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status.trim())) {
            list = list.stream().filter(u -> status.equalsIgnoreCase(u.getStatus())).collect(Collectors.toList());
        }

        if (search != null && !search.trim().isEmpty()) {
            String q = search.toLowerCase().trim();
            list = list.stream().filter(u ->
                    (u.getName() != null && u.getName().toLowerCase().contains(q)) ||
                    (u.getEmail() != null && u.getEmail().toLowerCase().contains(q)) ||
                    (u.getPhone() != null && u.getPhone().contains(q))
            ).collect(Collectors.toList());
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (User u : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", u.getId());
            map.put("name", u.getName());
            map.put("email", u.getEmail());
            map.put("role", u.getRole());
            map.put("status", u.getStatus() != null ? u.getStatus() : "ACTIVE");
            map.put("phone", u.getPhone());
            map.put("address", u.getAddress());
            map.put("verified", u.isVerified());
            map.put("rating", u.getRating());
            map.put("nidNumber", u.getNidNumber());
            map.put("profilePicture", u.getProfilePicture());

            if ("WORKER".equalsIgnoreCase(u.getRole())) {
                workerProfileRepository.findByUserId(u.getId()).ifPresent(wp -> {
                    map.put("skills", wp.getSkills());
                    map.put("serviceArea", wp.getServiceArea());
                    map.put("careerLevel", wp.getCareerLevel());
                    map.put("available", wp.isAvailable());
                });
                walletRepository.findByWorkerId(u.getId()).ifPresent(w -> {
                    map.put("walletBalance", w.getBalance());
                    map.put("totalEarnings", w.getTotalEarnings());
                });
            }
            result.add(map);
        }
        return result;
    }

    public Optional<User> updateUserStatus(Long id, Map<String, Object> payload) {
        return userRepository.findById(id).map(u -> {
            if (payload.containsKey("status")) {
                u.setStatus(payload.get("status").toString().toUpperCase());
            }
            if (payload.containsKey("verified")) {
                u.setVerified(Boolean.parseBoolean(payload.get("verified").toString()));
            }
            userRepository.save(u);

            auditLogRepository.save(new AuditLog("USER_STATUS_UPDATE", "System Admin", "ADMIN", "User", id,
                    "Admin modified user #" + id + " (" + u.getName() + ") -> Status: " + u.getStatus() + ", Verified: " + u.isVerified()));

            return u;
        });
    }

    public List<ServiceBooking> getBookings(String status) {
        List<ServiceBooking> list = bookingRepository.findAllByOrderByCreatedAtDesc();
        if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status.trim())) {
            return list.stream().filter(b -> status.equalsIgnoreCase(b.getStatus())).collect(Collectors.toList());
        }
        return list;
    }

    public Optional<ServiceBooking> resolveDispute(Long id, Map<String, Object> payload) {
        return bookingRepository.findById(id).map(booking -> {
            String resolution = payload.getOrDefault("resolution", "RESOLVED").toString();
            String remarks = payload.getOrDefault("remarks", "Dispute resolved by Admin").toString();

            if ("REFUND_CUSTOMER".equalsIgnoreCase(resolution)) {
                booking.setStatus("CANCELLED");
                booking.setIsRefunded(true);
                booking.setRefundAmount(booking.getAdvancePaidAmount() != null ? booking.getAdvancePaidAmount() : 300.0);
            } else if ("PAY_WORKER".equalsIgnoreCase(resolution)) {
                booking.setStatus("COMPLETED");
            }
            booking.setUpdatedAt(LocalDateTime.now());
            bookingRepository.save(booking);

            auditLogRepository.save(new AuditLog("DISPUTE_RESOLUTION", "System Admin", "ADMIN", "Booking", id,
                    "Dispute on Booking #" + id + " resolved with decision: " + resolution + " | " + remarks));

            return booking;
        });
    }

    public List<PlatformSetting> getSettings() {
        return settingRepository.findAll();
    }

    public Optional<PlatformSetting> updateSetting(String key, Map<String, String> payload) {
        return settingRepository.findBySettingKey(key).map(setting -> {
            if (payload.containsKey("value")) {
                setting.setSettingValue(payload.get("value"));
                setting.setUpdatedAt(LocalDateTime.now());
            }
            PlatformSetting saved = settingRepository.save(setting);

            auditLogRepository.save(new AuditLog("SETTING_UPDATE", "System Admin", "ADMIN", "PlatformSetting", setting.getId(),
                    "Platform setting updated: [" + key + "] = " + setting.getSettingValue()));

            return saved;
        });
    }

    public List<AuditLog> getAuditLogs() {
        return auditLogRepository.findTop50ByOrderByTimestampDesc();
    }

    public Map<String, Object> getFinances() {
        List<ServiceBooking> completedBookings = bookingRepository.findAll().stream()
                .filter(b -> "COMPLETED".equalsIgnoreCase(b.getStatus()))
                .collect(Collectors.toList());

        double totalBookingRevenue = completedBookings.stream()
                .mapToDouble(b -> b.getFinalPaymentAmount() != null ? b.getFinalPaymentAmount() : 0.0).sum();
        double totalPlatformFees = completedBookings.stream()
                .mapToDouble(b -> b.getPlatformCommission() != null ? b.getPlatformCommission() : 0.0).sum();
        double totalWorkerPayouts = completedBookings.stream()
                .mapToDouble(b -> b.getWorkerNetEarning() != null ? b.getWorkerNetEarning() : 0.0).sum();

        List<StoreOrder> allOrders = orderRepository.findAll();
        double storeGrossRevenue = allOrders.stream()
                .filter(o -> !"CANCELLED".equalsIgnoreCase(o.getOrderStatus()))
                .mapToDouble(o -> o.getTotalAmount() != null ? o.getTotalAmount() : 0.0).sum();

        List<WalletTransaction> withdrawals = transactionRepository.findByTransactionTypeOrderByCreatedAtDesc("WITHDRAWAL");
        double totalWithdrawn = Math.abs(withdrawals.stream()
                .mapToDouble(w -> w.getAmount() != null ? w.getAmount() : 0.0).sum());

        Map<String, Object> fin = new HashMap<>();
        fin.put("totalBookingRevenue", Math.round(totalBookingRevenue * 100.0) / 100.0);
        fin.put("totalPlatformFees", Math.round(totalPlatformFees * 100.0) / 100.0);
        fin.put("totalWorkerPayouts", Math.round(totalWorkerPayouts * 100.0) / 100.0);
        fin.put("storeGrossRevenue", Math.round(storeGrossRevenue * 100.0) / 100.0);
        fin.put("totalWithdrawnByWorkers", Math.round(totalWithdrawn * 100.0) / 100.0);
        fin.put("netPlatformIncome", Math.round((totalPlatformFees + (storeGrossRevenue * 0.15)) * 100.0) / 100.0);

        return fin;
    }
}
