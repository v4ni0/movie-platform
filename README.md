
# Movie Platform (Backend)

Spring Boot backend for movie search, trailers, recommendations, and user movie status management.

## Tech Stack

- Java 21
- Spring Boot 3
- Spring Data JPA
- PostgreSQL (Docker)
- Spring AI
- Swagger / OpenAPI

---

## Prerequisites

- Java 21
- Docker Desktop
- (Optional) Maven installed globally  
  (project includes Maven Wrapper: `mvnw.cmd`)

---

## Configuration

Backend reads configuration from `src/main/resources/application.properties`.

Important defaults in this project:

- Server port: `8081`
- Database URL: `jdbc:postgresql://localhost:5432/moviedb`
- DB user: `user`
- DB password: `password`
- Active profile: `local`

---

## 1) Start Database First (Required)

From project root (`movie-platform`), start PostgreSQL:

```powershell
docker compose up -d postgres
```

Check container status:

```powershell
docker compose ps
```

---

## 2) Start Backend

From project root:

```powershell
.\mvnw.cmd spring-boot:run
```

Backend will run on:

- `http://localhost:8081`

---

## 3) Verify Backend

- Swagger UI: `http://localhost:8081/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8081/v3/api-docs`

---

## 4) Run Tests

From project root:

```powershell
.\mvnw.cmd test
```

---

## 5) Stop Services

Stop backend with `Ctrl + C`.

Stop database container:

```powershell
docker compose stop postgres
```

Remove containers/network if needed:

```powershell
docker compose down
```

---

## Troubleshooting

### Database connection failure
- Ensure PostgreSQL container is running:
```powershell
docker compose ps
```
- Ensure credentials in `application.properties` match Docker settings.

### Port already in use
- Change backend port in `src/main/resources/application.properties`:
```ini
server.port=8081
```
(use another free port if needed)

### TMDB API issues
- Ensure `tmdb.api.key` is set correctly (via environment variable or local profile config).
```

If you want, I can also give you a second version for **backend + DB via Docker only** (single-command run).