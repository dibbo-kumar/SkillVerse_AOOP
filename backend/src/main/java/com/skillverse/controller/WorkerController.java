package com.skillverse.controller;

import com.skillverse.model.WorkerProfile;
import com.skillverse.service.WorkerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for Worker Profiles & Technicians.
 * Architecture Flow: User/Client -> Controller -> Service -> Repository -> Model (Entity)
 */
@RestController
@RequestMapping("/api/workers")
@CrossOrigin(origins = "*")
public class WorkerController {

    private final WorkerService workerService;

    public WorkerController(WorkerService workerService) {
        this.workerService = workerService;
    }

    /**
     * Get all worker profiles
     */
    @GetMapping
    public ResponseEntity<List<WorkerProfile>> getAll() {
        return ResponseEntity.ok(workerService.getAllWorkers());
    }

    /**
     * Filter nearby workers with RequestParam
     */
    @GetMapping("/nearby")
    public ResponseEntity<List<Map<String, Object>>> getNearbyWorkers(
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            @RequestParam(required = false) Double radius,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String query
    ) {
        return ResponseEntity.ok(workerService.getNearbyWorkers(lat, lon, radius, category, query));
    }

    /**
     * Standard CRUD: Get Worker Profile by user ID with PathVariable
     */
    @GetMapping("/{id}")
    public ResponseEntity<WorkerProfile> getById(@PathVariable Long id) {
        return workerService.getWorkerProfileByUserId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Standard CRUD: Save Worker Profile with RequestBody
     */
    @PostMapping
    public ResponseEntity<WorkerProfile> save(@RequestBody WorkerProfile profile) {
        return ResponseEntity.ok(workerService.save(profile));
    }

    /**
     * Quick verification with PathVariable & RequestParam
     */
    @PostMapping("/{id}/verify")
    public ResponseEntity<?> verifyWorker(@PathVariable Long id,
                                          @RequestParam String nid,
                                          @RequestParam(required = false) String frontPhoto) {
        return workerService.verifyWorker(id, nid, frontPhoto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Standard CRUD: Update Worker Profile with PathVariable and RequestBody
     */
    @PutMapping("/{id}/profile")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody WorkerProfile updatedProfile) {
        return workerService.updateProfile(id, updatedProfile)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update worker live location with PathVariable and RequestParam
     */
    @PutMapping("/{id}/location")
    public ResponseEntity<?> updateLocation(@PathVariable Long id,
                                            @RequestParam Double lat,
                                            @RequestParam Double lon,
                                            @RequestParam(required = false) String area) {
        return workerService.updateLocation(id, lat, lon, area)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Standard CRUD: Delete Worker Profile by ID with PathVariable
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (workerService.delete(id)) {
            return ResponseEntity.ok(Map.of("message", "Worker profile deleted successfully"));
        }
        return ResponseEntity.notFound().build();
    }
}
