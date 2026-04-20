package com.ebilling.repository;

import com.ebilling.model.Bill;
import com.ebilling.model.Bill.PaymentStatus;

import java.util.*;
import java.util.stream.Collectors;

/**
 * In-memory bill store.
 */
public class BillRepository {

    private final Map<String, Bill> store = new LinkedHashMap<>();
    private int counter = 1;

    public Bill save(Bill bill) {
        if (bill.getBillId() == null || bill.getBillId().isBlank()) {
            bill.setBillId(String.format("BILL%05d", counter++));
        }
        store.put(bill.getBillId(), bill);
        return bill;
    }

    public Optional<Bill> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<Bill> findAll() {
        return new ArrayList<>(store.values());
    }

    public List<Bill> findByCustomerId(String customerId) {
        return store.values().stream()
                .filter(b -> b.getCustomerId().equals(customerId))
                .sorted(Comparator.comparing(Bill::getBillDate).reversed())
                .collect(Collectors.toList());
    }

    public List<Bill> findUnpaid() {
        return store.values().stream()
                .filter(b -> b.getPaymentStatus() == PaymentStatus.UNPAID
                        || b.getPaymentStatus() == PaymentStatus.OVERDUE
                        || b.getPaymentStatus() == PaymentStatus.PARTIALLY_PAID)
                .collect(Collectors.toList());
    }

    public List<Bill> findByMonth(String billingMonth) {
        return store.values().stream()
                .filter(b -> billingMonth.equals(b.getBillingMonth()))
                .collect(Collectors.toList());
    }

    public Optional<Bill> findLatestForCustomer(String customerId) {
        return store.values().stream()
                .filter(b -> b.getCustomerId().equals(customerId))
                .max(Comparator.comparing(Bill::getBillDate));
    }

    public double totalCollectedAmount() {
        return store.values().stream()
                .mapToDouble(Bill::getAmountPaid)
                .sum();
    }

    public double totalOutstandingAmount() {
        return store.values().stream()
                .mapToDouble(Bill::getBalanceDue)
                .sum();
    }

    public int count() { return store.size(); }
}
