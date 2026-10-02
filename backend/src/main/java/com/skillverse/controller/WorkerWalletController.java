package com.skillverse.controller;

import com.skillverse.model.WalletTransaction;
import com.skillverse.model.WorkerWallet;
import com.skillverse.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for Worker Wallets and Cashouts/Withdrawals.
 * Architecture Flow: User/Client -> Controller -> Service -> Repository -> Model (Entity)
 */
@RestController
@RequestMapping("/api/wallet")
@CrossOrigin(origins = "*")
public class WorkerWalletController {

    private final WalletService walletService;

    public WorkerWalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    /**
     * Standard CRUD: Get Worker Wallet with PathVariable
     */
    @GetMapping("/worker/{workerId}")
    public ResponseEntity<?> getWallet(@PathVariable Long workerId) {
        try {
            WorkerWallet wallet = walletService.getWallet(workerId);
            return ResponseEntity.ok(wallet);
        } catch (java.util.NoSuchElementException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get Worker Wallet Transactions with PathVariable
     */
    @GetMapping("/transactions/worker/{workerId}")
    public ResponseEntity<List<WalletTransaction>> getTransactions(@PathVariable Long workerId) {
        return ResponseEntity.ok(walletService.getTransactions(workerId));
    }

    /**
     * Request Cashout / Withdrawal with RequestBody
     */
    @PostMapping("/withdraw")
    public ResponseEntity<?> requestWithdrawal(@RequestBody Map<String, Object> req) {
        try {
            Long workerId = Long.valueOf(req.get("workerId").toString());
            Double amount = Double.valueOf(req.get("amount").toString());
            String method = req.getOrDefault("method", "bKash").toString();
            String accountNo = req.getOrDefault("accountNo", "").toString();
            String bankName = req.getOrDefault("bankName", "").toString();
            String branchName = req.getOrDefault("branchName", "").toString();
            String accountHolder = req.getOrDefault("accountHolder", "").toString();

            Map<String, Object> result = walletService.requestWithdrawal(workerId, amount, method, accountNo, bankName, branchName, accountHolder);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException | IllegalStateException | java.util.NoSuchElementException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
