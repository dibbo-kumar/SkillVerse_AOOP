package com.skillverse.service;

import com.skillverse.dto.ProblemOfferRequest;
import com.skillverse.dto.ProblemPostRequest;
import com.skillverse.model.*;
import com.skillverse.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class ProblemPostService {

    private final ProblemPostRepository problemPostRepository;
    private final ProblemOfferRepository problemOfferRepository;
    private final ServiceBookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final PlatformSettingRepository settingRepository;

    public ProblemPostService(ProblemPostRepository problemPostRepository,
                              ProblemOfferRepository problemOfferRepository,
                              ServiceBookingRepository bookingRepository,
                              UserRepository userRepository,
                              PlatformSettingRepository settingRepository) {
        this.problemPostRepository = problemPostRepository;
        this.problemOfferRepository = problemOfferRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.settingRepository = settingRepository;
    }

    public double getPlatformCommissionRate() {
        try {
            return settingRepository.findBySettingKey("platform_commission")
                    .map(s -> Double.parseDouble(s.getSettingValue()) / 100.0)
                    .orElse(0.05);
        } catch (Exception e) {
            return 0.05;
        }
    }

    public ProblemPost save(ProblemPostRequest req) {
        User customer = userRepository.findById(req.getCustomerId()).orElse(null);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found: " + req.getCustomerId());
        }

        ProblemPost post = new ProblemPost();
        post.setCustomer(customer);
        post.setServiceCategory(req.getServiceCategory() != null ? req.getServiceCategory() : "General Maintenance");
        post.setTitle(req.getTitle() != null ? req.getTitle() : "Service Problem Request");
        post.setDescription(req.getDescription() != null ? req.getDescription() : "");
        post.setApplianceInfo(req.getApplianceInfo() != null ? req.getApplianceInfo() : "");
        post.setPhotoUrl(req.getPhotoUrl() != null ? req.getPhotoUrl() : "");
        post.setPreferredDate(req.getPreferredDate() != null ? req.getPreferredDate() : "Tomorrow");
        post.setPreferredTime(req.getPreferredTime() != null ? req.getPreferredTime() : "10:00 AM - 12:00 PM");
        post.setAddress(req.getAddress() != null ? req.getAddress() : customer.getAddress());
        post.setBudgetPrice(req.getBudgetPrice() != null ? req.getBudgetPrice() : 1000.0);
        post.setStatus("OPEN");

        return problemPostRepository.save(post);
    }

    public ProblemPost saveFromMap(Map<String, Object> req) {
        Long customerId = Long.valueOf(req.get("customerId").toString());
        User customer = userRepository.findById(customerId).orElse(null);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found: " + customerId);
        }

        ProblemPost post = new ProblemPost();
        post.setCustomer(customer);
        post.setServiceCategory(req.getOrDefault("serviceCategory", "General Maintenance").toString());
        post.setTitle(req.getOrDefault("title", "Service Problem Request").toString());
        post.setDescription(req.getOrDefault("description", "").toString());
        post.setApplianceInfo(req.getOrDefault("applianceInfo", "").toString());
        post.setPhotoUrl(req.getOrDefault("photoUrl", "").toString());
        post.setPreferredDate(req.getOrDefault("preferredDate", "Tomorrow").toString());
        post.setPreferredTime(req.getOrDefault("preferredTime", "10:00 AM - 12:00 PM").toString());
        post.setAddress(req.getOrDefault("address", customer.getAddress()).toString());
        post.setBudgetPrice(Double.valueOf(req.getOrDefault("budgetPrice", "1000").toString()));
        post.setStatus("OPEN");

        return problemPostRepository.save(post);
    }

    public List<ProblemPost> getAllOpenProblems() {
        return problemPostRepository.findByStatusOrderByCreatedAtDesc("OPEN");
    }

    public List<ProblemPost> getCustomerProblems(Long customerId) {
        return problemPostRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
    }

    public Optional<ProblemPost> getById(Long id) {
        return problemPostRepository.findById(id);
    }

    public Optional<ProblemPost> update(Long id, ProblemPost updated) {
        return problemPostRepository.findById(id).map(post -> {
            if (updated.getTitle() != null) post.setTitle(updated.getTitle());
            if (updated.getDescription() != null) post.setDescription(updated.getDescription());
            if (updated.getServiceCategory() != null) post.setServiceCategory(updated.getServiceCategory());
            if (updated.getBudgetPrice() != null) post.setBudgetPrice(updated.getBudgetPrice());
            if (updated.getAddress() != null) post.setAddress(updated.getAddress());
            return problemPostRepository.save(post);
        });
    }

    public boolean delete(Long id) {
        if (problemPostRepository.existsById(id)) {
            problemPostRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public ProblemOffer submitOffer(Long id, ProblemOfferRequest req) {
        ProblemPost post = problemPostRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Problem post not found: " + id));

        Long workerId = req.getWorkerId();
        User worker = userRepository.findById(workerId)
                .orElseThrow(() -> new NoSuchElementException("Worker not found: " + workerId));

        boolean isVerifiedWorker = Boolean.TRUE.equals(worker.isVerified())
                && !"SUSPENDED".equalsIgnoreCase(worker.getStatus())
                && !"REJECTED".equalsIgnoreCase(worker.getStatus())
                && !"BANNED".equalsIgnoreCase(worker.getStatus());
        if (!isVerifiedWorker) {
            throw new IllegalStateException("Worker is unverified or under review. Admin verification is required to submit quotes on problem posts.");
        }
        if (!"ACTIVE".equalsIgnoreCase(worker.getStatus())) {
            worker.setStatus("ACTIVE");
            userRepository.save(worker);
        }

        List<ServiceBooking> workerBookings = bookingRepository.findByWorkerId(workerId);
        boolean hasWarrantyClaim = workerBookings.stream().anyMatch(b ->
                "WARRANTY_CLAIMED".equalsIgnoreCase(b.getWarrantyStatus()) ||
                "WARRANTY_ACCEPTED".equalsIgnoreCase(b.getWarrantyStatus())
        );
        if (hasWarrantyClaim) {
            throw new IllegalStateException("Cannot submit quotes. You have an active warranty claim that must be resolved first.");
        }

        List<String> activeStatuses = List.of("CONFIRMED", "ON_THE_WAY", "ARRIVED", "IN_PROGRESS", "COMPLETION_REQUESTED");
        boolean isBusy = workerBookings.stream().anyMatch(b -> activeStatuses.contains(b.getStatus()));
        if (isBusy) {
            throw new IllegalStateException("Worker already has an active job in progress. Complete or finalize the current job before submitting offers on new problems.");
        }

        Double price = req.getProposedPrice();
        String msg = req.getMessage() != null ? req.getMessage() : "";
        String arrival = req.getEstimatedArrival() != null ? req.getEstimatedArrival() : "Within 2 hours";

        Optional<ProblemOffer> existingOffer = problemOfferRepository.findByProblemPostIdAndWorkerId(id, workerId);
        ProblemOffer offer;
        if (existingOffer.isPresent()) {
            offer = existingOffer.get();
            offer.setProposedPrice(price);
            offer.setMessage(msg);
            offer.setEstimatedArrival(arrival);
            offer.setStatus("PENDING");
        } else {
            offer = new ProblemOffer(post, worker, price, msg, arrival);
        }

        return problemOfferRepository.save(offer);
    }

    public List<ProblemOffer> getOffersForProblem(Long id) {
        return problemOfferRepository.findByProblemPostId(id);
    }

    public List<ProblemOffer> getOffersForWorker(Long workerId) {
        return problemOfferRepository.findByWorkerId(workerId);
    }

    public ServiceBooking acceptOffer(Long offerId) {
        ProblemOffer acceptedOffer = problemOfferRepository.findById(offerId)
                .orElseThrow(() -> new NoSuchElementException("Problem offer not found: " + offerId));

        ProblemPost post = acceptedOffer.getProblemPost();
        User worker = acceptedOffer.getWorker();

        boolean isVerifiedWorker = worker != null && Boolean.TRUE.equals(worker.isVerified())
                && !"SUSPENDED".equalsIgnoreCase(worker.getStatus())
                && !"REJECTED".equalsIgnoreCase(worker.getStatus())
                && !"BANNED".equalsIgnoreCase(worker.getStatus());
        if (!isVerifiedWorker) {
            throw new IllegalStateException("Cannot assign problem to an unverified technician.");
        }
        if (worker != null && !"ACTIVE".equalsIgnoreCase(worker.getStatus())) {
            worker.setStatus("ACTIVE");
            userRepository.save(worker);
        }

        List<String> activeStatuses = List.of("CONFIRMED", "ON_THE_WAY", "ARRIVED", "IN_PROGRESS", "COMPLETION_REQUESTED");
        List<ServiceBooking> workerBookings = bookingRepository.findByWorkerId(worker.getId());
        boolean isBusy = workerBookings.stream().anyMatch(b -> activeStatuses.contains(b.getStatus()));
        if (isBusy) {
            throw new IllegalStateException("This technician currently has an active job in progress and cannot accept new jobs at this time.");
        }

        acceptedOffer.setStatus("ACCEPTED");
        problemOfferRepository.save(acceptedOffer);

        post.setStatus("ASSIGNED");
        problemPostRepository.save(post);

        Double agreedPrice = acceptedOffer.getProposedPrice();
        double commissionRate = getPlatformCommissionRate();
        double commission = Math.round(agreedPrice * commissionRate * 100.0) / 100.0;
        double netEarning = agreedPrice - commission;

        ServiceBooking booking = new ServiceBooking();
        booking.setCustomer(post.getCustomer());
        booking.setWorker(worker);
        booking.setServiceType(post.getServiceCategory());
        booking.setBookingSource("POSTED_PROBLEM");
        booking.setScheduledTime(LocalDateTime.now().plusDays(1));
        booking.setPreferredDate(post.getPreferredDate());
        booking.setPreferredTime(post.getPreferredTime());
        booking.setAddress(post.getAddress());
        booking.setDescription(post.getTitle() + " — " + post.getDescription());
        booking.setApplianceDetails(post.getApplianceInfo());
        booking.setBeforePhoto(post.getPhotoUrl());

        booking.setEstimatedCost(agreedPrice);
        booking.setCustomerOfferPrice(post.getBudgetPrice());
        booking.setWorkerCounterPrice(agreedPrice);
        booking.setAgreedCost(agreedPrice);
        booking.setPlatformCommission(commission);
        booking.setWorkerNetEarning(netEarning);

        booking.setStatus("CONFIRMED");

        Random random = new Random();
        booking.setStartVerificationCode(String.format("%04d", random.nextInt(10000)));
        booking.setCompletionVerificationCode(String.format("%04d", random.nextInt(10000)));
        booking.setLiveLocation("23.8103, 90.4125");

        ServiceBooking savedBooking = bookingRepository.save(booking);

        List<ProblemOffer> allOffers = problemOfferRepository.findByProblemPostId(post.getId());
        for (ProblemOffer o : allOffers) {
            if (!o.getId().equals(offerId)) {
                o.setStatus("DECLINED");
                problemOfferRepository.save(o);
            }
        }

        return savedBooking;
    }
}
