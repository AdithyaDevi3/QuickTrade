# QuickTrade — Integrator (REST API)

The primary REST API gateway for the QuickTrade iOS app. Reads from the shared PostgreSQL database populated by `springBackend` and exposes all endpoints consumed by the mobile client.

## Requirements

| Tool | Version |
|------|---------|
| Java | 21 (LTS) |
| Maven | 3.9+ |
| PostgreSQL | 14+ (shared with springBackend) |

## Environment variables

Copy `.env.example` to `.env` and fill in your values:

```bash
cp .env.example .env
```

| Variable | Description |
|----------|-------------|
| `DB_URL` | JDBC URL — must point to the **same** database as springBackend |
| `DB_USERNAME` | PostgreSQL username |
| `DB_PASSWORD` | PostgreSQL password |
| `POLYGON_API_KEY` | Your [Polygon.io](https://polygon.io) API key (used for future direct calls) |

> **Security note:** `.env` is gitignored. Never commit real credentials.

## Database setup

Both services share the same PostgreSQL database. Run `springBackend` first so Hibernate creates the schema, then start this service.

```sql
CREATE DATABASE stocks_db;  -- if not already created by springBackend
```

## Running locally

```bash
# 1. Ensure springBackend is running (creates the schema and populates data)

# 2. Create and fill .env
cp .env.example .env

# 3. Build and run
mvn spring-boot:run
```

The Integrator starts on **port 8080** (configure a different port if running alongside springBackend).

To run on a different port:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

## API Reference

All endpoints accept and return JSON. CORS is open (`*`) for local development.

### Search

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/search?query={q}` | Fuzzy search by symbol or company name |

### Stocks

| Method | Path | Query params | Description |
|--------|------|-------------|-------------|
| `GET` | `/api/stocks/top-movers` | `limit=10` | Daily top gainers |
| `GET` | `/api/stocks/top-losers` | `limit=10` | Daily top losers |
| `GET` | `/api/stocks/{ticker}/historical` | `days=90` | Chronological price history |
| `GET` | `/api/stocks/{ticker}/metrics` | — | Snapshot: price, volume, 52W range |

### Predictions

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/predictions` | All watchlist predictions (sorted by score) |
| `GET` | `/api/predictions/{ticker}` | Full indicator breakdown for one ticker |

### Learn

| Method | Path | Query params | Description |
|--------|------|-------------|-------------|
| `GET` | `/api/learn/definitions` | `difficulty=beginner\|intermediate\|advanced` | Financial glossary |
| `GET` | `/api/learn/quiz` | `difficulty=beginner`, `count=5` | Shuffled MCQ questions |

### Example requests

```bash
curl http://localhost:8080/api/search?query=AAPL
curl http://localhost:8080/api/stocks/AAPL/metrics
curl http://localhost:8080/api/predictions/AAPL
curl "http://localhost:8080/api/learn/quiz?difficulty=beginner&count=5"
```
