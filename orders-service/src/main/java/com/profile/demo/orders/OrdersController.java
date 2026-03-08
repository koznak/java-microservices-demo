package com.profile.demo.orders;

import com.profile.demo.shared.CatalogItem;
import com.profile.demo.shared.CreateOrderRequest;
import com.profile.demo.shared.OrderSummary;
import com.profile.demo.shared.PricingEngine;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping
public class OrdersController {
    private final Map<Integer, CatalogItem> catalog = Map.of(
            1, new CatalogItem(1, "SKU-COFFEE-001", "Colombian Coffee Beans", new BigDecimal("18.90"), "USD"),
            2, new CatalogItem(2, "SKU-MUG-002", "Ceramic Mug", new BigDecimal("11.50"), "USD"),
            3, new CatalogItem(3, "SKU-FRENCHPRESS-003", "French Press", new BigDecimal("34.00"), "USD")
    );

    private final ConcurrentHashMap<UUID, OrderSummary> orders = new ConcurrentHashMap<>();

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of("service", "orders", "status", "healthy", "timestampUtc", Instant.now().toString());
    }

    @GetMapping("/api/orders")
    public List<OrderSummary> listOrders() {
        return orders.values().stream()
                .sorted(Comparator.comparing(OrderSummary::createdUtc).reversed())
                .toList();
    }

    @GetMapping("/api/orders/{id}")
    public OrderSummary getOrder(@PathVariable UUID id) {
        OrderSummary order = orders.get(id);
        if (order == null) {
            throw new ResourceNotFoundException("Order '" + id + "' was not found.");
        }
        return order;
    }

    @PostMapping("/api/orders")
    public ResponseEntity<?> createOrder(@RequestBody CreateOrderRequest request) {
        try {
            OrderSummary order = PricingEngine.buildOrder(request, catalog);
            orders.put(order.orderId(), order);
            return ResponseEntity.status(HttpStatus.CREATED).body(order);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }
}
