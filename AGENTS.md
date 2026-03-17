# Dispofast Backend — Agent Guide

## Overview

Spring Boot 3 REST API for the Dispofast logistics platform.

- **Language:** Java 17
- **Framework:** Spring Boot 3.5.9
- **Build tool:** Gradle (Kotlin DSL)
- **Database:** PostgreSQL 16 with Flyway migrations
- **API base path:** `/api/v1`
- **Port:** 8080

## Common Commands

```bash
# Start database (required before running the app)
docker-compose up -d

# Run the application
./gradlew bootRun

# Build (compile + test)
./gradlew build

# Run tests only
./gradlew test

# Format code (must pass before committing)
./gradlew spotlessApply

# Check formatting without applying
./gradlew spotlessCheck
```

## Project Structure

```
src/main/java/com/dispocol/dispofast/
├── modules/           # Feature modules (one per business domain)
│   ├── customers/
│   ├── iam/           # Identity & Access Management
│   ├── inventory/
│   ├── orders/
│   ├── pricelist/
│   └── quotes/
└── shared/            # Cross-cutting concerns (error handling, location utils)

src/main/resources/
├── application.yaml          # Base configuration
├── application-dev.yaml      # Dev profile overrides
└── db/migration/             # Flyway SQL migrations (V{n}__{description}.sql)
```

## Module Architecture

Each module follows hexagonal/clean architecture. Never skip layers:

```
module/
├── api/
│   ├── controllers/      # @RestController — HTTP only, no business logic
│   ├── dtos/             # Request/Response DTOs
│   └── mappers/          # MapStruct mappers: DTO ↔ Domain
├── application/
│   ├── interfaces/       # Service ports/contracts (interfaces)
│   └── impl/             # Service implementations (business logic lives here)
├── domain/               # Entities and business rules — no framework dependencies
└── infra/
    └── persistence/      # JPA repositories and entity implementations
```

## Key Conventions

- **Formatting:** Google Java Format enforced by Spotless. Always run `./gradlew spotlessApply` before committing.
- **Mappers:** Use MapStruct (`@Mapper`). Never map manually in controllers or services.
- **DTOs:** Controllers receive and return DTOs, never domain entities.
- **Business logic:** Only in `application/impl/` service classes.
- **Database migrations:** Add new SQL files under `src/main/resources/db/migration/` following the `V{n}__{description}.sql` naming convention. Never modify existing migration files.
- **Architecture tests:** ArchUnit tests enforce layer boundaries. If you add new classes, ensure they follow the package conventions above or the tests will fail.

## Environment Variables

| Variable | Description |
|---|---|
| `DB_PROTOCOL` | e.g. `postgresql` |
| `DB_HOST` | Database host |
| `DB_PORT` | Database port (default `5432`) |
| `DB_NAME` | Database name |
| `DB_USERNAME` | Database user |
| `DB_PASSWORD` | Database password |

For local development these are typically set via `application-dev.yaml` or `docker-compose.yml`.

## Testing

- **Framework:** JUnit 5 + Spring Boot Test
- **Architecture tests:** ArchUnit (validates package boundaries automatically)
- Tests live in `src/test/java/com/dispocol/dispofast/`
- Run with `./gradlew test`

## Adding a New Module

1. Create the package structure under `modules/{module-name}/` following the architecture above.
2. Add a Flyway migration if new tables are needed.
3. Register the new controller — Spring Boot auto-detects `@RestController` beans.
4. Ensure ArchUnit tests still pass with `./gradlew test`.

## API Documentation

Swagger UI is available at `http://localhost:8080/api/v1/swagger-ui.html` when the app is running.
