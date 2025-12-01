package com.managers.payment;

import com.entities.PaymentTransaction;
import com.repository.PaymentTransactionRepository;

public class PaymentTransactionManager {

    private final PaymentTransactionRepository repo;

    public PaymentTransactionManager(PaymentTransactionRepository repo) {
        this.repo = repo;
    }

    public void updateStatus(PaymentTransaction txn,
            PaymentTransaction.Status newStatus) {
        txn.setStatus(newStatus);
        repo.update(txn);
    }

    public PaymentTransaction getById(int id) {
        return repo.findById(id);
    }
}