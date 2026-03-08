package com.profile.demo.shared;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class PricingEngine {
    private static final BigDecimal TAX_RATE = new BigDecimal("0.10");

    private PricingEngine() {
    }

    public static OrderSummary buildOrder(CreateOrderRequest request, Map<Integer, CatalogItem> catalog) {
        if (request == null) {
            throw new IllegalArgumentException("Request is required.");
        }

        if (request.customerEmail() == null || request.customerEmail().isBlank()) {
            throw new IllegalArgumentException("Customer email is required.");
        }

        if (request.lines() == null || request.lines().isEmpty()) {
            throw new IllegalArgumentException("At least one order line is required.");
        }

        List<PricedOrderLine> pricedLines = new ArrayList<>();

        for (OrderLineRequest line : request.lines()) {
            if (line.quantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than zero.");
            }

            CatalogItem item = catalog.get(line.catalogItemId());
            if (item == null) {
                throw new IllegalArgumentException("Catalog item '" + line.catalogItemId() + "' was not found.");
            }

                BigDecimal lineTotal = item.price()
                    .multiply(BigDecimal.valueOf(line.quantity()))
                    .setScale(2, RoundingMode.HALF_UP);
                pricedLines.add(new PricedOrderLine(item.id(), item.name(), line.quantity(), item.price(), lineTotal));
            }

            BigDecimal subtotal = pricedLines.stream()
                .map(PricedOrderLine::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
            BigDecimal tax = subtotal.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
            BigDecimal total = subtotal.add(tax).setScale(2, RoundingMode.HALF_UP);
            String currency = catalog.values().stream().findFirst().map(CatalogItem::currency).orElse("USD");

            return new OrderSummary(UUID.randomUUID(), request.customerEmail(), Instant.now(), pricedLines, subtotal, tax, total, currency);
            }
        }
