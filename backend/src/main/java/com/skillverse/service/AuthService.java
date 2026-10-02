package com.skillverse.service;

import com.skillverse.model.User;
import com.skillverse.model.VerificationRequest;
import com.skillverse.model.WorkerProfile;
import com.skillverse.repository.UserRepository;
import com.skillverse.repository.WorkerProfileRepository;
import com.skillverse.repository.VerificationRequestRepository;
import com.skillverse.repository.AuditLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final WorkerProfileRepository workerProfileRepository;
    private final VerificationRequestRepository verificationRequestRepository;

    private final AuditLogRepository auditLogRepository;

    public AuthService(UserRepository userRepository,
                       WorkerProfileRepository workerProfileRepository,
                       VerificationRequestRepository verificationRequestRepository,
                       AuditLogRepository auditLogRepository) {
        this.userRepository = userRepository;
        this.workerProfileRepository = workerProfileRepository;
        this.verificationRequestRepository = verificationRequestRepository;
        this.auditLogRepository = auditLogRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        userOpt.ifPresent(user -> {
            if ("WORKER".equalsIgnoreCase(user.getRole())) {
                verificationRequestRepository.findTopByUserIdOrderBySubmittedAtDesc(user.getId())
                        .ifPresent(req -> {
                            if ("APPROVED".equalsIgnoreCase(req.getStatus())) {
                                user.setVerified(true);
                                // Only activate if unverified or under review, NEVER overwrite SUSPENDED status
                                if ("UNVERIFIED".equalsIgnoreCase(user.getStatus()) || "UNDER_REVIEW".equalsIgnoreCase(user.getStatus()) || "CORRECTION_REQUIRED".equalsIgnoreCase(user.getStatus())) {
                                    user.setStatus("ACTIVE");
                                    userRepository.save(user);
                                }
                            }
                        });
            }
        });
        return userOpt;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User registerUser(User user) {
        if ("WORKER".equalsIgnoreCase(user.getRole())) {
            user.setVerified(false);
            user.setStatus("UNVERIFIED");
        } else {
            user.setVerified(true);
            user.setStatus("ACTIVE");
        }
        User saved = userRepository.save(user);
        if ("WORKER".equalsIgnoreCase(saved.getRole())) {
            WorkerProfile profile = new WorkerProfile(
                    saved,
                    "Electrical, Plumbing",
                    1,
                    "Dhaka North (Gulshan, Banani, Uttara)",
                    "Bronze",
                    350.0);
            profile.setAvailable(true);
            workerProfileRepository.save(profile);
        }
        return saved;
    }

    public Optional<User> loginUser(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if ("WORKER".equalsIgnoreCase(user.getRole())) {
                verificationRequestRepository.findTopByUserIdOrderBySubmittedAtDesc(user.getId())
                        .ifPresent(req -> {
                            if ("APPROVED".equalsIgnoreCase(req.getStatus())) {
                                user.setVerified(true);
                                // Only activate if unverified or under review, NEVER overwrite SUSPENDED status
                                if ("UNVERIFIED".equalsIgnoreCase(user.getStatus()) || "UNDER_REVIEW".equalsIgnoreCase(user.getStatus()) || "CORRECTION_REQUIRED".equalsIgnoreCase(user.getStatus())) {
                                    user.setStatus("ACTIVE");
                                    userRepository.save(user);
                                }
                            }
                        });
            }
            return Optional.of(user);
        }
        return Optional.empty();
    }

    public Optional<User> requestAccountReopen(Long userId, String reason) {
        return userRepository.findById(userId).map(user -> {
            user.setReopenRequested(true);
            user.setReopenReason(reason != null && !reason.trim().isEmpty() ? reason.trim() : "User requested account reinstatement and review.");
            user.setReopenRequestedAt(LocalDateTime.now());
            userRepository.save(user);

            if (auditLogRepository != null) {
                auditLogRepository.save(new com.skillverse.model.AuditLog(
                        "ACCOUNT_REOPEN_REQUESTED",
                        user.getName(),
                        user.getRole(),
                        "User",
                        user.getId(),
                        "Suspended user " + user.getName() + " (" + user.getRole() + ") submitted an account reopening appeal: " + user.getReopenReason()
                ));
            }

            return user;
        });
    }

    public Optional<User> updateUser(Long id, Map<String, Object> profileMap) {
        return userRepository.findById(id).map(user -> {
            if (profileMap.containsKey("name") && profileMap.get("name") != null)
                user.setName(profileMap.get("name").toString());
            if (profileMap.containsKey("email") && profileMap.get("email") != null)
                user.setEmail(profileMap.get("email").toString());
            if (profileMap.containsKey("phone") && profileMap.get("phone") != null)
                user.setPhone(profileMap.get("phone").toString());
            if (profileMap.containsKey("profilePicture")) {
                Object pic = profileMap.get("profilePicture");
                if (pic == null || (pic instanceof String && ((String) pic).trim().isEmpty())) {
                    user.setProfilePicture(null);
                } else {
                    user.setProfilePicture(pic.toString());
                }
            }
            if (profileMap.containsKey("address") && profileMap.get("address") != null)
                user.setAddress(profileMap.get("address").toString());
            if (profileMap.containsKey("latitude") && profileMap.get("latitude") != null) {
                try { user.setLatitude(Double.parseDouble(profileMap.get("latitude").toString())); } catch (Exception ignored) {}
            }
            if (profileMap.containsKey("longitude") && profileMap.get("longitude") != null) {
                try { user.setLongitude(Double.parseDouble(profileMap.get("longitude").toString())); } catch (Exception ignored) {}
            }

            if (profileMap.containsKey("nidNumber") && profileMap.get("nidNumber") != null) {
                String newNid = profileMap.get("nidNumber").toString().trim();
                if (!newNid.isEmpty()) {
                    String currentNid = user.getNidNumber();
                    if (currentNid == null || !currentNid.equals(newNid)) {
                        VerificationRequest req = new VerificationRequest();
                        req.setUser(user);
                        req.setFullName(user.getName());
                        req.setPhone(user.getPhone());
                        req.setNidNumber(newNid);
                        req.setProfileSelfiePhoto(user.getProfilePicture());
                        req.setPresentAddress(user.getAddress());
                        req.setSubmittedAt(LocalDateTime.now());
                        req.setStatus("PENDING");
                        req.setAdminRemarks("NID Number update requested by user. Awaiting admin review.");
                        verificationRequestRepository.save(req);
                    }
                }
            }

            userRepository.save(user);

            if ("WORKER".equalsIgnoreCase(user.getRole())) {
                workerProfileRepository.findByUserId(user.getId()).ifPresent(wp -> {
                    if (profileMap.containsKey("address") && profileMap.get("address") != null)
                        wp.setServiceArea(profileMap.get("address").toString());
                    if (user.getLatitude() != null)
                        wp.setLatitude(user.getLatitude());
                    if (user.getLongitude() != null)
                        wp.setLongitude(user.getLongitude());
                    workerProfileRepository.save(wp);
                });
            }

            verificationRequestRepository.findTopByUserIdOrderBySubmittedAtDesc(user.getId())
                    .ifPresent(req -> {
                        req.setFullName(user.getName());
                        req.setPhone(user.getPhone());
                        if (user.getAddress() != null)
                            req.setPresentAddress(user.getAddress());
                        if (user.getProfilePicture() != null)
                            req.setProfileSelfiePhoto(user.getProfilePicture());
                        verificationRequestRepository.save(req);
                    });

            return user;
        });
    }

    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
