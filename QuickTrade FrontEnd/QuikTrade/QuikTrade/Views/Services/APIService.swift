// APIService.swift — centralized async/await networking layer for QuikTrade.
//
// All network calls go through this singleton so the base URL, error handling,
// and JSON decoding are defined in exactly one place.  Each method is `async
// throws` so callers can use structured concurrency (Task, async let, etc.)
// without any completion-handler boilerplate.

import Foundation

// MARK: - Configuration

/// App-wide networking configuration.
/// Debug builds hit the local backend; Release builds hit the deployed API.
/// Override `QUICKTRADE_API_BASE_URL` in the Release scheme's environment/Info.plist
/// once the backend has a real deployment URL.
enum Config {
    /// Base URL for the QuickTrade Integrator REST API.
    static let baseURL: String = {
        #if DEBUG
        return "http://localhost:8080"
        #else
        return Bundle.main.object(forInfoDictionaryKey: "QUICKTRADE_API_BASE_URL") as? String
            ?? "https://api.quicktrade.app"
        #endif
    }()
}

// MARK: - APIService

/// Singleton networking layer.
///
/// Usage:
/// ```swift
/// let movers = try await APIService.shared.fetchTopMovers(limit: 10)
/// ```
final class APIService {

    // MARK: Shared instance

    static let shared = APIService()
    private init() {}

    // MARK: Private helpers

    private let session = URLSession.shared
    private let decoder: JSONDecoder = {
        let d = JSONDecoder()
        d.keyDecodingStrategy = .convertFromSnakeCase
        return d
    }()

    /// Builds a URL from a path and optional query items.
    private func url(_ path: String, query: [URLQueryItem] = []) throws -> URL {
        var components = URLComponents(string: Config.baseURL + path)!
        if !query.isEmpty { components.queryItems = query }
        guard let url = components.url else {
            throw URLError(.badURL)
        }
        return url
    }

    private let encoder = JSONEncoder()

    /// Generic fetch: GETs the URL, decodes the body as `T`.
    private func fetch<T: Decodable>(_ url: URL) async throws -> T {
        var request = URLRequest(url: url)
        addAuthHeader(&request)
        let (data, response) = try await session.data(for: request)
        guard let http = response as? HTTPURLResponse, (200..<300).contains(http.statusCode) else {
            throw URLError(.badServerResponse)
        }
        return try decoder.decode(T.self, from: data)
    }

    /// Sends a JSON body via POST and decodes the response as `T`.
    private func post<Body: Encodable, T: Decodable>(_ path: String, body: Body) async throws -> T {
        var request = URLRequest(url: try url(path))
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.httpBody = try encoder.encode(body)
        addAuthHeader(&request)
        let (data, response) = try await session.data(for: request)
        guard let http = response as? HTTPURLResponse else { throw URLError(.badServerResponse) }
        guard (200..<300).contains(http.statusCode) else {
            let message = String(data: data, encoding: .utf8) ?? "Request failed"
            throw APIError.server(status: http.statusCode, message: message)
        }
        return try decoder.decode(T.self, from: data)
    }

    /// Attaches the stored JWT (if any) as a Bearer token.
    private func addAuthHeader(_ request: inout URLRequest) {
        if let token = KeychainHelper.loadToken() {
            request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        }
    }

    // MARK: - Search

    /// Fuzzy-searches stocks and ETFs by symbol or company name.
    ///
    /// - Parameter query: partial symbol or name string
    /// - Returns: list of matches sorted by matchPercentage descending
    func searchStocks(query: String) async throws -> [StockMatch] {
        let url = try url("/api/search", query: [URLQueryItem(name: "query", value: query)])
        return try await fetch(url)
    }

    // MARK: - Stocks

    /// Returns the top daily gainers.
    ///
    /// - Parameter limit: maximum results (default 10)
    func fetchTopMovers(limit: Int = 10) async throws -> [StockGrowth] {
        let url = try url("/api/stocks/top-movers",
                          query: [URLQueryItem(name: "limit", value: "\(limit)")])
        return try await fetch(url)
    }

