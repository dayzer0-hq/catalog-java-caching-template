package com.dayzer0.catalog;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * The store stands in for the primary database, and it is slow ON PURPOSE.
 *
 * <p>Every query pays {@code app.db-delay-ms} (default 30ms) — the simulated
 * round-trip to a database that is not on your machine — and then pulls the
 * WHOLE table and filters in Java, because nothing here uses a WHERE clause,
 * an index, or a cache.
 *
 * <p>Do not "fix" this class by deleting the delay. The briefs on this rung
 * are about not paying it on every request, and your load-test baseline is
 * only honest if the slow path stays slow.
 */
@Service
public class SlowStore {

    private final ProductRepository repository;
    private final long delayMs;

    public SlowStore(ProductRepository repository, @Value("${app.db-delay-ms}") long delayMs) {
        this.repository = repository;
        this.delayMs = delayMs;
    }

    /** Pays the round-trip cost, then loads EVERY row and filters in Java. */
    public Optional<Product> get(long id) {
        payRoundTrip();
        return repository.findAll().stream() // full table pull — no WHERE
                .filter(p -> p.getId() == id)
                .findFirst();
    }

    /** Returns every matching row. No pagination, no cache. */
    public List<Product> byCategory(String category) {
        payRoundTrip();
        List<Product> all = repository.findAll(); // full table pull — no WHERE
        if (category == null || category.isBlank()) {
            return all;
        }
        return all.stream().filter(p -> category.equals(p.getCategory())).toList();
    }

    private void payRoundTrip() {
        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
