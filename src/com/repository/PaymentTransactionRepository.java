package com.repository;

import com.entities.PaymentTransaction;
import java.util.List;

public interface PaymentTransactionRepository {

    int save(PaymentTransaction tx);

    void update(PaymentTransaction tx);

    PaymentTransaction findById(int id);

    List<PaymentTransaction> findByUserId(int userId);

    PaymentTransaction findByOrderId(int orderId);

    void updateStatus(int transactionId, String status);

    void logAuthorization(int txId, boolean authorized);

    double computeTotalPayments(long start, long end);

    int countSuccessfulPayments(long start, long end);
}