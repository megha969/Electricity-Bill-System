package com.ebilling.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a generated electricity bill.
 */
public class Bill {

    public enum PaymentStatus {
        UNPAID, PAID, OVERDUE, PARTIALLY_PAID
    }

    private String billId;
    private String customerId;
    private String customerName;
    private String readingId;
    private String billingMonth;

    // Consumption
    private double unitsConsumed;       // kWh

    // Charges breakdown
    private double energyCharge;        // Based on slab rates
    private double fixedCharge;         // Monthly fixed charge
    private double fuelSurcharge;       // Fuel adjustment charge
    private double electricityDuty;     // State electricity duty
    private double meterRent;           // Meter rental
    private double arrears;             // Outstanding from previous bill

    // Totals
    private double subTotal;
    private double taxAmount;           // GST/Tax
    private double totalAmount;
    private double amountPaid;

    // Dates
    private LocalDate billDate;
    private LocalDate dueDate;
    private LocalDate paymentDate;

    private PaymentStatus paymentStatus;
    private String connectionType;

    public Bill() {}

    // Compute derived totals
    public void computeTotals(double taxRate) {
        this.subTotal = energyCharge + fixedCharge + fuelSurcharge
                + electricityDuty + meterRent + arrears;
        this.taxAmount = subTotal * taxRate;
        this.totalAmount = subTotal + taxAmount;
        this.paymentStatus = PaymentStatus.UNPAID;
    }

    public double getBalanceDue() {
        return totalAmount - amountPaid;
    }

    public boolean isOverdue() {
        return paymentStatus == PaymentStatus.UNPAID
                && dueDate != null && LocalDate.now().isAfter(dueDate);
    }

    public String getFormattedBillDate() {
        return billDate != null ? billDate.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")) : "N/A";
    }

    public String getFormattedDueDate() {
        return dueDate != null ? dueDate.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")) : "N/A";
    }

    // Getters & Setters
    public String getBillId() { return billId; }
    public void setBillId(String billId) { this.billId = billId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getReadingId() { return readingId; }
    public void setReadingId(String readingId) { this.readingId = readingId; }

    public String getBillingMonth() { return billingMonth; }
    public void setBillingMonth(String billingMonth) { this.billingMonth = billingMonth; }

    public double getUnitsConsumed() { return unitsConsumed; }
    public void setUnitsConsumed(double unitsConsumed) { this.unitsConsumed = unitsConsumed; }

    public double getEnergyCharge() { return energyCharge; }
    public void setEnergyCharge(double energyCharge) { this.energyCharge = energyCharge; }

    public double getFixedCharge() { return fixedCharge; }
    public void setFixedCharge(double fixedCharge) { this.fixedCharge = fixedCharge; }

    public double getFuelSurcharge() { return fuelSurcharge; }
    public void setFuelSurcharge(double fuelSurcharge) { this.fuelSurcharge = fuelSurcharge; }

    public double getElectricityDuty() { return electricityDuty; }
    public void setElectricityDuty(double electricityDuty) { this.electricityDuty = electricityDuty; }

    public double getMeterRent() { return meterRent; }
    public void setMeterRent(double meterRent) { this.meterRent = meterRent; }

    public double getArrears() { return arrears; }
    public void setArrears(double arrears) { this.arrears = arrears; }

    public double getSubTotal() { return subTotal; }
    public void setSubTotal(double subTotal) { this.subTotal = subTotal; }

    public double getTaxAmount() { return taxAmount; }
    public void setTaxAmount(double taxAmount) { this.taxAmount = taxAmount; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public double getAmountPaid() { return amountPaid; }
    public void setAmountPaid(double amountPaid) { this.amountPaid = amountPaid; }

    public LocalDate getBillDate() { return billDate; }
    public void setBillDate(LocalDate billDate) { this.billDate = billDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }

    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getConnectionType() { return connectionType; }
    public void setConnectionType(String connectionType) { this.connectionType = connectionType; }
}
