package com.ebilling.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a payment transaction against a bill.
 */
public class Payment {

    public enum PaymentMode {
        CASH, CARD, UPI, NETBANKING, CHEQUE
    }

    private String paymentId;
    private String billId;
    private String customerId;
    private double amount;
    private PaymentMode paymentMode;
    private LocalDateTime paymentDateTime;
    private String transactionRef;
    private String remarks;

    public Payment() {}

    public Payment(String paymentId, String billId, String customerId,
                   double amount, PaymentMode paymentMode, String transactionRef) {
        this.paymentId = paymentId;
        this.billId = billId;
        this.customerId = customerId;
        this.amount = amount;
        this.paymentMode = paymentMode;
        this.transactionRef = transactionRef;
        this.paymentDateTime = LocalDateTime.now();
    }

    public String getFormattedDateTime() {
        return paymentDateTime.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm"));
    }

    // Getters & Setters
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public String getBillId() { return billId; }
    public void setBillId(String billId) { this.billId = billId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public PaymentMode getPaymentMode() { return paymentMode; }
    public void setPaymentMode(PaymentMode paymentMode) { this.paymentMode = paymentMode; }

    public LocalDateTime getPaymentDateTime() { return paymentDateTime; }
    public void setPaymentDateTime(LocalDateTime paymentDateTime) { this.paymentDateTime = paymentDateTime; }

    public String getTransactionRef() { return transactionRef; }
    public void setTransactionRef(String transactionRef) { this.transactionRef = transactionRef; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    @Override
    public String toString() {
        return String.format("Payment[%s] Bill:%s ₹%.2f via %s on %s",
                paymentId, billId, amount, paymentMode, getFormattedDateTime());
    }
}
