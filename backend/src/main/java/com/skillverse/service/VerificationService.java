package com.skillverse.service;

import com.skillverse.dto.VerificationSubmissionDto;
import com.skillverse.model.AuditLog;
import com.skillverse.model.User;
import com.skillverse.model.VerificationRequest;
import com.skillverse.model.WorkerProfile;
import com.skillverse.repository.AuditLogRepository;
import com.skillverse.repository.UserRepository;
import com.skillverse.repository.VerificationRequestRepository;
import com.skillverse.repository.WorkerProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional
public class VerificationService {

    private final VerificationRequestRepository verificationRepository;
    private final UserRepository userRepository;
    private final WorkerProfileRepository workerProfileRepository;
    private final AuditLogRepository auditLogRepository;

    private final Map<String, String> phoneOtpStore = new ConcurrentHashMap<>();

    public VerificationService(VerificationRequestRepository verificationRepository,
                               UserRepository userRepository,
                               WorkerProfileRepository workerProfileRepository,
                               AuditLogRepository auditLogRepository) {
        this.verificationRepository = verificationRepository;
        this.userRepository = userRepository;
        this.workerProfileRepository = workerProfileRepository;
        this.auditLogRepository = auditLogRepository;
    }

    public Map<String, Object> sendPhoneOtp(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number is required");
        }
        String phoneNum = phone.trim();
        String otp = String.format("%04d", new Random().nextInt(10000));
        phoneOtpStore.put(phoneNum, otp);

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("phone", phoneNum);
        res.put("simulatedOtp", otp);
        res.put("message", "Simulated OTP sent successfully: " + otp);
        return res;
    }

    public Map<String, Object> verifyPhoneOtp(String phone, String otp) {
        if (phone == null || otp == null) {
            throw new IllegalArgumentException("Phone number and OTP code are required");
        }
        String storedOtp = phoneOtpStore.get(phone.trim());
        boolean isValid = "1234".equals(otp.trim()) || (storedOtp != null && storedOtp.equals(otp.trim()));

        if (!isValid) {
            throw new IllegalArgumentException("Invalid OTP code. Please enter the simulated 4-digit OTP.");
        }

        phoneOtpStore.remove(phone.trim());
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("phone", phone.trim());
        res.put("verified", true);
        res.put("message", "Phone verified successfully!");
        return res;
    }

    public VerificationRequest submitVerification(VerificationSubmissionDto dto) {
        if (dto.getUserId() == null) {
            throw new IllegalArgumentException("User ID is required");
        }

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new NoSuchElementException("User not found: " + dto.getUserId()));

        if (dto.getFullName() != null && !dto.getFullName().isEmpty())
            user.setName(dto.getFullName());
        if (dto.getPhone() != null && !dto.getPhone().isEmpty())
            user.setPhone(dto.getPhone());
        if (dto.getProfileSelfiePhoto() != null && !dto.getProfileSelfiePhoto().isEmpty())
            user.setProfilePicture(dto.getProfileSelfiePhoto());

        String formattedAddress = (dto.getDetailedAddress() != null && !dto.getDetailedAddress().isEmpty())
                ? dto.getDetailedAddress()
                : (dto.getPresentAddress() != null ? dto.getPresentAddress() : user.getAddress());
        if (formattedAddress != null)
            user.setAddress(formattedAddress);
        if (dto.getLatitude() != null)
            user.setLatitude(dto.getLatitude());
        if (dto.getLongitude() != null)
            user.setLongitude(dto.getLongitude());

        boolean isNidChanged = dto.getNidNumber() != null
                && !dto.getNidNumber().trim().isEmpty()
                && !dto.getNidNumber().trim().equals(user.getNidNumber());

        if (user.getNidNumber() == null || user.getNidNumber().trim().isEmpty()) {
            if (dto.getNidNumber() != null && !dto.getNidNumber().trim().isEmpty()) {
                isNidChanged = true;
            }
        }

        if (isNidChanged || !user.isVerified()) {
            user.setVerified(false);
            user.setStatus("UNDER_REVIEW");
        }
        userRepository.save(user);

        WorkerProfile profile = workerProfileRepository.findByUserId(user.getId()).orElse(null);
        String calculatedServiceArea = dto.getServiceArea();
        if (calculatedServiceArea == null || calculatedServiceArea.trim().isEmpty()) {
            calculatedServiceArea = (dto.getCityArea() != null && !dto.getCityArea().isEmpty())
                    ? dto.getCityArea() + ", " + (dto.getDivision() != null ? dto.getDivision() : "Dhaka")
                    : (profile != null && profile.getServiceArea() != null ? profile.getServiceArea() : "Dhaka");
        }
        if (profile == null) {
            profile = new WorkerProfile(user, dto.getSkills() != null ? dto.getSkills() : "Technical Maintenance",
                    dto.getExperienceYears() != null ? dto.getExperienceYears() : 1,
                    calculatedServiceArea,
                    "Bronze", 350.0);
        } else {
            if (dto.getSkills() != null && !dto.getSkills().isEmpty())
                profile.setSkills(dto.getSkills());
            if (dto.getExperienceYears() != null)
                profile.setExperienceYears(dto.getExperienceYears());
            profile.setServiceArea(calculatedServiceArea);
        }
        if (dto.getLatitude() != null)
            profile.setLatitude(dto.getLatitude());
        if (dto.getLongitude() != null)
            profile.setLongitude(dto.getLongitude());
        workerProfileRepository.save(profile);

        VerificationRequest req = verificationRepository.findTopByUserIdOrderBySubmittedAtDesc(user.getId())
                .orElse(null);
        if (req == null) {
            req = new VerificationRequest();
            req.setUser(user);
        }

        req.setFullName(dto.getFullName() != null ? dto.getFullName() : user.getName());
        req.setDateOfBirth(dto.getDateOfBirth());
        req.setPhone(dto.getPhone() != null ? dto.getPhone() : user.getPhone());
        req.setPhoneVerified(dto.getPhoneVerified() != null ? dto.getPhoneVerified() : true);
        req.setNidNumber(dto.getNidNumber());
        req.setNidFrontPhoto(dto.getNidFrontPhoto());
        req.setNidBackPhoto(dto.getNidBackPhoto());
        req.setProfileSelfiePhoto(
                dto.getProfileSelfiePhoto() != null ? dto.getProfileSelfiePhoto() : user.getProfilePicture());

        req.setPresentAddress(dto.getPresentAddress());
        req.setPermanentAddress(dto.getPermanentAddress());
        req.setDivision(dto.getDivision());
        req.setDistrict(dto.getDistrict());
        req.setCityArea(dto.getCityArea());
        req.setPostalCode(dto.getPostalCode());
        req.setDetailedAddress(dto.getDetailedAddress());

        req.setSkills(dto.getSkills());
        req.setExperienceYears(dto.getExperienceYears());
        req.setExperienceDescription(dto.getExperienceDescription());
        req.setPreviousEmployer(dto.getPreviousEmployer());
        req.setExperienceCertPhoto(dto.getExperienceCertPhoto());
        req.setTrainingCertPhoto(dto.getTrainingCertPhoto());
        req.setWorkProofPhoto(dto.getWorkProofPhoto());

        req.setPayoutMethod(dto.getPayoutMethod() != null ? dto.getPayoutMethod() : "bKash");
        req.setPayoutAccount(dto.getPayoutAccount());
        req.setPayoutAccountHolder(dto.getPayoutAccountHolder());
        req.setPayoutBankName(dto.getPayoutBankName());
        req.setPayoutBankBranch(dto.getPayoutBankBranch());

        req.setStatus("PENDING");
        req.setAdminRemarks(null);
        req.setSubmittedAt(LocalDateTime.now());

        VerificationRequest saved = verificationRepository.save(req);

        auditLogRepository.save(new AuditLog(
                "VERIFICATION_SUBMITTED",
                user.getName(),
                "WORKER",
                "VerificationRequest",
                saved.getId(),
                "Worker " + user.getName() + " submitted complete verification dossier for administrative review."));

        return saved;
    }

    public Optional<VerificationRequest> getWorkerVerification(Long userId) {
        Optional<VerificationRequest> opt = verificationRepository.findTopByUserIdOrderBySubmittedAtDesc(userId);
        if (opt.isPresent()) {
            VerificationRequest req = opt.get();
            if ("APPROVED".equalsIgnoreCase(req.getStatus())) {
                userRepository.findById(userId).ifPresent(u -> {
                    if (!u.isVerified() || !"ACTIVE".equalsIgnoreCase(u.getStatus())) {
                        u.setVerified(true);
                        u.setStatus("ACTIVE");
                        userRepository.save(u);
                    }
                });
            }
        }
        return opt;
    }

    public List<VerificationRequest> getUserRequests(Long userId) {
        return verificationRepository.findByUserIdOrderBySubmittedAtDesc(userId);
    }

    public List<VerificationRequest> getPendingRequests() {
        return verificationRepository.findByStatusOrderBySubmittedAtDesc("PENDING");
    }

    public Optional<VerificationRequest> getById(Long id) {
        return verificationRepository.findById(id);
    }

    public Optional<VerificationRequest> approve(Long id) {
        return verificationRepository.findById(id).map(req -> {
            req.setStatus("APPROVED");
            req.setReviewedAt(LocalDateTime.now());
            verificationRepository.save(req);

            User user = req.getUser();
            if (user != null) {
                user.setVerified(true);
                user.setStatus("ACTIVE");
                if (req.getNidNumber() != null)
                    user.setNidNumber(req.getNidNumber());
                userRepository.save(user);
            }

            auditLogRepository.save(new AuditLog(
                    "VERIFICATION_APPROVED",
                    "System Admin",
                    "ADMIN",
                    "VerificationRequest",
                    req.getId(),
                    "Verification #" + req.getId() + " approved & certified for "
                            + (user != null ? user.getName() : "Worker")));

            return req;
        });
    }

    public Optional<VerificationRequest> requestCorrection(Long id, String reason) {
        return verificationRepository.findById(id).map(req -> {
            req.setStatus("CORRECTION_REQUIRED");
            req.setAdminRemarks(reason);
            req.setReviewedAt(LocalDateTime.now());
            verificationRepository.save(req);

            User user = req.getUser();
            if (user != null) {
                user.setVerified(false);
                user.setStatus("CORRECTION_REQUIRED");
                userRepository.save(user);
            }

            auditLogRepository.save(new AuditLog(
                    "VERIFICATION_CORRECTION_REQUESTED",
                    "System Admin",
                    "ADMIN",
                    "VerificationRequest",
                    req.getId(),
                    "Correction requested for Verification #" + req.getId() + ". Reason: " + reason));

            return req;
        });
    }

    public Optional<VerificationRequest> reject(Long id, String reason) {
        return verificationRepository.findById(id).map(req -> {
            req.setStatus("REJECTED");
            req.setAdminRemarks(reason);
            req.setReviewedAt(LocalDateTime.now());
            verificationRepository.save(req);

            User user = req.getUser();
            if (user != null) {
                user.setVerified(false);
                user.setStatus("REJECTED");
                userRepository.save(user);
            }

            auditLogRepository.save(new AuditLog(
                    "VERIFICATION_REJECTED",
                    "System Admin",
                    "ADMIN",
                    "VerificationRequest",
                    req.getId(),
                    "Verification #" + req.getId() + " rejected. Reason: " + reason));

            return req;
        });
    }
}
