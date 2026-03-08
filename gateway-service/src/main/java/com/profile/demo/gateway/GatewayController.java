package com.profile.demo.gateway;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.profile.demo.shared.CreateOrderRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping
public class GatewayController {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Value("${services.catalog-url:http://localhost:8081}")
    private String catalogUrl;

    @Value("${services.orders-url:http://localhost:8082}")
    private String ordersUrl;

    public GatewayController(ObjectMapper objectMapper) {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = objectMapper;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of("service", "gateway", "status", "healthy", "timestampUtc", Instant.now().toString());
    }

    @GetMapping("/api/shop/catalog")
    public ResponseEntity<?> catalog() {
        return forwardGet(catalogUrl + "/api/catalog/items");
    }

    @GetMapping("/api/shop/orders")
    public ResponseEntity<?> orders() {
        return forwardGet(ordersUrl + "/api/orders");
    }

    @PostMapping("/api/shop/checkout")
    public ResponseEntity<?> checkout(@RequestBody CreateOrderRequest request) {
        try {
            String json = objectMapper.writeValueAsString(request);
            HttpRequest upstream = HttpRequest.newBuilder()
                    .uri(URI.create(ordersUrl + "/api/orders"))
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = httpClient.send(upstream, HttpResponse.BodyHandlers.ofString());
            return ResponseEntity.status(response.statusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response.body());
        } catch (JsonProcessingException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid checkout payload."));
        } catch (IOException | InterruptedException ex) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of("message", "Orders service unavailable."));
        }
    }

    @GetMapping("/")
    public ResponseEntity<Void> root() {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/api/shop/catalog"))
                .build();
    }

    private ResponseEntity<?> forwardGet(String url) {
        try {
            HttpRequest upstream = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(upstream, HttpResponse.BodyHandlers.ofString());
            return ResponseEntity.status(response.statusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response.body());
        } catch (IOException | InterruptedException ex) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of("message", "Upstream service unavailable."));
        }
    }
}
