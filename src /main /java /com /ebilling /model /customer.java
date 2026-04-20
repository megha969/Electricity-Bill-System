package com.ebilling.model;

import java.time.LocalDate;

/**
 * Represents an electricity customer/consumer.
 */
public class Customer {

    public enum ConnectionType {
        DOMESTIC("Domestic"),
        COMMERCIAL("Commercial"),
        INDUSTRIAL("Industrial");

        private final String label;
        ConnectionType(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

    private String customerId;
    private String name;
    private String address;
    private String phone;
    private String email;
    private ConnectionType connectionType;
    private LocalDate connectionDate;
    private boolean active;

    public Customer() {}

    public Customer(String customerId, String name, String address,
                    String phone, String email, ConnectionType connectionType) {
        this.customerId = customerId;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.connectionType = connectionType;
        this.connectionDate = LocalDate.now();
        this.active = true;
    }

    // Getters & Setters
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public ConnectionType getConnectionType() { return connectionType; }
    public void setConnectionType(ConnectionType connectionType) { this.connectionType = connectionType; }

    public LocalDate getConnectionDate() { return connectionDate; }
    public void setConnectionDate(LocalDate connectionDate) { this.connectionDate = connectionDate; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    @Override
    public String toString() {
        return String.format("[%s] %s | %s | %s", customerId, name, connectionType.getLabel(), address);
    }
}
