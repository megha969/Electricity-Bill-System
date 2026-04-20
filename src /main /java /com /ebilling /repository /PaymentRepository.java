package com.ebilling.repository;

import com.ebilling.model.Payment;

import java.util.*;
import java.util.stream.Collectors;

/**
 * In-memory payment store.
 */
public class PaymentRepository {

    private final Map<String, Payment> store = new LinkedHashMap<>();
    private int counter = 1;

    public Payment save(Payment payment) {
        if (payment.getPaymentId() == null || payment.getPaymentId().isBlank()) {
            payment.setPaymentId(String.format("PAY%05d", counter++));
        }
        store.put(payment.getPaymentId(), payment);
        return payment;
    }

    public Optional<Payment> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<Payment> findByBillId(String billId) {
        return store.values().stream()
                .filter(p -> p.getBillId().equals(billId))
                .collect(Collectors.toList());
    }

    public List<Payment> findByCustomerId(String customerId) {
        return store.values().stream()
                .filter(p -> p.getCustomerId().equals(customerId))
                .sorted(Comparator.comparing(Payment::getPaymentDateTime).reversed())
                .collect(Collectors.toList());
    }

    public List<Payment> findAll() {
        return new ArrayList<>(store.values());
    }

    public double totalCollected() {
        return store.values().stream().mapToDouble(Payment::getAmount).sum();
    }

    public int count() { return store.size(); }
}
