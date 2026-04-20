package com.ebilling.model;

/**
 * Represents a slab-based tariff rate for a connection type.
 *
 * Slab example (Domestic):
 *   0 – 100 units  → ₹3.50/unit
 *   101 – 200 units → ₹5.00/unit
 *   201 – 300 units → ₹6.50/unit
 *   301+  units     → ₹8.00/unit
 */
public class TariffRate {

    private String connectionType;
    private int slabFrom;       // units
    private int slabTo;         // units (-1 = unlimited)
    private double ratePerUnit; // ₹ per kWh
    private double fixedCharge; // ₹ per month (only on first slab)

    public TariffRate() {}

    public TariffRate(String connectionType, int slabFrom, int slabTo,
                      double ratePerUnit, double fixedCharge) {
        this.connectionType = connectionType;
        this.slabFrom = slabFrom;
        this.slabTo = slabTo;
        this.ratePerUnit = ratePerUnit;
        this.fixedCharge = fixedCharge;
    }

    public boolean appliesTo(double units) {
        return units > slabFrom && (slabTo == -1 || units <= slabTo);
    }

    // Getters & Setters
    public String getConnectionType() { return connectionType; }
    public void setConnectionType(String connectionType) { this.connectionType = connectionType; }

    public int getSlabFrom() { return slabFrom; }
    public void setSlabFrom(int slabFrom) { this.slabFrom = slabFrom; }

    public int getSlabTo() { return slabTo; }
    public void setSlabTo(int slabTo) { this.slabTo = slabTo; }

    public double getRatePerUnit() { return ratePerUnit; }
    public void setRatePerUnit(double ratePerUnit) { this.ratePerUnit = ratePerUnit; }

    public double getFixedCharge() { return fixedCharge; }
    public void setFixedCharge(double fixedCharge) { this.fixedCharge = fixedCharge; }

    public String getSlabLabel() {
        return slabTo == -1
                ? slabFrom + "+ units"
                : slabFrom + "–" + slabTo + " units";
    }

    @Override
    public String toString() {
        return String.format("[%s] %s → ₹%.2f/unit + ₹%.2f fixed",
                connectionType, getSlabLabel(), ratePerUnit, fixedCharge);
    }
}
