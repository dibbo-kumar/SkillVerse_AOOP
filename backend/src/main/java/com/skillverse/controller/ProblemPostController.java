package com.skillverse.controller;

import com.skillverse.dto.ProblemOfferRequest;
import com.skillverse.dto.ProblemPostRequest;
import com.skillverse.model.ProblemOffer;
import com.skillverse.model.ProblemPost;
import com.skillverse.model.ServiceBooking;
import com.skillverse.service.ProblemPostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/problems")
@CrossOrigin(origins = "*")
public class ProblemPostController {

    private final ProblemPostService problemPostService;

    public ProblemPostController(ProblemPostService problemPostService) {
        this.problemPostService = problemPostService;
    }

    @PostMapping
    public ResponseEntity<?> createProblemPost(@RequestBody ProblemPostRequest req) {
        try {
            ProblemPost saved = problemPostService.createProblemPost(req);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<ProblemPost>> getAllOpenProblems() {
        return ResponseEntity.ok(problemPostService.getAllOpenProblems());
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<ProblemPost>> getCustomerProblems(@PathVariable Long customerId) {
        return ResponseEntity.ok(problemPostService.getCustomerProblems(customerId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProblemById(@PathVariable Long id) {
        return problemPostService.getProblemById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/offers")
    public ResponseEntity<?> submitOffer(@PathVariable Long id, @RequestBody ProblemOfferRequest req) {
        try {
            ProblemOffer offer = problemPostService.submitOffer(id, req);
            return ResponseEntity.ok(offer);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}/offers")
    public ResponseEntity<List<ProblemOffer>> getOffersForProblem(@PathVariable Long id) {
        return ResponseEntity.ok(problemPostService.getOffersForProblem(id));
    }

    @GetMapping("/offers/worker/{workerId}")
    public ResponseEntity<List<ProblemOffer>> getOffersForWorker(@PathVariable Long workerId) {
        return ResponseEntity.ok(problemPostService.getOffersForWorker(workerId));
    }

    @PutMapping("/offers/{offerId}/accept")
    public ResponseEntity<?> acceptOffer(@PathVariable Long offerId) {
        try {
            ServiceBooking savedBooking = problemPostService.acceptOffer(offerId);
            return ResponseEntity.ok(savedBooking);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProblem(@PathVariable Long id) {
        problemPostService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Problem post deleted"));
    }
}
