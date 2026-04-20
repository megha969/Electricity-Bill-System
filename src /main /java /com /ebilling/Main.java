package com.ebilling;

import com.ebilling.service.BillingService;
import com.ebilling.ui.ConsoleUI;

/**
 * ⚡ Electricity Billing Management System
 *
 * Entry point. Run with:
 *   mvn exec:java
 * or:
 *   java -jar target/ebilling.jar
 */
public class Main {
    public static void main(String[] args) {
        BillingService service = new BillingService();
        ConsoleUI ui = new ConsoleUI(service);
        ui.start();
    }
}
