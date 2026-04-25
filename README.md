# Movie Platform

A personal movie tracking and recommendation platform built with Spring Boot. Search for films, track what you've watched, manage favourites and watchlists, and get AI-powered recommendations based on natural language descriptions.

## Features

- **Movie search** — search TMDB's full catalogue by title
- **Watch tracking** — mark films as watched, add a personal rating (1–10) and notes
- **Favourites & watchlist** — manage lists with a single consolidated data model
- **Trailers** — fetch YouTube trailer links via TMDB
- **AI-powered recommendations** — describe a movie you're in the mood for in plain English; the app uses a custom ML model (FastAPI/Hugging Face) with automatic fallback to a Spring AI (Ollama/Gemma) strategy if the primary model is unavailable
- **AI movie descriptions** — generate compelling film summaries and audience suitability notes via Spring AI
- **Movie links** — store and manage streaming/external links per film

## Architecture

```
Angular frontend (in progress)
        │
Spring Boot REST API (port 8081)
        │                    │
   TMDB API            Recommendation layer
   (movie data)              │            │
                    Custom ML API     Spring AI
                    (FastAPI/HF)     (Ollama local)
        │
   PostgreSQL
```

The recommendation layer uses the **Strategy pattern** — `CUSTOM_API`, `AI`, or `AUTO` mode. `AUTO` tries the custom ML model first and falls back to Spring AI if it's unavailable.

## Tech Stack

- **Java 21**, Spring Boot 3.5
- **Spring Data JPA** + PostgreSQL
- **Spring AI** (OpenAI-compatible, configured for Ollama/Gemma locally)
- **Spring Security** (HTTP Basic)
- **Spring Cache** (in-memory, TMDB responses cached)
- **Springdoc OpenAPI** / Swagger UI
- **Gson** for TMDB/ML API response parsing
- **JUnit 5 + Mockito** — unit and integration tests

## Prerequisites

- Java 21
- Docker Desktop (for PostgreSQL)
- Ollama running locally with `gemma3:1b` pulled (`ollama pull gemma3:1b`)
- TMDB API key — get one free at [themoviedb.org](https://www.themoviedb.org/settings/api)

## Setup

### 1. Set environment variables

Set these before running — either in your shell, IntelliJ run configuration, or a `.env` file:

```
TMDB_API_KEY=your_tmdb_key_here
```

### 2. Start PostgreSQL

```powershell
docker compose up -d postgres
```

### 3. Run the app

```powershell
.\mvnw.cmd spring-boot:run
```

App runs at `http://localhost:8081`

### 4. Explore the API

Swagger UI: `http://localhost:8081/swagger-ui/index.html`

Register a user, then use HTTP Basic auth on all other endpoints.

## Running Tests

```powershell
.\mvnw.cmd test
```

Tests cover: recommendation strategy routing and fallback logic, UserMovie service (favourites, watchlist, status), TMDB service, authentication service, movie link service, and AI strategy behaviour.

## Recommendation API

The `/api/recommend` endpoint accepts a natural language description and returns matching films:

```json
POST /api/recommend
{
  "description": "a slow burn psychological thriller set in Japan",
  "topK": 5,
  "strategy": "AUTO"
}
```

`strategy` options: `CUSTOM_API` (ML model), `AI` (Spring AI/Ollama), `AUTO` (ML with AI fallback).

## Troubleshooting

**Database connection failure** — ensure the PostgreSQL container is running: `docker compose ps`

**TMDB errors** — check `TMDB_API_KEY` is set in your environment

**Recommendation API unavailable** — use `strategy: "AI"` to bypass the custom model and use Ollama directly. Ensure Ollama is running: `ollama serve`

**Port conflict** — change `server.port` in `application.properties`
