package com.skillverse.service;

import com.skillverse.dto.VerificationSubmissionRequest;
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

@Service
@Transactional
public class VerificationService {

    private final VerificationRequestRepository verificationRepository;
    private final UserRepository userRepository;
    private final WorkerProfileRepository workerProfileRepository;
    private final AuditLogRepository auditLogRepository;

    private final Map<String, String> phoneOtpStore = new HashMap<>();

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

        return Map.of(
                "success", true,
                "phone", phoneNum,
                "simulatedOtp", otp,
                "message", "Simulated SMS OTP sent to " + phoneNum + ". Code: " + otp
        );
    }

    public Map<String, Object> verifyPhoneOtp(String phone, String otp) {
        if (phone == null || phone.trim().isEmpty() || otp == null || otp.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number and OTP code are required");
        }
        String phoneNum = phone.trim();
        String storedOtp = phoneOtpStore.get(phoneNum);

        boolean match = (storedOtp != null && storedOtp.equals(otp.trim())) || "1234".equals(otp.trim());
        if (match) {
            phoneOtpStore.remove(phoneNum);
            return Map.of("success", true, "message", "Phone verified successfully!");
        } else {
            throw new IllegalArgumentException("Invalid or expired OTP. Please try again or use demo code 1234.");
        }
    }

    public VerificationRequest submitVerification(VerificationSubmissionRequest dto) {
        if (dto.getUserId() == null) {
            throw new IllegalArgumentException("User ID is required");
        }

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

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
        workerProfileRepository.save(profile);

        VerificationRequest req = new VerificationRequest();
        req.setUser(user);
        req.setFullName(dto.getFullName() != null ? dto.getFullName() : user.getName());
        req.setDateOfBirth(dto.getDateOfBirth());
        req.setPhone(dto.getPhone() != null ? dto.getPhone() : user.getPhone());
        req.setPhoneVerified(dto.getPhoneVerified() != null ? dto.getPhoneVerified() : true);
        req.setNidNumber(dto.getNidNumber());
        req.setNidFrontPhoto(dto.getNidFrontPhoto());
        req.setNidBackPhoto(dto.getNidBackPhoto());
        req.setProfileSelfiePhoto(dto.getProfileSelfiePhoto());
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
        req.setPayoutMethod(dto.getPayoutMethod());
        req.setPayoutAccount(dto.getPayoutAccount());
        req.setPayoutAccountHolder(dto.getPayoutAccountHolder());
        req.setPayoutBankName(dto.getPayoutBankName());
        req.setPayoutBankBranch(dto.getPayoutBankBranch());
        req.setStatus("PENDING");
        req.setSubmittedAt(LocalDateTime.now());
        req.setAdminRemarks(null);

        VerificationRequest savedReq = verificationRepository.save(req);

        AuditLog log = new AuditLog("SUBMIT_VERIFICATION", user.getName(), "WORKER", "VerificationRequest", savedReq.getId(),
                "Worker " + user.getName() + " submitted full KYC verification request (Req #" + savedReq.getId() + ")");
        auditLogRepository.save(log);

        return savedReq;
    }

    public Optional<VerificationRequest> getLatestStatus(Long userId) {
        return verificationRepository.findTopByUserIdOrderBySubmittedAtDesc(userId);
    }

    public List<VerificationRequest> getAllRequests(String status) {
        if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status.trim())) {
            return verificationRepository.findByStatusOrderBySubmittedAtDesc(status.toUpperCase().trim());
        }
        return verificationRepository.findAllByOrderBySubmittedAtDesc();
    }

    public Optional<VerificationRequest> getById(Long id) {
        return verificationRepository.findById(id);
    }

    public Optional<VerificationRequest> reviewRequest(Long id, String status, String remarks, Long reviewerId) {
        return verificationRepository.findById(id).map(req -> {
            String cleanStatus = status != null ? status.toUpperCase().trim() : "APPROVED";
            req.setStatus(cleanStatus);
            req.setAdminRemarks(remarks);
            req.setReviewedAt(LocalDateTime.now());

            User user = req.getUser();
            if (user != null) {
                if ("APPROVED".equalsIgnoreCase(cleanStatus)) {
                    user.setVerified(true);
                    user.setStatus("ACTIVE");
                    if (req.getNidNumber() != null && !req.getNidNumber().isEmpty()) {
                        user.setNidNumber(req.getNidNumber());
                    }
                    if (req.getProfileSelfiePhoto() != null && !req.getProfileSelfiePhoto().isEmpty()) {
                        user.setProfilePicture(req.getProfileSelfiePhoto());
                    }
                    if (req.getPhone() != null && !req.getPhone().isEmpty()) {
                        user.setPhone(req.getPhone());
                    }
                } else if ("REJECTED".equalsIgnoreCase(cleanStatus)) {
                    user.setVerified(false);
                    user.setStatus("REJECTED");
                } else if ("NEEDS_RESUBMISSION".equalsIgnoreCase(cleanStatus)) {
                    user.setVerified(false);
                    user.setStatus("ACTION_REQUIRED");
                }
                userRepository.save(user);
            }

            VerificationRequest saved = verificationRepository.save(req);

            AuditLog log = new AuditLog("REVIEW_VERIFICATION", "System Admin", "ADMIN", "VerificationRequest", id,
                    "Admin reviewed KYC Request #" + id + " -> Status: " + cleanStatus
                            + (remarks != null ? " | Remarks: " + remarks : ""));
            auditLogRepository.save(log);

            return saved;
        });
    }

    public void delete(Long id) {
        verificationRepository.deleteById(id);
    }
}
