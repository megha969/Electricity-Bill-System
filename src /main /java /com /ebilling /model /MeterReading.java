package com.ebilling.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a meter reading for a billing cycle.
 */
public class MeterReading {

    private String readingId;
    private String customerId;
    private double previousReading;   // kWh
    private double currentReading;    // kWh
    private LocalDate readingDate;
    private String billingMonth;      // e.g. "2024-03"

    public MeterReading() {}

    public MeterReading(String readingId, String customerId,
                        double previousReading, double currentReading,
                        LocalDate readingDate) {
        this.readingId = readingId;
        this.customerId = customerId;
        this.previousReading = previousReading;
        this.currentReading = currentReading;
        this.readingDate = readingDate;
        this.billingMonth = readingDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));
    }

    /** Units consumed this cycle (kWh). */
    public double getUnitsConsumed() {
        return Math.max(0, currentReading - previousReading);
    }

    // Getters & Setters
    public String getReadingId() { return readingId; }
    public void setReadingId(String readingId) { this.readingId = readingId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public double getPreviousReading() { return previousReading; }
    public void setPreviousReading(double previousReading) { this.previousReading = previousReading; }

    public double getCurrentReading() { return currentReading; }
    public void setCurrentReading(double currentReading) { this.currentReading = currentReading; }

    public LocalDate getReadingDate() { return readingDate; }
    public void setReadingDate(LocalDate readingDate) { this.readingDate = readingDate; }

    public String getBillingMonth() { return billingMonth; }
    public void setBillingMonth(String billingMonth) { this.billingMonth = billingMonth; }

    @Override
    public String toString() {
        return String.format("Reading[%s] Customer:%s | Prev:%.1f | Curr:%.1f | Units:%.1f | %s",
                readingId, customerId, previousReading, currentReading, getUnitsConsumed(), billingMonth);
    }
}
