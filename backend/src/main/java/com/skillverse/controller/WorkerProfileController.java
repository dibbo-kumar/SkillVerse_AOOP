package com.skillverse.controller;

import com.skillverse.dto.WorkerProfileRequest;
import com.skillverse.model.WorkerProfile;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/worker-profiles")
@CrossOrigin(origins = "*")
public class WorkerProfileController {

    // In-memory list to store worker profiles (No Database)
    private List<WorkerProfile> profileList = new ArrayList<>();
    private Long nextId = 1L;

    public WorkerProfileController() {
        // Sample starter data
        profileList.add(new WorkerProfile(nextId++, 2L, "Electrical Wiring, AC Repair", 5, "Uttara, Dhaka", "Gold", 450.0));
    }

    // 1. getAll - uses @RequestParam
    @GetMapping
    public List<WorkerProfile> getAll(@RequestParam(required = false) String serviceArea) {
        if (serviceArea == null || serviceArea.isEmpty()) {
            return profileList;
        }

        List<WorkerProfile> result = new ArrayList<>();
        for (WorkerProfile profile : profileList) {
            if (profile.getServiceArea() != null && profile.getServiceArea().toLowerCase().contains(serviceArea.toLowerCase())) {
                result.add(profile);
            }
        }
        return result;
    }

    // 2. getById - uses @PathVariable
    @GetMapping("/{id}")
    public WorkerProfile getById(@PathVariable Long id) {
        for (WorkerProfile profile : profileList) {
            if (profile.getId().equals(id)) {
                return profile;
            }
        }
        return null;
    }

    // 3. save - uses @RequestBody with WorkerProfileRequest object
    @PostMapping
    public WorkerProfile save(@RequestBody WorkerProfileRequest request) {
        WorkerProfile profile = new WorkerProfile();
        profile.setId(nextId++);
        profile.setUserId(request.getUserId());
        profile.setSkills(request.getSkills());
        profile.setExperienceYears(request.getExperienceYears());
        profile.setServiceArea(request.getServiceArea());
        profile.setCareerLevel(request.getCareerLevel());
        profile.setHourlyRate(request.getHourlyRate());
        profile.setBasePrice(request.getBasePrice() != null ? request.getBasePrice() : 300.0);

        profileList.add(profile);
        return profile;
    }

    // 4. upload - uses @RequestParam
    @PostMapping("/{id}/upload")
    public WorkerProfile upload(@PathVariable Long id, @RequestParam String serviceArea) {
        for (WorkerProfile profile : profileList) {
            if (profile.getId().equals(id)) {
                profile.setServiceArea(serviceArea);
                return profile;
            }
        }
        return null;
    }

    // 5. delete - uses @PathVariable
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        for (int i = 0; i < profileList.size(); i++) {
            if (profileList.get(i).getId().equals(id)) {
                profileList.remove(i);
                return "WorkerProfile deleted successfully";
            }
        }
        return "WorkerProfile not found";
    }
}
