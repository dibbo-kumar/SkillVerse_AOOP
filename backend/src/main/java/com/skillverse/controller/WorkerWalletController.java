package com.skillverse.controller;

import com.skillverse.dto.WorkerWalletRequest;
import com.skillverse.model.WorkerWallet;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/worker-wallets")
@CrossOrigin(origins = "*")
public class WorkerWalletController {

    // In-memory list to store worker wallets (No Database)
    private List<WorkerWallet> walletList = new ArrayList<>();
    private Long nextId = 1L;

    public WorkerWalletController() {
        // Sample starter data
        walletList.add(new WorkerWallet(nextId++, 2L, 12500.0, 18500.0));
    }

    // 1. getAll - uses @RequestParam
    @GetMapping
    public List<WorkerWallet> getAll(@RequestParam(required = false) Long workerId) {
        if (workerId == null) {
            return walletList;
        }

        List<WorkerWallet> result = new ArrayList<>();
        for (WorkerWallet wallet : walletList) {
            if (wallet.getWorkerId() != null && wallet.getWorkerId().equals(workerId)) {
                result.add(wallet);
            }
        }
        return result;
    }

    // 2. getById - uses @PathVariable
    @GetMapping("/{id}")
    public WorkerWallet getById(@PathVariable Long id) {
        for (WorkerWallet wallet : walletList) {
            if (wallet.getId().equals(id)) {
                return wallet;
            }
        }
        return null;
    }

    // 3. save - uses @RequestBody with WorkerWalletRequest object
    @PostMapping
    public WorkerWallet save(@RequestBody WorkerWalletRequest request) {
        WorkerWallet wallet = new WorkerWallet();
        wallet.setId(nextId++);
        wallet.setWorkerId(request.getWorkerId());
        wallet.setBalance(request.getBalance() != null ? request.getBalance() : 0.0);
        wallet.setTotalEarnings(request.getTotalEarnings() != null ? request.getTotalEarnings() : 0.0);

        walletList.add(wallet);
        return wallet;
    }

    // 4. upload - uses @RequestParam
    @PostMapping("/{id}/upload")
    public WorkerWallet upload(@PathVariable Long id, @RequestParam Double depositAmount) {
        for (WorkerWallet wallet : walletList) {
            if (wallet.getId().equals(id)) {
                double currentBalance = wallet.getBalance() != null ? wallet.getBalance() : 0.0;
                double add = depositAmount != null ? depositAmount : 0.0;
                wallet.setBalance(currentBalance + add);
                return wallet;
            }
        }
        return null;
    }

    // 5. delete - uses @PathVariable
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        for (int i = 0; i < walletList.size(); i++) {
            if (walletList.get(i).getId().equals(id)) {
                walletList.remove(i);
                return "WorkerWallet deleted successfully";
            }
        }
        return "WorkerWallet not found";
    }
}
