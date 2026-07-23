# QuickTrade — springBackend

Data-ingestion and algorithm service for the QuickTrade platform.

## Responsibilities

| Area | Description |
|------|-------------|
| **Data ingestion** | Fetches daily grouped-aggregate data from the [Polygon.io API](https://polygon.io) and persists it to `stocks_data`. Reloads stock names from `nasdaq.json` classpath resource daily. |
| **Algorithm engine** | `TechnicalIndicatorService` computes RSI, SMA, EMA, MACD, and Bollinger Bands from stored price history. `PredictionService` produces a weighted composite BULLISH/NEUTRAL/BEARISH signal for every watchlist ticker. |
| **Watchlist seeder** | `WatchlistSeeder` populates the `watchlist_entries` table on startup with top-50 US stocks and 16 major ETFs. |

## Requirements

| Tool | Version |
|------|---------|
| Java | 21 (LTS) |
| Maven | 3.9+ |
| PostgreSQL | 14+ |

## Environment variables

Copy `.env.example` to `.env` and fill in your values:

```bash
cp .env.example .env
```

| Variable | Description |
|----------|-------------|
| `DB_URL` | JDBC URL, e.g. `jdbc:postgresql://localhost:5432/stocks_db` |
| `DB_USERNAME` | PostgreSQL username |
| `DB_PASSWORD` | PostgreSQL password |
| `POLYGON_API_KEY` | Your [Polygon.io](https://polygon.io) API key |

> **Security note:** The `.env` file is gitignored. Never commit real credentials.
> The `POLYGON_API_KEY` was previously hardcoded in old commits — **rotate this key** in your Polygon.io dashboard before pushing to a public repository.

## Database setup

```sql
CREATE DATABASE stocks_db;
-- Hibernate auto-creates tables on first run (ddl-auto: update)
```

## Running locally

```bash
# 1. Create and fill .env
cp .env.example .env
# edit .env with your values

# 2. Build and run
mvn spring-boot:run
```

The service starts on **port 8080** by default.

## Scheduled jobs

| Cron | Description |
|------|-------------|
| `0 0 6 * * *` (daily 06:00) | Reload `nasdaq.json` stock names |
| `0 30 6 * * MON-FRI` (weekday 06:30) | Fetch latest day from Polygon.io API |
| `0 45 6 * * MON-FRI` (weekday 06:45) | Recompute predictions for all watchlist tickers |

## Key database tables

| Table | Description |
|-------|-------------|
| `stocks_data` | Daily OHLCV records per ticker |
| `stock_names` | Symbol → company name + logo URL |
| `watchlist_entries` | Curated tickers for the prediction cycle |
| `prediction_results` | Indicator values + composite signal per ticker |
| `top_movers` | Pre-computed daily mover snapshots |
