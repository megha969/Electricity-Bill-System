package com.ebilling.report;

import com.ebilling.model.*;
import com.ebilling.model.Bill.PaymentStatus;
import com.ebilling.service.BillingService;
import com.ebilling.util.ConsoleColors;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Generates summary reports for the billing system.
 */
public class ReportGenerator {

    private final BillingService billingService;

    public ReportGenerator(BillingService billingService) {
        this.billingService = billingService;
    }

    // ─── Dashboard Summary ───────────────────────────────────────

    public void printDashboard() {
        System.out.println();
        System.out.println(ConsoleColors.CYAN_BOLD +
                "╔══════════════════════════════════════════════════════╗");
        System.out.println("║           📊  SYSTEM DASHBOARD                      ║");
        System.out.println("╚══════════════════════════════════════════════════════╝"
                + ConsoleColors.RESET);

        System.out.printf("  %-30s  %s%d%s%n",
                "Total Customers:", ConsoleColors.YELLOW_BOLD, billingService.totalCustomers(), ConsoleColors.RESET);
        System.out.printf("  %-30s  %s%d%s%n",
                "Total Bills Generated:", ConsoleColors.YELLOW_BOLD, billingService.totalBills(), ConsoleColors.RESET);
        System.out.printf("  %-30s  %s%d%s%n",
                "Total Payments Recorded:", ConsoleColors.YELLOW_BOLD, billingService.totalPayments(), ConsoleColors.RESET);
        System.out.printf("  %-30s  %s₹%,.2f%s%n",
                "Total Amount Collected:", ConsoleColors.GREEN_BOLD, billingService.totalCollected(), ConsoleColors.RESET);
        System.out.printf("  %-30s  %s₹%,.2f%s%n",
                "Total Outstanding:", ConsoleColors.RED_BOLD, billingService.totalOutstanding(), ConsoleColors.RESET);

        long unpaid = billingService.getUnpaidBills().stream()
                .filter(b -> b.getPaymentStatus() == PaymentStatus.OVERDUE).count();
        System.out.printf("  %-30s  %s%d%s%n",
                "Overdue Bills:", ConsoleColors.RED_BOLD, unpaid, ConsoleColors.RESET);
        System.out.println();
    }

    // ─── Customer Ledger ─────────────────────────────────────────

    public void printCustomerLedger(String customerId) {
        Customer customer = billingService.getCustomer(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found."));

        System.out.println();
        System.out.println(ConsoleColors.CYAN_BOLD + "─── Customer Ledger: " + customer.getName() +
                " [" + customerId + "] ───" + ConsoleColors.RESET);

        List<Bill> bills = billingService.getBillsForCustomer(customerId);
        if (bills.isEmpty()) {
            System.out.println("  No bills found.");
            return;
        }
        System.out.printf("  %-12s %-10s %-8s %12s %10s %-12s%n",
                "Bill ID", "Month", "Units", "Amount(₹)", "Paid(₹)", "Status");
        System.out.println("  " + "─".repeat(68));
        for (Bill b : bills) {
            String statusColor = switch (b.getPaymentStatus()) {
                case PAID -> ConsoleColors.GREEN;
                case OVERDUE -> ConsoleColors.RED;
                default -> ConsoleColors.YELLOW;
            };
            System.out.printf("  %-12s %-10s %8.1f %12.2f %10.2f %s%-12s%s%n",
                    b.getBillId(), b.getBillingMonth(), b.getUnitsConsumed(),
                    b.getTotalAmount(), b.getAmountPaid(),
                    statusColor, b.getPaymentStatus(), ConsoleColors.RESET);
        }

        double totalBilled = bills.stream().mapToDouble(Bill::getTotalAmount).sum();
        double totalPaid   = bills.stream().mapToDouble(Bill::getAmountPaid).sum();
        System.out.println("  " + "─".repeat(68));
        System.out.printf("  %-30s  ₹%,.2f%n", "Total Billed:", totalBilled);
        System.out.printf("  %-30s  ₹%,.2f%n", "Total Paid:", totalPaid);
        System.out.printf("  %-30s  %s₹%,.2f%s%n", "Balance Due:",
                ConsoleColors.RED_BOLD, totalBilled - totalPaid, ConsoleColors.RESET);
        System.out.println();
    }

    // ─── Unpaid Bills Report ─────────────────────────────────────

    public void printUnpaidBillsReport() {
        System.out.println();
        System.out.println(ConsoleColors.RED_BOLD + "─── Unpaid / Overdue Bills ─────────────────────" + ConsoleColors.RESET);
        List<Bill> unpaid = billingService.getUnpaidBills();
        if (unpaid.isEmpty()) {
            System.out.println(ConsoleColors.GREEN + "  ✅ No outstanding bills!" + ConsoleColors.RESET);
            return;
        }
        System.out.printf("  %-12s %-16s %-10s %12s %-10s%n",
                "Bill ID", "Customer", "Month", "Balance(₹)", "Status");
        System.out.println("  " + "─".repeat(65));
        for (Bill b : unpaid) {
            String c = b.getPaymentStatus() == PaymentStatus.OVERDUE
                    ? ConsoleColors.RED : ConsoleColors.YELLOW;
            System.out.printf("  %-12s %-16s %-10s %12.2f %s%-10s%s%n",
                    b.getBillId(), b.getCustomerName(), b.getBillingMonth(),
                    b.getBalanceDue(), c, b.getPaymentStatus(), ConsoleColors.RESET);
        }
        System.out.println();
    }

    // ─── Tariff Chart ────────────────────────────────────────────

    public void printTariffChart() {
        System.out.println();
        System.out.println(ConsoleColors.CYAN_BOLD + "─── ⚡ Tariff Rate Chart ────────────────────────" + ConsoleColors.RESET);
        billingService.getTariffService().getAllTariffs().forEach((type, slabs) -> {
            System.out.println(ConsoleColors.YELLOW_BOLD + "\n  " + type + ConsoleColors.RESET);
            System.out.printf("  %-20s %12s %12s%n", "Slab", "Rate/Unit(₹)", "Fixed(₹)");
            System.out.println("  " + "─".repeat(46));
            slabs.forEach(s -> System.out.printf("  %-20s %12.2f %12.2f%n",
                    s.getSlabLabel(), s.getRatePerUnit(), s.getFixedCharge()));
        });
        System.out.println();
    }
}
