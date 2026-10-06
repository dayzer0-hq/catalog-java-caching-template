package com.dayzer0.catalog;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class CatalogController {

    private final SlowStore store;

    public CatalogController(SlowStore store) {
        this.store = store;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "ok");
    }

    @GetMapping("/products/{id}")
    public Product product(@PathVariable long id) {
        return store.get(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "no such product"));
    }

    /** The full list — a large, unpaginated payload, on purpose. */
    @GetMapping("/products")
    public List<Product> products(@RequestParam(required = false) String category) {
        return store.byCategory(category);
    }
}
