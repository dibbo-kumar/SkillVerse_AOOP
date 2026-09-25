package com.skillverse.controller;

import com.skillverse.dto.ProblemOfferRequest;
import com.skillverse.model.ProblemOffer;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/problem-offers")
@CrossOrigin(origins = "*")
public class ProblemOfferController {

    // In-memory list to store problem offers (No Database)
    private List<ProblemOffer> offerList = new ArrayList<>();
    private Long nextId = 1L;

    public ProblemOfferController() {
        // Sample starter data
        offerList.add(new ProblemOffer(nextId++, 1L, 2L, 750.0, "I have 5 years experience with circuit breakers. Can visit in 30 mins.", "30 mins"));
    }

    // 1. getAll - uses @RequestParam
    @GetMapping
    public List<ProblemOffer> getAll(@RequestParam(required = false) Long problemPostId) {
        if (problemPostId == null) {
            return offerList;
        }

        List<ProblemOffer> result = new ArrayList<>();
        for (ProblemOffer offer : offerList) {
            if (offer.getProblemPostId() != null && offer.getProblemPostId().equals(problemPostId)) {
                result.add(offer);
            }
        }
        return result;
    }

    // 2. getById - uses @PathVariable
    @GetMapping("/{id}")
    public ProblemOffer getById(@PathVariable Long id) {
        for (ProblemOffer offer : offerList) {
            if (offer.getId().equals(id)) {
                return offer;
            }
        }
        return null;
    }

    // 3. save - uses @RequestBody with ProblemOfferRequest object
    @PostMapping
    public ProblemOffer save(@RequestBody ProblemOfferRequest request) {
        ProblemOffer offer = new ProblemOffer();
        offer.setId(nextId++);
        offer.setProblemPostId(request.getProblemPostId());
        offer.setWorkerId(request.getWorkerId());
        offer.setProposedPrice(request.getProposedPrice());
        offer.setMessage(request.getMessage());
        offer.setEstimatedArrival(request.getEstimatedArrival());

        offerList.add(offer);
        return offer;
    }

    // 4. upload - uses @RequestParam
    @PostMapping("/{id}/upload")
    public ProblemOffer upload(@PathVariable Long id, @RequestParam String status) {
        for (ProblemOffer offer : offerList) {
            if (offer.getId().equals(id)) {
                offer.setStatus(status);
                return offer;
            }
        }
        return null;
    }

    // 5. delete - uses @PathVariable
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        for (int i = 0; i < offerList.size(); i++) {
            if (offerList.get(i).getId().equals(id)) {
                offerList.remove(i);
                return "ProblemOffer deleted successfully";
            }
        }
        return "ProblemOffer not found";
    }
}
