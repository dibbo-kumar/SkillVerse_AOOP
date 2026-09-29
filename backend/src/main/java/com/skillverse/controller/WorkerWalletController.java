package com.skillverse.controller;

import com.skillverse.dto.WithdrawalRequest;
import com.skillverse.model.WalletTransaction;
import com.skillverse.service.WorkerWalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wallet")
@CrossOrigin(origins = "*")
public class WorkerWalletController {

    private final WorkerWalletService walletService;

    public WorkerWalletController(WorkerWalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/worker/{workerId}")
    public ResponseEntity<?> getWallet(@PathVariable Long workerId) {
        return walletService.getWallet(workerId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().body(Map.of("error", "Worker not found")));
    }

    @GetMapping("/transactions/worker/{workerId}")
    public ResponseEntity<List<WalletTransaction>> getTransactions(@PathVariable Long workerId) {
        return ResponseEntity.ok(walletService.getTransactions(workerId));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> requestWithdrawal(@RequestBody WithdrawalRequest req) {
        try {
            Map<String, Object> result = walletService.requestWithdrawal(req);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
