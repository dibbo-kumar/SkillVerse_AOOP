package com.skillverse.controller;

import com.skillverse.service.AIService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AIController {

    private final AIService aiService;

    public AIController(AIService aiService) {
        this.aiService = aiService;
    }

    @GetMapping("/estimate-cost")
    public ResponseEntity<?> estimateCost(@RequestParam String issueDescription) {
        return ResponseEntity.ok(aiService.estimateCost(issueDescription));
    }

    @PostMapping("/chatbot")
    public ResponseEntity<?> chatbotResponse(@RequestBody Map<String, String> request) {
        String msg = request.getOrDefault("message", "");
        String reply = aiService.generateChatbotReply(msg);
        return ResponseEntity.ok(Map.of("response", reply));
    }
}
