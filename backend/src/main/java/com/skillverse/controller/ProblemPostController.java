package com.skillverse.controller;

import com.skillverse.dto.ProblemOfferRequest;
import com.skillverse.model.ProblemOffer;
import com.skillverse.model.ProblemPost;
import com.skillverse.model.ServiceBooking;
import com.skillverse.service.ProblemPostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for Customer Problem Posts and Worker Bidding/Offers.
 * Architecture Flow: User/Client -> Controller -> Service -> Repository -> Model (Entity)
 */
@RestController
@RequestMapping("/api/problems")
@CrossOrigin(origins = "*")
public class ProblemPostController {

    private final ProblemPostService problemPostService;

    public ProblemPostController(ProblemPostService problemPostService) {
        this.problemPostService = problemPostService;
    }

    /**
     * Standard CRUD: Save / Post a Problem with RequestBody
     */
    @PostMapping
    public ResponseEntity<?> save(@RequestBody Map<String, Object> req) {
        try {
            ProblemPost saved = problemPostService.saveFromMap(req);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Standard list endpoint for open problems
     */
    @GetMapping
    public ResponseEntity<List<ProblemPost>> getAll() {
        return ResponseEntity.ok(problemPostService.getAllOpenProblems());
    }

    /**
     * Standard CRUD: Get Problem by ID with PathVariable
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return problemPostService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Standard CRUD: Update Problem with PathVariable and RequestBody
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ProblemPost updated) {
        return problemPostService.update(id, updated)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Standard CRUD: Delete Problem with PathVariable
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (problemPostService.delete(id)) {
            return ResponseEntity.ok(Map.of("message", "Problem post deleted"));
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Get Customer's Problems with PathVariable
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<ProblemPost>> getCustomerProblems(@PathVariable Long customerId) {
        return ResponseEntity.ok(problemPostService.getCustomerProblems(customerId));
    }

    /**
     * Worker submits quote offer with PathVariable and RequestBody
     */
    @PostMapping("/{id}/offers")
    public ResponseEntity<?> submitOffer(@PathVariable Long id, @RequestBody Map<String, Object> req) {
        try {
            ProblemOfferRequest offerReq = new ProblemOfferRequest();
            if (req.containsKey("workerId")) {
                offerReq.setWorkerId(Long.valueOf(req.get("workerId").toString()));
            }
            if (req.containsKey("proposedPrice")) {
                offerReq.setProposedPrice(Double.valueOf(req.get("proposedPrice").toString()));
            }
            if (req.containsKey("message")) {
                offerReq.setMessage(req.get("message").toString());
            }
            if (req.containsKey("estimatedArrival")) {
                offerReq.setEstimatedArrival(req.get("estimatedArrival").toString());
            }

            ProblemOffer savedOffer = problemPostService.submitOffer(id, offerReq);
            return ResponseEntity.ok(savedOffer);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get Offers for a Problem with PathVariable
     */
    @GetMapping("/{id}/offers")
    public ResponseEntity<List<ProblemOffer>> getOffersForProblem(@PathVariable Long id) {
        return ResponseEntity.ok(problemPostService.getOffersForProblem(id));
    }

    /**
     * Get Offers by Worker with PathVariable
     */
    @GetMapping("/offers/worker/{workerId}")
    public ResponseEntity<List<ProblemOffer>> getOffersForWorker(@PathVariable Long workerId) {
        return ResponseEntity.ok(problemPostService.getOffersForWorker(workerId));
    }

    /**
     * Customer accepts offer with PathVariable
     */
    @PutMapping("/offers/{offerId}/accept")
    public ResponseEntity<?> acceptOffer(@PathVariable Long offerId) {
        try {
            ServiceBooking booking = problemPostService.acceptOffer(offerId);
            return ResponseEntity.ok(booking);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
