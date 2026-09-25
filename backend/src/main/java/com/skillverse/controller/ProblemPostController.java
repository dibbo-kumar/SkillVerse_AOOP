package com.skillverse.controller;

import com.skillverse.dto.ProblemPostRequest;
import com.skillverse.model.ProblemPost;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/problem-posts")
@CrossOrigin(origins = "*")
public class ProblemPostController {

    // In-memory list to store problem posts (No Database)
    private List<ProblemPost> problemList = new ArrayList<>();
    private Long nextId = 1L;

    public ProblemPostController() {
        // Sample starter data
        problemList.add(new ProblemPost(nextId++, 1L, "Electrical", "Short Circuit in Main Board", "Main breaker tripped and sparks were seen.", "Dhanmondi 27, Dhaka", 800.0));
        problemList.add(new ProblemPost(nextId++, 1L, "Plumbing", "Leaking Kitchen Sink Pipe", "Water is continuously dripping under the sink cabinet.", "Banani, Dhaka", 500.0));
    }

    // 1. getAll - uses @RequestParam
    @GetMapping
    public List<ProblemPost> getAll(@RequestParam(required = false) String serviceCategory) {
        if (serviceCategory == null || serviceCategory.isEmpty()) {
            return problemList;
        }

        List<ProblemPost> result = new ArrayList<>();
        for (ProblemPost post : problemList) {
            if (post.getServiceCategory() != null && post.getServiceCategory().equalsIgnoreCase(serviceCategory)) {
                result.add(post);
            }
        }
        return result;
    }

    // 2. getById - uses @PathVariable
    @GetMapping("/{id}")
    public ProblemPost getById(@PathVariable Long id) {
        for (ProblemPost post : problemList) {
            if (post.getId().equals(id)) {
                return post;
            }
        }
        return null;
    }

    // 3. save - uses @RequestBody with ProblemPostRequest object
    @PostMapping
    public ProblemPost save(@RequestBody ProblemPostRequest request) {
        ProblemPost post = new ProblemPost();
        post.setId(nextId++);
        post.setCustomerId(request.getCustomerId());
        post.setServiceCategory(request.getServiceCategory());
        post.setTitle(request.getTitle());
        post.setDescription(request.getDescription());
        post.setApplianceInfo(request.getApplianceInfo());
        post.setPreferredDate(request.getPreferredDate());
        post.setPreferredTime(request.getPreferredTime());
        post.setAddress(request.getAddress());
        post.setBudgetPrice(request.getBudgetPrice());

        problemList.add(post);
        return post;
    }

    // 4. upload - uses @RequestParam
    @PostMapping("/{id}/upload")
    public ProblemPost upload(@PathVariable Long id, @RequestParam String photoUrl) {
        for (ProblemPost post : problemList) {
            if (post.getId().equals(id)) {
                post.setPhotoUrl(photoUrl);
                return post;
            }
        }
        return null;
    }

    // 5. delete - uses @PathVariable
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        for (int i = 0; i < problemList.size(); i++) {
            if (problemList.get(i).getId().equals(id)) {
                problemList.remove(i);
                return "ProblemPost deleted successfully";
            }
        }
        return "ProblemPost not found";
    }
}
