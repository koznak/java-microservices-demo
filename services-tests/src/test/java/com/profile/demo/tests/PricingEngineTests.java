package com.profile.demo.tests;

import com.profile.demo.shared.CatalogItem;
import com.profile.demo.shared.CreateOrderRequest;
import com.profile.demo.shared.OrderLineRequest;
import com.profile.demo.shared.OrderSummary;
import com.profile.demo.shared.PricingEngine;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PricingEngineTests {

    @Test
    void buildOrder_returnsExpectedTotals() {
        Map<Integer, CatalogItem> catalog = Map.of(
                1, new CatalogItem(1, "SKU-1", "Test Item", new BigDecimal("10.00"), "USD")
        );

        CreateOrderRequest request = new CreateOrderRequest(
                "dev@example.com",
                List.of(new OrderLineRequest(1, 2))
        );

        OrderSummary order = PricingEngine.buildOrder(request, catalog);

        assertEquals(new BigDecimal("20.00"), order.subtotal());
        assertEquals(new BigDecimal("2.00"), order.tax());
        assertEquals(new BigDecimal("22.00"), order.total());
        assertEquals("USD", order.currency());
        assertEquals(1, order.lines().size());
    }

    @Test
    void buildOrder_throwsWhenItemMissing() {
        CreateOrderRequest request = new CreateOrderRequest(
                "dev@example.com",
                List.of(new OrderLineRequest(999, 1))
        );

        assertThrows(IllegalArgumentException.class, () -> PricingEngine.buildOrder(request, Map.of()));
    }
}
