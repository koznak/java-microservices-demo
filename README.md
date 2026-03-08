# java-microservices-demo

A Java equivalent of the `.NET` microservices portfolio demo, built for recruiter review.

## Stack

- Java 21
- Spring Boot 3
- Maven multi-module build
- Docker Compose for local orchestration

## Architecture

- `shared-contracts`
- Shared DTOs and pricing engine.
- `catalog-service`
- Product catalog endpoints.
- `orders-service`
- Checkout and order totals calculation.
- `gateway-service`
- Single entry point that forwards/aggregates requests.
- `services-tests`
- JUnit tests for shared pricing logic.

## Endpoints

### Catalog service (`:5001` via Docker)

- `GET /health`
- `GET /api/catalog/items`
- `GET /api/catalog/items/{id}`

### Orders service (`:5002` via Docker)

- `GET /health`
- `GET /api/orders`
- `GET /api/orders/{id}`
- `POST /api/orders`

### Gateway service (`:5000`)

- `GET /health`
- `GET /api/shop/catalog`
- `GET /api/shop/orders`
- `POST /api/shop/checkout`

## Run locally (without Docker)

Prerequisites: Java 21 and Maven 3.9+

```powershell
mvn clean test
mvn -pl catalog-service spring-boot:run
mvn -pl orders-service spring-boot:run
mvn -pl gateway-service spring-boot:run
```

Then use gateway endpoints at `http://localhost:8080`.

## Run with Docker

```powershell
docker compose up --build
```

Then use gateway endpoints at `http://localhost:5000`.

Sample checkout payload:

```json
{
  "customerEmail": "candidate@example.com",
  "lines": [
    { "catalogItemId": 1, "quantity": 2 },
    { "catalogItemId": 2, "quantity": 1 }
  ]
}
```

## Notes for reviewers

- Data is intentionally in memory to keep service boundaries and request flow easy to evaluate.
- Production next steps: persistence, tracing, auth, and async messaging.
"# java-microservices-demo" 
