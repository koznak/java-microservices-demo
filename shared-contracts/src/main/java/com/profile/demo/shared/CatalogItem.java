package com.profile.demo.shared;

import java.math.BigDecimal;

public record CatalogItem(int id, String sku, String name, BigDecimal price, String currency) {
}
