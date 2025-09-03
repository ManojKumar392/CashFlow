package com.CashFlow.Dashboard.Services;

import com.CashFlow.Dashboard.Entities.Transaction;
import com.CashFlow.Dashboard.Entities.User;
import com.CashFlow.Dashboard.Repository.TransactionRepository;
import org.springframework.stereotype.Service;
import com.CashFlow.Dashboard.Services.UserService;

import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserService userService;


    public TransactionService(TransactionRepository transactionRepository, UserService userService) {
        this.transactionRepository = transactionRepository;
        this.userService = userService;
    }

    public Transaction addTransaction(Transaction transaction) {
    if (transaction.getUser() == null) {
        throw new IllegalArgumentException("Transaction must have a user set");
    }
    return transactionRepository.save(transaction);
}

    // Get all transactions
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    // Find a transaction by ID
    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id).orElse(null);
    }

    // Update (save works for both add + update)
    public Transaction updateTransaction(Long id, Transaction updatedTransaction) {
        Transaction existing = transactionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Transaction not found"));

        existing.setAmount(updatedTransaction.getAmount());
        existing.setType(updatedTransaction.getType());
        existing.setCategory(updatedTransaction.getCategory());
        existing.setDescription(updatedTransaction.getDescription());
        existing.setDate(updatedTransaction.getDate());

        return transactionRepository.save(existing);
    }

    // Delete
    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }
}
