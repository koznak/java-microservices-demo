package com.profile.demo.shared;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderSummary(
        UUID orderId,
        String customerEmail,
        Instant createdUtc,
        List<PricedOrderLine> lines,
        BigDecimal subtotal,
        BigDecimal tax,
        BigDecimal total,
        String currency) {
}
