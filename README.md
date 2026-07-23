# QuickTrade

A full-stack stock market learning and prediction app.

- **iOS app** (`QuickTrade FrontEnd/`) — SwiftUI, 5-tab layout, Swift Charts, live data
- **springBackend** — Data ingestion from Polygon.io, technical-indicator algorithm engine (RSI/MACD/Bollinger/SMA), prediction scheduler
- **QuickTrade Integrator** — REST API gateway (9 endpoints) consumed by the iOS app

---

## Architecture

```
┌─────────────────────────────┐
│   iOS App (SwiftUI)         │
│   QuickTrade FrontEnd/      │
│   • Home  • Search          │
│   • Analytics  • Predict    │
│   • Learn (Quiz)            │
└───────────┬─────────────────┘
            │ HTTP / localhost:8080
            ▼
┌─────────────────────────────┐
│  QuickTrade Integrator      │  ← REST API (port 8080)
│  GET /api/stocks/*          │
│  GET /api/predictions/*     │
│  GET /api/learn/*           │
└───────────┬─────────────────┘
            │ Shared PostgreSQL
            ▼
┌─────────────────────────────┐     ┌──────────────────┐
│  springBackend              │────▶│  Polygon.io API  │
│  • Data ingestion           │     └──────────────────┘
│  • Algorithm engine         │
│  • Prediction scheduler     │
│  • Watchlist seeder         │
└─────────────────────────────┘
```

---

## Prerequisites

| Tool | Version | Notes |
|------|---------|-------|
| Java | **21 LTS** | `java -version` to verify |
| Maven | 3.9+ | Bundled `mvnw` in each backend |
| PostgreSQL | 14+ | Must be running locally |
| Xcode | 16+ | iOS 16 target (Swift Charts) |

---

## First-time setup

### 1 — PostgreSQL

```bash
# macOS (Homebrew)
brew install postgresql@16
brew services start postgresql@16

psql -U postgres -c "CREATE DATABASE stocks_db;"
```

### 2 — Environment variables

Both Java backends load config from a `.env` file using Spring's `optional:file:.env[.properties]` import. `.env` files are **gitignored** — never commit them.

```bash
# springBackend
cp springBackend/.env.example springBackend/.env
# open springBackend/.env and fill in real values

# QuickTrade Integrator
cp "QuickTrade Integrator/.env.example" "QuickTrade Integrator/.env"
# open "QuickTrade Integrator/.env" and fill in real values
```

**`.env` values:**

| Variable | Example value | Description |
|----------|---------------|-------------|
| `DB_URL` | `jdbc:postgresql://localhost:5432/stocks_db` | PostgreSQL JDBC URL |
| `DB_USERNAME` | `postgres` | DB user |
| `DB_PASSWORD` | `yourpassword` | DB password |
| `POLYGON_API_KEY` | `your_key_here` | [Polygon.io](https://polygon.io) free-tier key |

> ⚠️ **Rotate your Polygon.io API key** if the old key was ever committed to a shared / public repository. Issue a new key at [polygon.io/dashboard](https://polygon.io/dashboard).

### 3 — Seed stock names

The `nasdaq.json` file in `springBackend/` is loaded automatically on startup by `WatchlistSeeder` and `ApiCall`. No manual step needed.

---

## Running the backends

Both services share the same PostgreSQL database. Start `springBackend` first so Hibernate auto-creates the schema.

```bash
# Terminal 1 — springBackend (data ingestion + algorithm engine)
cd springBackend
mvn spring-boot:run

# Terminal 2 — QuickTrade Integrator (REST API, port 8081 to avoid conflict)
cd "QuickTrade Integrator"
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

> The iOS app points to `http://localhost:8080` by default (see `APIService.swift → Config.baseURL`). If you run the Integrator on 8081, update that constant.

### Verify the API is up

```bash
curl http://localhost:8080/api/learn/definitions?difficulty=beginner
curl http://localhost:8080/api/stocks/top-movers
```

---

## Running the iOS app

1. Open `QuickTrade FrontEnd/QuikTrade/QuikTrade.xcodeproj` in Xcode 16+.
2. Select an iOS 16+ simulator or device.
3. Press **Run** (⌘R).

The app requires the Integrator to be running locally for live data. It falls back gracefully with skeleton loaders if the server is unreachable.

---

## Project structure

```
QuickTrade/
├── springBackend/              Data ingestion + algorithm engine
│   ├── src/main/java/com/quicktrade/
│   │   ├── entity/             JPA entities (Stocks, PredictionResult, …)
│   │   ├── repository/         Spring Data repositories
│   │   ├── service/            TechnicalIndicatorService, PredictionService
│   │   └── stockMarketApiService/  ApiCall (Polygon.io, nasdaq.json)
│   ├── src/main/resources/
│   │   ├── application.yml     Config (env-var references only)
│   │   ├── .env.example        Copy → .env with real values
│   │   └── nasdaq.json         Stock name/logo seed data
│   └── README.md
│
├── QuickTrade Integrator/      REST API gateway
│   ├── src/main/java/quicktrade/com/
│   │   ├── controller/         RepositoryController (9 endpoints)
│   │   ├── service/            StockService, PredictionClientService, DefinitionsSeeder
│   │   ├── entity/             DTOs + read-only entity mirrors
│   │   └── repository/         JPA repositories
│   ├── src/main/resources/
│   │   ├── application.yml     Config (env-var references only)
│   │   └── .env.example        Copy → .env with real values
│   └── README.md
│
└── QuickTrade FrontEnd/        iOS SwiftUI app
    └── QuikTrade/QuikTrade/
        ├── ContentView.swift   5-tab root
        ├── Views/
        │   ├── HomeView.swift          Live movers + predictions teaser
        │   ├── SearchView.swift        Fuzzy search → StockDetailView
        │   ├── AnalyticsView.swift     Expandable rows + mini charts
        │   ├── PredictionView.swift    Watchlist signals (NEW)
        │   ├── SettingsView.swift      Glossary + quiz launcher
        │   ├── StockDetailView.swift   3-tab detail (Metrics/Chart/Predict)
        │   ├── PredictionDetailView.swift  Full indicator breakdown
        │   ├── QuizView.swift          MCQ quiz with scoring
        │   ├── Services/APIService.swift   Centralised async/await networking
        │   ├── Models/                 Codable structs
        │   └── ViewModels/             ObservableObject view models
        └── README.md (see below)
```

---

## Secrets audit

| Location | Status |
|----------|--------|
| `springBackend/.env` | ✅ gitignored — never tracked |
| `QuickTrade Integrator/.env` | ✅ gitignored — never tracked |
| `application.yml` (both) | ✅ uses `${ENV_VAR}` only — no hardcoded values |
| Git history (old commits) | ⚠️ The Polygon.io API key and `password: postgres` appear in commits predating this session. **Rotate the Polygon.io key** and change the DB password if this repo is public. |

---

## Prediction algorithm

The algorithm runs weekday mornings at 06:45 AM on all 66 default watchlist tickers.

| Indicator | Weight | Bullish signal |
|-----------|--------|----------------|
| RSI (14-period) | 30% | RSI < 30 (oversold) |
| MACD crossover (12-26-9) | 25% | MACD line above signal line |
| SMA trend (20/50-day) | 25% | SMA-20 above SMA-50 (golden cross) |
| Bollinger Band position | 20% | Price near lower band |

Composite score 0–100: **>65 = BULLISH**, **<35 = BEARISH**, **35–65 = NEUTRAL**.
