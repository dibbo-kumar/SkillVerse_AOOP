package com.skillverse.service;

import com.skillverse.dto.LoginRequest;
import com.skillverse.dto.RegisterRequest;
import com.skillverse.model.User;
import com.skillverse.model.VerificationRequest;
import com.skillverse.model.WorkerProfile;
import com.skillverse.repository.UserRepository;
import com.skillverse.repository.VerificationRequestRepository;
import com.skillverse.repository.WorkerProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final WorkerProfileRepository workerProfileRepository;
    private final VerificationRequestRepository verificationRequestRepository;

    public UserService(UserRepository userRepository,
                       WorkerProfileRepository workerProfileRepository,
                       VerificationRequestRepository verificationRequestRepository) {
        this.userRepository = userRepository;
        this.workerProfileRepository = workerProfileRepository;
        this.verificationRequestRepository = verificationRequestRepository;
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
                                if (!user.isVerified() || !"ACTIVE".equalsIgnoreCase(user.getStatus())) {
                                    user.setVerified(true);
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

    public User register(RegisterRequest req) {
        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Error: Email is already in use!");
        }

        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setRole(req.getRole());
        user.setPhone(req.getPhone());
        user.setAddress(req.getAddress());

        if ("WORKER".equalsIgnoreCase(user.getRole())) {
            user.setVerified(false);
            user.setStatus("UNVERIFIED");
        } else if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            user.setVerified(true);
            user.setStatus("ACTIVE");
        } else {
            user.setVerified(true);
            user.setStatus("ACTIVE");
        }

        User saved = userRepository.save(user);

        if ("WORKER".equalsIgnoreCase(saved.getRole())) {
            String skills = (req.getSkills() != null && !req.getSkills().trim().isEmpty())
                    ? req.getSkills()
                    : "Electrical, Plumbing";
            WorkerProfile profile = new WorkerProfile(
                    saved,
                    skills,
                    1,
                    "Dhaka North (Gulshan, Banani, Uttara)",
                    "Bronze",
                    350.0);
            profile.setAvailable(true);
            workerProfileRepository.save(profile);
        }

        return saved;
    }

    public User registerUserEntity(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Error: Email is already in use!");
        }
        if ("WORKER".equalsIgnoreCase(user.getRole())) {
            user.setVerified(false);
            user.setStatus("UNVERIFIED");
        } else if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            user.setVerified(true);
            user.setStatus("ACTIVE");
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

    public Optional<User> authenticate(LoginRequest loginRequest) {
        Optional<User> userOpt = userRepository.findByEmail(loginRequest.getEmail());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if ("WORKER".equalsIgnoreCase(user.getRole())) {
                verificationRequestRepository.findTopByUserIdOrderBySubmittedAtDesc(user.getId())
                        .ifPresent(req -> {
                            if ("APPROVED".equalsIgnoreCase(req.getStatus())) {
                                if (!user.isVerified() || !"ACTIVE".equalsIgnoreCase(user.getStatus())) {
                                    user.setVerified(true);
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
                try {
                    user.setLatitude(Double.parseDouble(profileMap.get("latitude").toString()));
                } catch (Exception ignored) {}
            }
            if (profileMap.containsKey("longitude") && profileMap.get("longitude") != null) {
                try {
                    user.setLongitude(Double.parseDouble(profileMap.get("longitude").toString()));
                } catch (Exception ignored) {}
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

    public User save(User user) {
        return userRepository.save(user);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
