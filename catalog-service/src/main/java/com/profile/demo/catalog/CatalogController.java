package com.profile.demo.catalog;

import com.profile.demo.shared.CatalogItem;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
public class CatalogController {
    private static final List<CatalogItem> ITEMS = List.of(
            new CatalogItem(1, "SKU-COFFEE-001", "Colombian Coffee Beans", new BigDecimal("18.90"), "USD"),
            new CatalogItem(2, "SKU-MUG-002", "Ceramic Mug", new BigDecimal("11.50"), "USD"),
            new CatalogItem(3, "SKU-FRENCHPRESS-003", "French Press", new BigDecimal("34.00"), "USD")
    );

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of("service", "catalog", "status", "healthy", "timestampUtc", Instant.now().toString());
    }

    @GetMapping("/api/catalog/items")
    public List<CatalogItem> getItems() {
        return ITEMS;
    }

    @GetMapping("/api/catalog/items/{id}")
    public CatalogItem getItemById(@PathVariable int id) {
        return ITEMS.stream()
                .filter(item -> item.id() == id)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Catalog item '" + id + "' was not found."));
    }
}
