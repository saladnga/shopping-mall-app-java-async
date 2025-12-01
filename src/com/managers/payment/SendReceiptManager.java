package com.managers.payment;

import com.entities.PaymentTransaction;
import com.services.EmailService;

public class SendReceiptManager {

    private final EmailService emailService;

    public SendReceiptManager(EmailService emailService) {
        this.emailService = emailService;
    }

    public void sendReceipt(PaymentTransaction txn) {

        String subject = "Your Payment Receipt";

        String body = """
                Thank you for your purchase!

                Transaction ID: %d
                Amount: $%.2f
                Payment Method: %s
                Date: %d

                """.formatted(
                txn.getId(),
                txn.getAmount(),
                txn.getPaymentMethod(),
                txn.getTimestamp());

        emailService.sendEmail(txn.getUserId(), subject, body);
    }
}