package com.dayzer0.catalog;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Seeds the catalog deterministically on startup: every clone gets exactly
 * the same rows, so numbers quoted in a pull request are reproducible.
 */
@Component
public class Seeder implements CommandLineRunner {

    static final String[] CATEGORIES = {"beauty", "electronics", "fashion", "grocery", "home", "sports", "toys"};

    private final JdbcTemplate jdbc;
    private final int rows;
    private final long delayMs;

    public Seeder(JdbcTemplate jdbc, @Value("${app.seed-rows}") int rows, @Value("${app.db-delay-ms}") long delayMs) {
        this.jdbc = jdbc;
        this.rows = rows;
        this.delayMs = delayMs;
    }

    @Override
    public void run(String... args) {
        Long existing = jdbc.queryForObject("select count(*) from products", Long.class);
        if (existing != null && existing > 0) {
            return;
        }
        Random rng = new Random(42);
        List<Object[]> batch = new ArrayList<>();
        for (int i = 1; i <= rows; i++) {
            batch.add(new Object[] {
                (long) i,
                "Product " + i,
                CATEGORIES[rng.nextInt(CATEGORIES.length)],
                999 + rng.nextInt(500000),
                Math.round((1 + rng.nextDouble() * 4) * 10) / 10.0,
                rng.nextInt(10) > 1,
            });
            if (batch.size() >= 10000) {
                insert(batch);
                batch.clear();
            }
        }
        if (!batch.isEmpty()) {
            insert(batch);
        }
        System.out.printf("seeded %d products — every query pays ~%dms%n", rows, delayMs);
    }

    private void insert(List<Object[]> batch) {
        jdbc.batchUpdate(
            "insert into products (id, name, category, price_paise, rating, in_stock) values (?, ?, ?, ?, ?, ?)",
            batch);
    }
}
