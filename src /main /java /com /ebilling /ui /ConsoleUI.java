package com.ebilling.ui;

import com.ebilling.model.*;
import com.ebilling.model.Customer.ConnectionType;
import com.ebilling.model.Payment.PaymentMode;
import com.ebilling.report.ReportGenerator;
import com.ebilling.service.BillingService;
import com.ebilling.util.BillPrinter;
import com.ebilling.util.ConsoleColors;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * Console-based UI for the Electricity Billing System.
 */
public class ConsoleUI {

    private final BillingService    service;
    private final ReportGenerator   report;
    private final Scanner           scanner;

    public ConsoleUI(BillingService service) {
        this.service = service;
        this.report  = new ReportGenerator(service);
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        printBanner();
        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt("Enter choice: ", 1, 8);
            switch (choice) {
                case 1 -> customerMenu();
                case 2 -> meterReadingMenu();
                case 3 -> billingMenu();
                case 4 -> paymentMenu();
                case 5 -> reportsMenu();
                case 6 -> report.printTariffChart();
                case 7 -> report.printDashboard();
                case 8 -> { printGoodbye(); running = false; }
            }
        }
        scanner.close();
    }

    // ─── BANNER ──────────────────────────────────────────────────

    private void printBanner() {
        System.out.println(ConsoleColors.YELLOW_BOLD);
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║   ⚡  ELECTRICITY BILLING MANAGEMENT SYSTEM  ⚡     ║");
        System.out.println("║               Version 1.0  |  Java Edition           ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        System.out.println(ConsoleColors.RESET);
    }

    private void printMainMenu() {
        System.out.println(ConsoleColors.CYAN_BOLD + "═══ MAIN MENU ═══════════════════════════════════" + ConsoleColors.RESET);
        System.out.println("  1. 👤  Customer Management");
        System.out.println("  2. 🔢  Meter Reading Entry");
        System.out.println("  3. 📄  Bill Generation & View");
        System.out.println("  4. 💳  Payment Processing");
        System.out.println("  5. 📊  Reports");
        System.out.println("  6. 📋  View Tariff Chart");
        System.out.println("  7. 🖥️   Dashboard");
        System.out.println("  8. 🚪  Exit");
        System.out.println("─────────────────────────────────────────────────────");
    }

    // ─── CUSTOMER MENU ───────────────────────────────────────────

    private void customerMenu() {
        System.out.println(ConsoleColors.CYAN_BOLD + "\n─── Customer Management ─────────────────────────" + ConsoleColors.RESET);
        System.out.println("  1. Add New Customer");
        System.out.println("  2. View All Customers");
        System.out.println("  3. Search Customer");
        System.out.println("  4. View Customer Details");
        System.out.println("  5. Back");
        int c = readInt("Choice: ", 1, 5);
        switch (c) {
            case 1 -> addCustomer();
            case 2 -> listCustomers();
            case 3 -> searchCustomer();
            case 4 -> viewCustomer();
            case 5 -> {}
        }
    }

    private void addCustomer() {
        System.out.println(ConsoleColors.WHITE_BOLD + "\n  ── Add New Customer ──" + ConsoleColors.RESET);
        String name    = readString("  Name         : ");
        String address = readString("  Address      : ");
        String phone   = readString("  Phone        : ");
        String email   = readString("  Email        : ");

        System.out.println("  Connection Type: 1.Domestic  2.Commercial  3.Industrial");
        int t = readInt("  Select (1-3): ", 1, 3);
        ConnectionType type = ConnectionType.values()[t - 1];

        try {
            Customer cust = service.addCustomer(name, address, phone, email, type);
            System.out.println(ConsoleColors.GREEN + "\n  ✅ Customer added! ID: " + cust.getCustomerId() + ConsoleColors.RESET);
        } catch (Exception e) {
            System.out.println(ConsoleColors.RED + "  Error: " + e.getMessage() + ConsoleColors.RESET);
        }
        pause();
    }

    private void listCustomers() {
        List<Customer> list = service.getAllCustomers();
        System.out.println(ConsoleColors.WHITE_BOLD + "\n  ── All Customers ─────────────────────────────" + ConsoleColors.RESET);
        System.out.printf("  %-8s %-20s %-12s %-12s%n", "ID", "Name", "Type", "Status");
        System.out.println("  " + "─".repeat(56));
        for (Customer c : list) {
            String status = c.isActive()
                    ? ConsoleColors.GREEN + "Active" + ConsoleColors.RESET
                    : ConsoleColors.RED + "Inactive" + ConsoleColors.RESET;
            System.out.printf("  %-8s %-20s %-12s %s%n",
                    c.getCustomerId(), c.getName(), c.getConnectionType().getLabel(), status);
        }
        System.out.println();
        pause();
    }

    private void searchCustomer() {
        String q = readString("  Search (name/ID/address): ");
        List<Customer> results = service.searchCustomers(q);
        if (results.isEmpty()) {
            System.out.println(ConsoleColors.YELLOW + "  No customers found." + ConsoleColors.RESET);
        } else {
            results.forEach(c -> System.out.println("  " + c));
        }
        pause();
    }

    private void viewCustomer() {
        String id = readString("  Customer ID: ");
        service.getCustomer(id).ifPresentOrElse(c -> {
            System.out.println(ConsoleColors.WHITE_BOLD + "\n  ── Customer Details ──" + ConsoleColors.RESET);
            System.out.println("  ID          : " + c.getCustomerId());
            System.out.println("  Name        : " + c.getName());
            System.out.println("  Address     : " + c.getAddress());
            System.out.println("  Phone       : " + c.getPhone());
            System.out.println("  Email       : " + c.getEmail());
            System.out.println("  Connection  : " + c.getConnectionType().getLabel());
            System.out.println("  Since       : " + c.getConnectionDate());
            System.out.println("  Status      : " + (c.isActive() ? "Active" : "Inactive"));
        }, () -> System.out.println(ConsoleColors.RED + "  Customer not found." + ConsoleColors.RESET));
        pause();
    }

    // ─── METER READING MENU ──────────────────────────────────────

    private void meterReadingMenu() {
        System.out.println(ConsoleColors.CYAN_BOLD + "\n─── Meter Reading Entry ─────────────────────────" + ConsoleColors.RESET);
        System.out.println("  1. Enter New Reading");
        System.out.println("  2. View Readings for Customer");
        System.out.println("  3. Back");
        int c = readInt("Choice: ", 1, 3);
        switch (c) {
            case 1 -> enterReading();
            case 2 -> viewReadings();
            case 3 -> {}
        }
    }

    private void enterReading() {
        String custId = readString("  Customer ID: ");
        service.getCustomer(custId).ifPresentOrElse(cust -> {
            System.out.println("  Customer: " + cust.getName());
            double current = readDouble("  Current Meter Reading (kWh): ");
            try {
                MeterReading reading = service.addMeterReading(custId, current, LocalDate.now());
                System.out.println(ConsoleColors.GREEN + "\n  ✅ Reading saved! ID: " + reading.getReadingId());
                System.out.printf("  Units consumed: %.2f kWh%s%n", reading.getUnitsConsumed(), ConsoleColors.RESET);
            } catch (Exception e) {
                System.out.println(ConsoleColors.RED + "  Error: " + e.getMessage() + ConsoleColors.RESET);
            }
        }, () -> System.out.println(ConsoleColors.RED + "  Customer not found." + ConsoleColors.RESET));
        pause();
    }

    private void viewReadings() {
        String custId = readString("  Customer ID: ");
        List<MeterReading> readings = service.getReadingsForCustomer(custId);
        if (readings.isEmpty()) {
            System.out.println("  No readings found.");
        } else {
            System.out.printf("  %-10s %-14s %-12s %-12s %-10s%n",
                    "ID", "Date", "Previous", "Current", "Units");
            System.out.println("  " + "─".repeat(62));
            readings.forEach(r -> System.out.printf("  %-10s %-14s %12.1f %12.1f %10.1f%n",
                    r.getReadingId(), r.getReadingDate(), r.getPreviousReading(),
                    r.getCurrentReading(), r.getUnitsConsumed()));
        }
        pause();
    }

    // ─── BILLING MENU ────────────────────────────────────────────

    private void billingMenu() {
        System.out.println(ConsoleColors.CYAN_BOLD + "\n─── Bill Generation & View ──────────────────────" + ConsoleColors.RESET);
        System.out.println("  1. Generate New Bill");
        System.out.println("  2. View Bill");
        System.out.println("  3. View All Bills for Customer");
        System.out.println("  4. Back");
        int c = readInt("Choice: ", 1, 4);
        switch (c) {
            case 1 -> generateBill();
            case 2 -> viewBill();
            case 3 -> viewCustomerBills();
            case 4 -> {}
        }
    }

    private void generateBill() {
        String custId    = readString("  Customer ID   : ");
        String readingId = readString("  Reading ID    : ");
        double arrears   = readDoubleOptional("  Arrears (₹)  [0]: ", 0.0);
        try {
            Bill bill = service.generateBill(custId, readingId, arrears);
            System.out.println(ConsoleColors.GREEN + "\n  ✅ Bill generated!" + ConsoleColors.RESET);
            BillPrinter.print(bill);
        } catch (Exception e) {
            System.out.println(ConsoleColors.RED + "  Error: " + e.getMessage() + ConsoleColors.RESET);
        }
        pause();
    }

    private void viewBill() {
        String billId = readString("  Bill ID: ");
        service.getBill(billId).ifPresentOrElse(
                BillPrinter::print,
                () -> System.out.println(ConsoleColors.RED + "  Bill not found." + ConsoleColors.RESET));
        pause();
    }

    private void viewCustomerBills() {
        String custId = readString("  Customer ID: ");
        report.printCustomerLedger(custId);
        pause();
    }

    // ─── PAYMENT MENU ────────────────────────────────────────────

    private void paymentMenu() {
        System.out.println(ConsoleColors.CYAN_BOLD + "\n─── Payment Processing ──────────────────────────" + ConsoleColors.RESET);
        System.out.println("  1. Record Payment");
        System.out.println("  2. View Payments for Customer");
        System.out.println("  3. View Unpaid Bills");
        System.out.println("  4. Back");
        int c = readInt("Choice: ", 1, 4);
        switch (c) {
            case 1 -> recordPayment();
            case 2 -> viewPayments();
            case 3 -> report.printUnpaidBillsReport();
            case 4 -> {}
        }
        if (c == 3) pause();
    }

    private void recordPayment() {
        String billId = readString("  Bill ID    : ");
        service.getBill(billId).ifPresentOrElse(bill -> {
            System.out.printf("  Balance Due: ₹%.2f%n", bill.getBalanceDue());
            double amount = readDouble("  Amount (₹): ");
            System.out.println("  Mode: 1.Cash  2.Card  3.UPI  4.NetBanking  5.Cheque");
            int m = readInt("  Select (1-5): ", 1, 5);
            PaymentMode mode = PaymentMode.values()[m - 1];
            String ref = readString("  Transaction Ref: ");
            try {
                Payment pay = service.recordPayment(billId, amount, mode, ref);
                System.out.println(ConsoleColors.GREEN + "\n  ✅ Payment recorded! ID: " + pay.getPaymentId() + ConsoleColors.RESET);
            } catch (Exception e) {
                System.out.println(ConsoleColors.RED + "  Error: " + e.getMessage() + ConsoleColors.RESET);
            }
        }, () -> System.out.println(ConsoleColors.RED + "  Bill not found." + ConsoleColors.RESET));
        pause();
    }

    private void viewPayments() {
        String custId = readString("  Customer ID: ");
        List<Payment> payments = service.getPaymentsForCustomer(custId);
        if (payments.isEmpty()) {
            System.out.println("  No payments found.");
        } else {
            System.out.printf("  %-10s %-12s %12s %-12s %-20s%n",
                    "Pay ID", "Bill ID", "Amount(₹)", "Mode", "Date");
            System.out.println("  " + "─".repeat(70));
            payments.forEach(p -> System.out.printf("  %-10s %-12s %12.2f %-12s %-20s%n",
                    p.getPaymentId(), p.getBillId(), p.getAmount(),
                    p.getPaymentMode(), p.getFormattedDateTime()));
        }
        pause();
    }

    // ─── REPORTS MENU ────────────────────────────────────────────

    private void reportsMenu() {
        System.out.println(ConsoleColors.CYAN_BOLD + "\n─── Reports ─────────────────────────────────────" + ConsoleColors.RESET);
        System.out.println("  1. Dashboard Summary");
        System.out.println("  2. Customer Ledger");
        System.out.println("  3. Unpaid / Overdue Bills");
        System.out.println("  4. Tariff Chart");
        System.out.println("  5. Back");
        int c = readInt("Choice: ", 1, 5);
        switch (c) {
            case 1 -> report.printDashboard();
            case 2 -> { String id = readString("  Customer ID: "); report.printCustomerLedger(id); }
            case 3 -> report.printUnpaidBillsReport();
            case 4 -> report.printTariffChart();
            case 5 -> {}
        }
        if (c != 5) pause();
    }

    // ─── UTILITY ─────────────────────────────────────────────────

    private String readString(String prompt) {
        System.out.print(prompt);
        String s = scanner.nextLine().trim();
        while (s.isEmpty()) { System.out.print("  Required. " + prompt); s = scanner.nextLine().trim(); }
        return s;
    }

    private int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int v = Integer.parseInt(scanner.nextLine().trim());
                if (v >= min && v <= max) return v;
                System.out.println(ConsoleColors.RED + "  Enter " + min + "–" + max + "." + ConsoleColors.RESET);
            } catch (NumberFormatException e) {
                System.out.println(ConsoleColors.RED + "  Invalid number." + ConsoleColors.RESET);
            }
        }
    }

    private double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double v = Double.parseDouble(scanner.nextLine().trim());
                if (v >= 0) return v;
                System.out.println(ConsoleColors.RED + "  Must be non-negative." + ConsoleColors.RESET);
            } catch (NumberFormatException e) {
                System.out.println(ConsoleColors.RED + "  Invalid number." + ConsoleColors.RESET);
            }
        }
    }

    private double readDoubleOptional(String prompt, double defaultVal) {
        System.out.print(prompt);
        String line = scanner.nextLine().trim();
        if (line.isEmpty()) return defaultVal;
        try { return Double.parseDouble(line); } catch (NumberFormatException e) { return defaultVal; }
    }

    private void pause() {
        System.out.print("\n  Press ENTER to continue...");
        scanner.nextLine();
        System.out.println();
    }

    private void printGoodbye() {
        System.out.println(ConsoleColors.GREEN + "\n  Thank you for using Electricity Billing System! 👋\n" + ConsoleColors.RESET);
    }
}
