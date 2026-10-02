package com.skillverse.service;

import com.skillverse.model.User;
import com.skillverse.model.WalletTransaction;
import com.skillverse.model.WorkerWallet;
import com.skillverse.repository.UserRepository;
import com.skillverse.repository.WalletTransactionRepository;
import com.skillverse.repository.WorkerWalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class WalletService {

    private final WorkerWalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public WalletService(WorkerWalletRepository walletRepository,
                         WalletTransactionRepository transactionRepository,
                         UserRepository userRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public WorkerWallet getWallet(Long workerId) {
        User worker = userRepository.findById(workerId)
                .orElseThrow(() -> new NoSuchElementException("Worker not found: " + workerId));

        WorkerWallet wallet = walletRepository.findByWorkerId(workerId)
                .orElseGet(() -> walletRepository.save(new WorkerWallet(worker)));

        if (wallet.getBalance() == null || wallet.getBalance() < 0.0) {
            wallet.setBalance(0.0);
            walletRepository.save(wallet);
        }
        return wallet;
    }

    public List<WalletTransaction> getTransactions(Long workerId) {
        return transactionRepository.findByWorkerIdOrderByCreatedAtDesc(workerId);
    }

    public Map<String, Object> requestWithdrawal(Long workerId, Double amount, String method, String accountNo,
                                                String bankName, String branchName, String accountHolder) {
        User worker = userRepository.findById(workerId)
                .orElseThrow(() -> new NoSuchElementException("Worker not found: " + workerId));

        if ("SUSPENDED".equalsIgnoreCase(worker.getStatus())) {
            throw new IllegalStateException("Your account has been suspended by Administrator. Wallet withdrawals are disabled while suspended.");
        }

        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Cashout amount must be greater than 0.");
        }

        WorkerWallet wallet = walletRepository.findByWorkerId(workerId)
                .orElseGet(() -> walletRepository.save(new WorkerWallet(worker)));

        double currentBal = wallet.getBalance() != null ? Math.max(0.0, wallet.getBalance()) : 0.0;
        if (currentBal < amount) {
            throw new IllegalStateException("Insufficient available balance for cashout. Available balance: ৳" + currentBal);
        }

        double newBalance = Math.max(0.0, Math.round((currentBal - amount) * 100.0) / 100.0);
        wallet.setBalance(newBalance);
        double totalWithdrawn = (wallet.getTotalWithdrawals() != null ? wallet.getTotalWithdrawals() : 0.0) + amount;
        wallet.setTotalWithdrawals(Math.round(totalWithdrawn * 100.0) / 100.0);
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);

        String desc;
        if ("Bank".equalsIgnoreCase(method) || "Bank Transfer".equalsIgnoreCase(method)) {
            desc = "Bank Cashout to " + (bankName != null && !bankName.isEmpty() ? bankName : "Bank") + " (A/C: " + accountNo + ", Branch: " + (branchName != null && !branchName.isEmpty() ? branchName : "Principal") + ", Holder: " + accountHolder + ")";
        } else {
            desc = "Cashout via " + method + " (" + accountNo + ")";
        }

        WalletTransaction tx = new WalletTransaction(
                worker, "WITHDRAWAL", -amount, desc, null
        );
        tx.setStatus("COMPLETED");
        transactionRepository.save(tx);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "Cashout of ৳" + amount + " via " + method + " processed successfully.");
        result.put("wallet", wallet);
        result.put("transaction", tx);
        return result;
    }
}
