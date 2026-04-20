package com.ebilling;

import com.ebilling.model.*;
import com.ebilling.model.Bill.PaymentStatus;
import com.ebilling.model.Customer.ConnectionType;
import com.ebilling.model.Payment.PaymentMode;
import com.ebilling.repository.*;
import com.ebilling.service.BillingService;
import com.ebilling.service.TariffService;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ElectricityBillingTest {

    private BillingService service;

    @BeforeEach
    void setUp() {
        service = new BillingService();
    }

    // ─── TariffService Tests ──────────────────────────────────────

    @Test @Order(1)
    @DisplayName("Domestic: 0–100 units slab charged at ₹3.50/unit")
    void testDomesticSlab1() {
        TariffService ts = new TariffService();
        double charge = ts.calculateEnergyCharge(ConnectionType.DOMESTIC, 80);
        assertEquals(80 * 3.50, charge, 0.01);
    }

    @Test @Order(2)
    @DisplayName("Domestic: progressive 180 units spans two slabs")
    void testDomesticProgressive() {
        TariffService ts = new TariffService();
        // 100 units @ 3.50 + 80 units @ 5.00
        double expected = 100 * 3.50 + 80 * 5.00;
        double charge = ts.calculateEnergyCharge(ConnectionType.DOMESTIC, 180);
        assertEquals(expected, charge, 0.01);
    }

    @Test @Order(3)
    @DisplayName("Commercial energy charge calculated correctly")
    void testCommercialCharge() {
        TariffService ts = new TariffService();
        // 300 units: 200@6.00 + 100@7.50
        double expected = 200 * 6.00 + 100 * 7.50;
        assertEquals(expected, ts.calculateEnergyCharge(ConnectionType.COMMERCIAL, 300), 0.01);
    }

    @Test @Order(4)
    @DisplayName("Industrial charge for large consumption")
    void testIndustrialCharge() {
        TariffService ts = new TariffService();
        // 600 units: 500@5.50 + 100@7.00
        double expected = 500 * 5.50 + 100 * 7.00;
        assertEquals(expected, ts.calculateEnergyCharge(ConnectionType.INDUSTRIAL, 600), 0.01);
    }

    @Test @Order(5)
    @DisplayName("Fuel surcharge is 6% of energy charge")
    void testFuelSurcharge() {
        TariffService ts = new TariffService();
        assertEquals(60.0, ts.getFuelSurcharge(1000.0), 0.01);
    }

    // ─── Customer Tests ───────────────────────────────────────────

    @Test @Order(6)
    @DisplayName("Add customer and retrieve by ID")
    void testAddCustomer() {
        Customer c = service.addCustomer("Test User", "123 Main St",
                "9999999999", "test@test.com", ConnectionType.DOMESTIC);
        assertNotNull(c.getCustomerId());
        assertTrue(c.isActive());
        assertEquals("Test User", c.getName());

        Customer found = service.getCustomer(c.getCustomerId()).orElseThrow();
        assertEquals(c.getCustomerId(), found.getCustomerId());
    }

    @Test @Order(7)
    @DisplayName("Add customer with blank name throws exception")
    void testAddCustomerBlankName() {
        assertThrows(IllegalArgumentException.class,
                () -> service.addCustomer("", "Address", "phone", "email", ConnectionType.DOMESTIC));
    }

    @Test @Order(8)
    @DisplayName("Search customer by name")
    void testSearchCustomer() {
        List<Customer> results = service.searchCustomers("Ravi");
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(c -> c.getName().contains("Ravi")));
    }

    // ─── Meter Reading Tests ──────────────────────────────────────

    @Test @Order(9)
    @DisplayName("Add meter reading and compute units correctly")
    void testMeterReading() {
        MeterReading r = service.addMeterReading("C1001", 1500.0, LocalDate.now());
        assertNotNull(r.getReadingId());
        assertTrue(r.getUnitsConsumed() >= 0);
    }

    @Test @Order(10)
    @DisplayName("Meter reading lower than previous throws exception")
    void testMeterReadingLowerValue() {
        service.addMeterReading("C1002", 2000.0, LocalDate.now());
        assertThrows(IllegalArgumentException.class,
                () -> service.addMeterReading("C1002", 100.0, LocalDate.now()));
    }

    @Test @Order(11)
    @DisplayName("Meter reading for unknown customer throws exception")
    void testMeterReadingUnknownCustomer() {
        assertThrows(IllegalArgumentException.class,
                () -> service.addMeterReading("UNKNOWN", 500.0, LocalDate.now()));
    }

    // ─── Bill Generation Tests ────────────────────────────────────

    @Test @Order(12)
    @DisplayName("Generate bill and verify charges are positive")
    void testGenerateBill() {
        MeterReading r = service.addMeterReading("C1001", 2000.0, LocalDate.now());
        Bill bill = service.generateBill("C1001", r.getReadingId(), 0);

        assertNotNull(bill.getBillId());
        assertTrue(bill.getTotalAmount() > 0);
        assertTrue(bill.getEnergyCharge() > 0);
        assertEquals(PaymentStatus.UNPAID, bill.getPaymentStatus());
        assertNotNull(bill.getDueDate());
    }

    @Test @Order(13)
    @DisplayName("Bill total equals subtotal + 18% tax")
    void testBillTaxCalculation() {
        MeterReading r = service.addMeterReading("C1003", 3000.0, LocalDate.now());
        Bill bill = service.generateBill("C1003", r.getReadingId(), 0);

        double expectedTax = bill.getSubTotal() * TariffService.TAX_RATE;
        assertEquals(expectedTax, bill.getTaxAmount(), 0.01);
        assertEquals(bill.getSubTotal() + bill.getTaxAmount(), bill.getTotalAmount(), 0.01);
    }

    @Test @Order(14)
    @DisplayName("Bill due date is 15 days after bill date")
    void testBillDueDate() {
        MeterReading r = service.addMeterReading("C1004", 500.0, LocalDate.now());
        Bill bill = service.generateBill("C1004", r.getReadingId(), 0);
        assertEquals(bill.getBillDate().plusDays(15), bill.getDueDate());
    }

    // ─── Payment Tests ────────────────────────────────────────────

    @Test @Order(15)
    @DisplayName("Full payment marks bill as PAID")
    void testFullPayment() {
        MeterReading r = service.addMeterReading("C1001", 3000.0, LocalDate.now());
        Bill bill = service.generateBill("C1001", r.getReadingId(), 0);
        service.recordPayment(bill.getBillId(), bill.getTotalAmount(), PaymentMode.UPI, "TXN001");

        Bill updated = service.getBill(bill.getBillId()).orElseThrow();
        assertEquals(PaymentStatus.PAID, updated.getPaymentStatus());
        assertEquals(0.0, updated.getBalanceDue(), 0.01);
    }

    @Test @Order(16)
    @DisplayName("Partial payment marks bill as PARTIALLY_PAID")
    void testPartialPayment() {
        MeterReading r = service.addMeterReading("C1002", 4000.0, LocalDate.now());
        Bill bill = service.generateBill("C1002", r.getReadingId(), 0);
        double partial = bill.getTotalAmount() / 2;
        service.recordPayment(bill.getBillId(), partial, PaymentMode.CASH, "CASH001");

        Bill updated = service.getBill(bill.getBillId()).orElseThrow();
        assertEquals(PaymentStatus.PARTIALLY_PAID, updated.getPaymentStatus());
        assertTrue(updated.getBalanceDue() > 0);
    }

    @Test @Order(17)
    @DisplayName("Payment exceeding balance due throws exception")
    void testOvertPaymentThrows() {
        MeterReading r = service.addMeterReading("C1004", 1000.0, LocalDate.now());
        Bill bill = service.generateBill("C1004", r.getReadingId(), 0);
        assertThrows(IllegalArgumentException.class,
                () -> service.recordPayment(bill.getBillId(), bill.getTotalAmount() + 1000, PaymentMode.CARD, "TXN"));
    }

    @Test @Order(18)
    @DisplayName("Zero payment throws exception")
    void testZeroPaymentThrows() {
        MeterReading r = service.addMeterReading("C1001", 5000.0, LocalDate.now());
        Bill bill = service.generateBill("C1001", r.getReadingId(), 0);
        assertThrows(IllegalArgumentException.class,
                () -> service.recordPayment(bill.getBillId(), 0, PaymentMode.CASH, ""));
    }

    // ─── Stats Tests ──────────────────────────────────────────────

    @Test @Order(19)
    @DisplayName("System stats increment correctly")
    void testSystemStats() {
        int initialCustomers = service.totalCustomers();
        service.addCustomer("Stats User", "Address", "000", "stats@test.com", ConnectionType.COMMERCIAL);
        assertEquals(initialCustomers + 1, service.totalCustomers());
    }

    @Test @Order(20)
    @DisplayName("Unpaid bills list is not null")
    void testUnpaidBills() {
        assertNotNull(service.getUnpaidBills());
    }
}
