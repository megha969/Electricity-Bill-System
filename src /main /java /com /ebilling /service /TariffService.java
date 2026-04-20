package com.ebilling.service;

import com.ebilling.model.Customer.ConnectionType;
import com.ebilling.model.TariffRate;

import java.util.*;

/**
 * Manages tariff slabs and calculates energy charges.
 *
 * Tariff Structure (illustrative):
 *
 * DOMESTIC:
 *   0–100 units   → ₹3.50/unit, Fixed: ₹50
 *   101–200 units → ₹5.00/unit, Fixed: ₹75
 *   201–300 units → ₹6.50/unit, Fixed: ₹100
 *   301+  units   → ₹8.00/unit, Fixed: ₹150
 *
 * COMMERCIAL:
 *   0–200 units   → ₹6.00/unit, Fixed: ₹150
 *   201–500 units → ₹7.50/unit, Fixed: ₹200
 *   501+  units   → ₹9.00/unit, Fixed: ₹300
 *
 * INDUSTRIAL:
 *   0–500 units   → ₹5.50/unit, Fixed: ₹500
 *   501–2000 units→ ₹7.00/unit, Fixed: ₹750
 *   2001+ units   → ₹8.50/unit, Fixed: ₹1000
 */
public class TariffService {

    // Fuel Surcharge % of energy charge
    public static final double FUEL_SURCHARGE_RATE  = 0.06;
    // Electricity Duty % of energy charge
    public static final double ELECTRICITY_DUTY_RATE = 0.05;
    // GST / Tax rate
    public static final double TAX_RATE              = 0.18;
    // Meter Rent (per month)
    public static final double METER_RENT            = 25.0;

    private final Map<String, List<TariffRate>> tariffs = new HashMap<>();

    public TariffService() {
        loadTariffs();
    }

    private void loadTariffs() {
        // DOMESTIC slabs
        tariffs.put(ConnectionType.DOMESTIC.name(), Arrays.asList(
                new TariffRate("DOMESTIC",   0, 100,  3.50,  50.0),
                new TariffRate("DOMESTIC", 100, 200,  5.00,  75.0),
                new TariffRate("DOMESTIC", 200, 300,  6.50, 100.0),
                new TariffRate("DOMESTIC", 300,  -1,  8.00, 150.0)
        ));
        // COMMERCIAL slabs
        tariffs.put(ConnectionType.COMMERCIAL.name(), Arrays.asList(
                new TariffRate("COMMERCIAL",   0, 200,  6.00, 150.0),
                new TariffRate("COMMERCIAL", 200, 500,  7.50, 200.0),
                new TariffRate("COMMERCIAL", 500,  -1,  9.00, 300.0)
        ));
        // INDUSTRIAL slabs
        tariffs.put(ConnectionType.INDUSTRIAL.name(), Arrays.asList(
                new TariffRate("INDUSTRIAL",    0,  500,  5.50,  500.0),
                new TariffRate("INDUSTRIAL",  500, 2000,  7.00,  750.0),
                new TariffRate("INDUSTRIAL", 2000,   -1,  8.50, 1000.0)
        ));
    }

    /**
     * Calculate energy charge using progressive slab billing.
     * Each slab's units are charged at their respective rate.
     */
    public double calculateEnergyCharge(ConnectionType type, double units) {
        List<TariffRate> slabs = tariffs.get(type.name());
        if (slabs == null) throw new IllegalArgumentException("Unknown connection type: " + type);

        double total = 0.0;
        double remaining = units;

        for (TariffRate slab : slabs) {
            if (remaining <= 0) break;
            int from = slab.getSlabFrom();
            int to   = slab.getSlabTo();

            double slabCapacity = (to == -1) ? remaining : (to - from);
            double unitsInSlab  = Math.min(remaining, slabCapacity);
            total    += unitsInSlab * slab.getRatePerUnit();
            remaining -= unitsInSlab;
        }
        return Math.round(total * 100.0) / 100.0;
    }

    /**
     * Get the fixed charge for a given unit consumption and connection type.
     */
    public double getFixedCharge(ConnectionType type, double units) {
        List<TariffRate> slabs = tariffs.get(type.name());
        if (slabs == null) return 0;
        // Use fixed charge of the highest applicable slab
        TariffRate applicable = slabs.get(0);
        for (TariffRate slab : slabs) {
            if (units > slab.getSlabFrom()) applicable = slab;
        }
        return applicable.getFixedCharge();
    }

    public double getFuelSurcharge(double energyCharge) {
        return Math.round(energyCharge * FUEL_SURCHARGE_RATE * 100.0) / 100.0;
    }

    public double getElectricityDuty(double energyCharge) {
        return Math.round(energyCharge * ELECTRICITY_DUTY_RATE * 100.0) / 100.0;
    }

    public double getMeterRent() { return METER_RENT; }
    public double getTaxRate()   { return TAX_RATE;   }

    public List<TariffRate> getSlabs(ConnectionType type) {
        return Collections.unmodifiableList(tariffs.getOrDefault(type.name(), Collections.emptyList()));
    }

    public Map<String, List<TariffRate>> getAllTariffs() {
        return Collections.unmodifiableMap(tariffs);
    }
}
