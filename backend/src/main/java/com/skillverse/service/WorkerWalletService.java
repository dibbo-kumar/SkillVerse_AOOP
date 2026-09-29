package com.skillverse.service;

import com.skillverse.dto.WithdrawalRequest;
import com.skillverse.model.User;
import com.skillverse.model.WalletTransaction;
import com.skillverse.model.WorkerWallet;
import com.skillverse.repository.UserRepository;
import com.skillverse.repository.WalletTransactionRepository;
import com.skillverse.repository.WorkerWalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class WorkerWalletService {

    private final WorkerWalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public WorkerWalletService(WorkerWalletRepository walletRepository,
                               WalletTransactionRepository transactionRepository,
                               UserRepository userRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public Optional<WorkerWallet> getWallet(Long workerId) {
        Optional<User> workerOpt = userRepository.findById(workerId);
        if (workerOpt.isEmpty()) {
            return Optional.empty();
        }
        User worker = workerOpt.get();

        WorkerWallet wallet = walletRepository.findByWorkerId(workerId)
                .orElseGet(() -> walletRepository.save(new WorkerWallet(worker)));

        if (wallet.getBalance() == null || wallet.getBalance() < 0.0) {
            wallet.setBalance(0.0);
            walletRepository.save(wallet);
        }

        return Optional.of(wallet);
    }

    public List<WalletTransaction> getTransactions(Long workerId) {
        return transactionRepository.findByWorkerIdOrderByCreatedAtDesc(workerId);
    }

    public Map<String, Object> requestWithdrawal(WithdrawalRequest req) {
        Long workerId = req.getWorkerId();
        Double amount = req.getAmount();
        String method = req.getMethod() != null ? req.getMethod() : "bKash";
        String accountNo = req.getAccountNo() != null ? req.getAccountNo() : "";
        String bankName = req.getBankName() != null ? req.getBankName() : "";
        String branchName = req.getBranchName() != null ? req.getBranchName() : "";
        String accountHolder = req.getAccountHolder() != null ? req.getAccountHolder() : "";

        User worker = userRepository.findById(workerId).orElse(null);
        if (worker == null) {
            throw new IllegalArgumentException("Worker not found");
        }

        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Cashout amount must be greater than 0.");
        }

        WorkerWallet wallet = walletRepository.findByWorkerId(workerId)
                .orElseGet(() -> walletRepository.save(new WorkerWallet(worker)));

        double currentBal = wallet.getBalance() != null ? Math.max(0.0, wallet.getBalance()) : 0.0;
        if (currentBal < amount) {
            throw new IllegalArgumentException("Insufficient available balance for cashout. Available balance: ৳" + currentBal);
        }

        double newBalance = Math.max(0.0, Math.round((currentBal - amount) * 100.0) / 100.0);
        wallet.setBalance(newBalance);
        double totalWithdrawn = (wallet.getTotalWithdrawals() != null ? wallet.getTotalWithdrawals() : 0.0) + amount;
        wallet.setTotalWithdrawals(Math.round(totalWithdrawn * 100.0) / 100.0);
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);

        String desc;
        if ("Bank".equalsIgnoreCase(method) || "Bank Transfer".equalsIgnoreCase(method)) {
            desc = "Bank Cashout to " + (bankName.isEmpty() ? "Bank" : bankName) + " (A/C: " + accountNo + ", Branch: "
                    + (branchName.isEmpty() ? "Principal" : branchName) + ", Holder: " + accountHolder + ")";
        } else {
            desc = "Cashout via " + method + " (" + accountNo + ")";
        }

        WalletTransaction tx = new WalletTransaction(worker, "WITHDRAWAL", -amount, desc, null);
        tx.setStatus("COMPLETED");
        transactionRepository.save(tx);

        return Map.of(
                "message", "Cashout of ৳" + amount + " via " + method + " processed successfully.",
                "newBalance", newBalance,
                "transaction", tx
        );
    }

    public WorkerWallet save(WorkerWallet wallet) {
        return walletRepository.save(wallet);
    }
}
