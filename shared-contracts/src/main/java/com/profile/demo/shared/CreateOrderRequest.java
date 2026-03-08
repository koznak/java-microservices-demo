package com.profile.demo.shared;

import java.util.List;

public record CreateOrderRequest(String customerEmail, List<OrderLineRequest> lines) {
}