    /// Returns the top daily losers.
    ///
    /// - Parameter limit: maximum results (default 10)
    func fetchTopLosers(limit: Int = 10) async throws -> [StockGrowth] {
        let url = try url("/api/stocks/top-losers",
                          query: [URLQueryItem(name: "limit", value: "\(limit)")])
        return try await fetch(url)
    }

    /// Returns historical close prices for a ticker (for line chart).
    ///
    /// - Parameters:
    ///   - ticker: stock symbol
    ///   - days: number of trading-day records to fetch
    func fetchHistorical(ticker: String, days: Int = 90) async throws -> [StockDataPoint] {
        let url = try url("/api/stocks/\(ticker)/historical",
                          query: [URLQueryItem(name: "days", value: "\(days)")])
        return try await fetch(url)
    }

    /// Returns snapshot metrics (price, 52-week range, volume) for a ticker.
    ///
    /// - Parameter ticker: stock symbol
    func fetchMetrics(ticker: String) async throws -> StockMetrics {
        let url = try url("/api/stocks/\(ticker)/metrics")
        return try await fetch(url)
    }

    // MARK: - Predictions

    /// Returns the latest prediction summary for every watchlist ticker.
    func fetchPredictions() async throws -> [PredictionSummary] {
        let url = try url("/api/predictions")
        return try await fetch(url)
    }

    /// Returns the full indicator breakdown for a single ticker.
    ///
    /// - Parameter ticker: stock symbol
    func fetchPrediction(ticker: String) async throws -> PredictionDetail {
        let url = try url("/api/predictions/\(ticker)")
        return try await fetch(url)
    }

    // MARK: - Learn

    /// Returns financial term definitions filtered by difficulty level.
    ///
    /// - Parameter difficulty: "beginner", "intermediate", or "advanced"
    func fetchDefinitions(difficulty: String) async throws -> [Definition] {
        let url = try url("/api/learn/definitions",
                          query: [URLQueryItem(name: "difficulty", value: difficulty)])
        return try await fetch(url)
    }

    /// Generates a shuffled multiple-choice quiz.
    ///
    /// - Parameters:
    ///   - difficulty: "beginner", "intermediate", or "advanced"
    ///   - count: number of questions (default 5)
    func fetchQuiz(difficulty: String, count: Int = 5) async throws -> [QuizQuestion] {
        let url = try url("/api/learn/quiz", query: [
            URLQueryItem(name: "difficulty", value: difficulty),
            URLQueryItem(name: "count", value: "\(count)")
        ])
        return try await fetch(url)
    }

    // MARK: - Auth

    /// Registers a new account and provisions its starting $100,000 paper-trading portfolio.
    func register(email: String, password: String) async throws -> AuthResponse {
        try await post("/api/auth/register", body: AuthCredentials(email: email, password: password))
    }

    /// Logs in an existing account.
    func login(email: String, password: String) async throws -> AuthResponse {
        try await post("/api/auth/login", body: AuthCredentials(email: email, password: password))
    }

    // MARK: - Portfolio (requires auth)

    /// Returns cash balance, holdings, and total account value.
    func fetchPortfolio() async throws -> Portfolio {
        let url = try url("/api/portfolio")
        return try await fetch(url)
    }

    /// Returns trade history, most recent first.
    func fetchTrades() async throws -> [Trade] {
        let url = try url("/api/portfolio/trades")
        return try await fetch(url)
    }

    /// Places a buy or sell order at the latest available market price.
    func placeOrder(ticker: String, side: TradeSide, quantity: Double) async throws -> Trade {
        try await post("/api/portfolio/orders", body: OrderRequest(ticker: ticker, side: side, quantity: quantity))
    }
}

private struct AuthCredentials: Encodable {
    let email: String
    let password: String
}

enum APIError: LocalizedError {
    case server(status: Int, message: String)

    var errorDescription: String? {
        switch self {
        case .server(_, let message): return message
        }
    }
}
