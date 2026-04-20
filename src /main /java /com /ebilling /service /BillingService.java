package com.ebilling.service;

import com.ebilling.model.*;
import com.ebilling.model.Bill.PaymentStatus;
import com.ebilling.model.Customer.ConnectionType;
import com.ebilling.model.Payment.PaymentMode;
import com.ebilling.repository.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Core billing business logic service.
 */
public class BillingService {

    private final CustomerRepository     customerRepo;
    private final MeterReadingRepository meterRepo;
    private final BillRepository         billRepo;
    private final PaymentRepository      paymentRepo;
    private final TariffService          tariffService;

    public BillingService() {
        this.customerRepo  = new CustomerRepository();
        this.meterRepo     = new MeterReadingRepository();
        this.billRepo      = new BillRepository();
        this.paymentRepo   = new PaymentRepository();
        this.tariffService = new TariffService();
    }

    // Testable constructor
    public BillingService(CustomerRepository customerRepo,
                          MeterReadingRepository meterRepo,
                          BillRepository billRepo,
                          PaymentRepository paymentRepo,
                          TariffService tariffService) {
        this.customerRepo  = customerRepo;
        this.meterRepo     = meterRepo;
        this.billRepo      = billRepo;
        this.paymentRepo   = paymentRepo;
        this.tariffService = tariffService;
    }

    // ─── CUSTOMER ──────────────────────────────────────────────────

    public Customer addCustomer(String name, String address, String phone,
                                String email, ConnectionType type) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name is required.");
        if (address == null || address.isBlank()) throw new IllegalArgumentException("Address is required.");
        Customer c = new Customer(null, name.trim(), address.trim(), phone, email, type);
        return customerRepo.save(c);
    }

    public Optional<Customer> getCustomer(String id) {
        return customerRepo.findById(id);
    }

    public List<Customer> getAllCustomers() {
        return customerRepo.findAll();
    }

    public List<Customer> searchCustomers(String query) {
        return customerRepo.search(query);
    }

    public Customer updateCustomer(Customer customer) {
        return customerRepo.save(customer);
    }

    // ─── METER READING ─────────────────────────────────────────────

    public MeterReading addMeterReading(String customerId, double currentReading, LocalDate date) {
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));

        // Get previous reading
        double prevReading = meterRepo.findLatestForCustomer(customerId)
                .map(MeterReading::getCurrentReading)
                .orElse(0.0);

        if (currentReading < prevReading) {
            throw new IllegalArgumentException(
                    "Current reading (" + currentReading + ") cannot be less than previous (" + prevReading + ").");
        }

        MeterReading reading = new MeterReading(null, customerId, prevReading, currentReading, date);
        return meterRepo.save(reading);
    }

    public List<MeterReading> getReadingsForCustomer(String customerId) {
        return meterRepo.findByCustomerId(customerId);
    }

    // ─── BILL GENERATION ───────────────────────────────────────────

    public Bill generateBill(String customerId, String readingId, double arrears) {
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));

        MeterReading reading = meterRepo.findById(readingId)
                .orElseThrow(() -> new IllegalArgumentException("Reading not found: " + readingId));

        if (!reading.getCustomerId().equals(customerId)) {
            throw new IllegalArgumentException("Reading does not belong to customer.");
        }

        ConnectionType type  = customer.getConnectionType();
        double units         = reading.getUnitsConsumed();

        double energyCharge  = tariffService.calculateEnergyCharge(type, units);
        double fixedCharge   = tariffService.getFixedCharge(type, units);
        double fuelSurcharge = tariffService.getFuelSurcharge(energyCharge);
        double elecDuty      = tariffService.getElectricityDuty(energyCharge);
        double meterRent     = tariffService.getMeterRent();

        Bill bill = new Bill();
        bill.setCustomerId(customerId);
        bill.setCustomerName(customer.getName());
        bill.setReadingId(readingId);
        bill.setBillingMonth(reading.getBillingMonth());
        bill.setUnitsConsumed(units);
        bill.setConnectionType(type.getLabel());
        bill.setEnergyCharge(energyCharge);
        bill.setFixedCharge(fixedCharge);
        bill.setFuelSurcharge(fuelSurcharge);
        bill.setElectricityDuty(elecDuty);
        bill.setMeterRent(meterRent);
        bill.setArrears(arrears);
        bill.setBillDate(LocalDate.now());
        bill.setDueDate(LocalDate.now().plusDays(15));
        bill.computeTotals(tariffService.getTaxRate());

        return billRepo.save(bill);
    }

    public List<Bill> getBillsForCustomer(String customerId) {
        return billRepo.findByCustomerId(customerId);
    }

    public List<Bill> getUnpaidBills() {
        // Mark overdue
        billRepo.findUnpaid().forEach(b -> {
            if (b.isOverdue()) b.setPaymentStatus(PaymentStatus.OVERDUE);
        });
        return billRepo.findUnpaid();
    }

    public Optional<Bill> getBill(String billId) {
        return billRepo.findById(billId);
    }

    // ─── PAYMENT ───────────────────────────────────────────────────

    public Payment recordPayment(String billId, double amount, PaymentMode mode, String ref) {
        Bill bill = billRepo.findById(billId)
                .orElseThrow(() -> new IllegalArgumentException("Bill not found: " + billId));

        if (amount <= 0) throw new IllegalArgumentException("Payment amount must be positive.");
        if (amount > bill.getBalanceDue()) {
            throw new IllegalArgumentException(
                    String.format("Amount ₹%.2f exceeds balance due ₹%.2f.", amount, bill.getBalanceDue()));
        }

        Payment payment = new Payment(null, billId, bill.getCustomerId(), amount, mode, ref);
        paymentRepo.save(payment);

        // Update bill
        bill.setAmountPaid(bill.getAmountPaid() + amount);
        bill.setPaymentDate(LocalDate.now());
        if (bill.getBalanceDue() <= 0.01) {
            bill.setPaymentStatus(PaymentStatus.PAID);
        } else {
            bill.setPaymentStatus(PaymentStatus.PARTIALLY_PAID);
        }
        billRepo.save(bill);

        return payment;
    }

    public List<Payment> getPaymentsForCustomer(String customerId) {
        return paymentRepo.findByCustomerId(customerId);
    }

    // ─── SUMMARY STATS ─────────────────────────────────────────────

    public int totalCustomers()    { return customerRepo.count(); }
    public int totalBills()        { return billRepo.count(); }
    public int totalPayments()     { return paymentRepo.count(); }
    public double totalCollected() { return billRepo.totalCollectedAmount(); }
    public double totalOutstanding(){ return billRepo.totalOutstandingAmount(); }

    // Expose repos and services for advanced use
    public TariffService getTariffService()  { return tariffService; }
    public BillRepository getBillRepo()      { return billRepo; }
    public CustomerRepository getCustomerRepo() { return customerRepo; }
    public MeterReadingRepository getMeterRepo() { return meterRepo; }
}
