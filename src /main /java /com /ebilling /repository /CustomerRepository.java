package com.ebilling.repository;

import com.ebilling.model.Customer;
import com.ebilling.model.Customer.ConnectionType;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * In-memory customer data store with sample data.
 */
public class CustomerRepository {

    private final Map<String, Customer> store = new LinkedHashMap<>();
    private int counter = 1001;

    public CustomerRepository() {
        loadSampleData();
    }

    private void loadSampleData() {
        add(new Customer("C1001", "Ravi Kumar",     "12 MG Road, Bengaluru",     "9845001001", "ravi@email.com",    ConnectionType.DOMESTIC));
        add(new Customer("C1002", "Priya Sharma",   "45 Indiranagar, Bengaluru", "9845001002", "priya@email.com",   ConnectionType.DOMESTIC));
        add(new Customer("C1003", "Suresh Traders", "88 Commercial St, Mumbai",  "9821003003", "suresh@biz.com",    ConnectionType.COMMERCIAL));
        add(new Customer("C1004", "Anita Verma",    "7 Whitefield, Bengaluru",   "9845001004", "anita@email.com",   ConnectionType.DOMESTIC));
        add(new Customer("C1005", "Mehta Industries","Industrial Area, Pune",    "9922005005", "mehta@ind.com",     ConnectionType.INDUSTRIAL));
        add(new Customer("C1006", "Lakshmi Stores", "22 T Nagar, Chennai",       "9444006006", "lakshmi@store.com", ConnectionType.COMMERCIAL));
    }

    private void add(Customer c) {
        store.put(c.getCustomerId(), c);
    }

    public Customer save(Customer customer) {
        if (customer.getCustomerId() == null || customer.getCustomerId().isBlank()) {
            customer.setCustomerId("C" + counter++);
        }
        if (customer.getConnectionDate() == null) {
            customer.setConnectionDate(LocalDate.now());
        }
        store.put(customer.getCustomerId(), customer);
        return customer;
    }

    public Optional<Customer> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<Customer> findAll() {
        return new ArrayList<>(store.values());
    }

    public List<Customer> findActive() {
        return store.values().stream().filter(Customer::isActive).collect(Collectors.toList());
    }

    public List<Customer> findByConnectionType(ConnectionType type) {
        return store.values().stream()
                .filter(c -> c.getConnectionType() == type)
                .collect(Collectors.toList());
    }

    public List<Customer> search(String query) {
        String q = query.toLowerCase();
        return store.values().stream()
                .filter(c -> c.getName().toLowerCase().contains(q)
                        || c.getCustomerId().toLowerCase().contains(q)
                        || c.getAddress().toLowerCase().contains(q)
                        || c.getPhone().contains(q))
                .collect(Collectors.toList());
    }

    public boolean delete(String id) {
        Customer c = store.get(id);
        if (c != null) { c.setActive(false); return true; }
        return false;
    }

    public int count() { return store.size(); }
}
