package com.skillverse.controller;

import com.skillverse.service.AIService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for AI Diagnostics, Cost Estimation, and Chatbot.
 * Architecture Flow: User/Client -> Controller -> Service -> Model
 */
@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AIController {

    private final AIService aiService;

    public AIController(AIService aiService) {
        this.aiService = aiService;
    }

    /**
     * AI Cost Estimation with RequestParam
     */
    @GetMapping("/estimate-cost")
    public ResponseEntity<?> estimateCost(@RequestParam String issueDescription) {
        return ResponseEntity.ok(aiService.estimateCost(issueDescription));
    }

    /**
     * AI Diagnostic Chatbot with RequestBody
     */
    @PostMapping("/chatbot")
    public ResponseEntity<?> chatbotResponse(@RequestBody Map<String, String> request) {
        String msg = request != null ? request.get("message") : "";
        return ResponseEntity.ok(aiService.chatbotResponse(msg));
    }
}
