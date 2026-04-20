package com.ebilling.util;

import com.ebilling.model.Bill;

/**
 * Formats a Bill as a printable console receipt.
 */
public class BillPrinter {

    private static final String LINE  = "─".repeat(52);
    private static final String DLINE = "═".repeat(52);

    public static void print(Bill b) {
        System.out.println();
        System.out.println(ConsoleColors.CYAN_BOLD + "╔" + DLINE + "╗");
        System.out.println("║       ⚡  ELECTRICITY BILL RECEIPT  ⚡       ║");
        System.out.println("╚" + DLINE + "╝" + ConsoleColors.RESET);
        row("Bill No",      b.getBillId());
        row("Customer ID",  b.getCustomerId());
        row("Name",         b.getCustomerName());
        row("Connection",   b.getConnectionType());
        row("Billing Month",b.getBillingMonth());
        row("Bill Date",    b.getFormattedBillDate());
        row("Due Date",     b.getFormattedDueDate());
        System.out.println(ConsoleColors.WHITE_BOLD + LINE + ConsoleColors.RESET);
        row("Units Consumed", String.format("%.2f kWh", b.getUnitsConsumed()));
        System.out.println(ConsoleColors.WHITE_BOLD + LINE + ConsoleColors.RESET);
        rowAmt("Energy Charge",    b.getEnergyCharge());
        rowAmt("Fixed Charge",     b.getFixedCharge());
        rowAmt("Fuel Surcharge",   b.getFuelSurcharge());
        rowAmt("Electricity Duty", b.getElectricityDuty());
        rowAmt("Meter Rent",       b.getMeterRent());
        if (b.getArrears() > 0) rowAmt("Arrears", b.getArrears());
        System.out.println(ConsoleColors.WHITE_BOLD + LINE + ConsoleColors.RESET);
        rowAmt("Sub Total",   b.getSubTotal());
        rowAmt("Tax (18%)",   b.getTaxAmount());
        System.out.println(ConsoleColors.YELLOW_BOLD + LINE + ConsoleColors.RESET);
        System.out.printf(ConsoleColors.YELLOW_BOLD + "  %-28s  ₹%,14.2f%n" + ConsoleColors.RESET,
                "TOTAL AMOUNT DUE", b.getTotalAmount());
        System.out.println(ConsoleColors.YELLOW_BOLD + LINE + ConsoleColors.RESET);
        if (b.getAmountPaid() > 0) rowAmt("Amount Paid", b.getAmountPaid());
        String statusColor = switch (b.getPaymentStatus()) {
            case PAID -> ConsoleColors.GREEN_BOLD;
            case OVERDUE -> ConsoleColors.RED_BOLD;
            default -> ConsoleColors.YELLOW_BOLD;
        };
        System.out.printf("  %-28s  %s%s%s%n", "Status",
                statusColor, b.getPaymentStatus(), ConsoleColors.RESET);
        System.out.println();
    }

    private static void row(String label, String value) {
        System.out.printf("  %-28s  %s%n", label + ":", value);
    }

    private static void rowAmt(String label, double amount) {
        System.out.printf("  %-28s  ₹%,14.2f%n", label + ":", amount);
    }
}
