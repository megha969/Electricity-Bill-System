package com.ebilling.repository;

import com.ebilling.model.MeterReading;
import com.ebilling.model.Payment;

import java.util.*;
import java.util.stream.Collectors;

/**
 * In-memory meter reading store.
 */
public class MeterReadingRepository {

    private final Map<String, MeterReading> store = new LinkedHashMap<>();
    private int counter = 1;

    public MeterReading save(MeterReading reading) {
        if (reading.getReadingId() == null || reading.getReadingId().isBlank()) {
            reading.setReadingId(String.format("MR%05d", counter++));
        }
        store.put(reading.getReadingId(), reading);
        return reading;
    }

    public Optional<MeterReading> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<MeterReading> findByCustomerId(String customerId) {
        return store.values().stream()
                .filter(r -> r.getCustomerId().equals(customerId))
                .sorted(Comparator.comparing(MeterReading::getReadingDate).reversed())
                .collect(Collectors.toList());
    }

    public Optional<MeterReading> findLatestForCustomer(String customerId) {
        return store.values().stream()
                .filter(r -> r.getCustomerId().equals(customerId))
                .max(Comparator.comparing(MeterReading::getReadingDate));
    }

    public List<MeterReading> findAll() {
        return new ArrayList<>(store.values());
    }

    public int count() { return store.size(); }
}
