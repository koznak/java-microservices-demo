package com.profile.demo.shared;

import java.math.BigDecimal;

public record PricedOrderLine(int catalogItemId, String name, int quantity, BigDecimal unitPrice, BigDecimal lineTotal) {
}
