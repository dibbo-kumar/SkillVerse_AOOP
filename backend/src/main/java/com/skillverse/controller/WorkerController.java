package com.skillverse.controller;

import com.skillverse.model.WorkerProfile;
import com.skillverse.service.WorkerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/workers")
@CrossOrigin(origins = "*")
public class WorkerController {

    private final WorkerService workerService;

    public WorkerController(WorkerService workerService) {
        this.workerService = workerService;
    }

    @GetMapping
    public ResponseEntity<List<WorkerProfile>> getAllWorkers() {
        return ResponseEntity.ok(workerService.getAllWorkers());
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<Map<String, Object>>> getNearbyWorkers(
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            @RequestParam(required = false) Double radius,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String query) {
        return ResponseEntity.ok(workerService.getNearbyWorkers(lat, lon, radius, category, query));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkerProfile> getWorkerProfile(@PathVariable Long id) {
        return workerService.getWorkerProfile(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/verify")
    public ResponseEntity<?> verifyWorker(
            @PathVariable Long id,
            @RequestParam String nid,
            @RequestParam(required = false) String frontPhoto) {
        return workerService.verifyWorker(id, nid, frontPhoto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/profile")
    public ResponseEntity<?> updateProfile(
            @PathVariable Long id,
            @RequestBody WorkerProfile updatedProfile) {
        return workerService.updateProfile(id, updatedProfile)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/location")
    public ResponseEntity<?> updateLocation(
            @PathVariable Long id,
            @RequestParam Double lat,
            @RequestParam Double lon,
            @RequestParam(required = false) String area) {
        return workerService.updateLocation(id, lat, lon, area)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWorker(@PathVariable Long id) {
        workerService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Worker profile deleted successfully"));
    }
}
