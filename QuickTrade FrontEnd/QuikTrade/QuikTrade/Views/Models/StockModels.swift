// StockModels.swift — Codable structs for the stock data endpoints.
//
// Each struct matches the JSON shape produced by the QuickTrade Integrator's
// REST layer.  `convertFromSnakeCase` decoding is active in APIService so
// property names use camelCase throughout.

import Foundation

// MARK: - Search

/// A fuzzy-search result returned by `GET /api/search`.
struct StockMatch: Codable, Identifiable {
    /// Computed from symbol so instances are Identifiable without an extra DB id.
    var id: String { symbol }
    let symbol: String
    let name: String
    let logoUrl: String
    let matchPercentage: Double
}

// MARK: - Movers / Losers

/// A stock's single-day change data returned by the top-movers and top-losers endpoints.
struct StockGrowth: Codable, Identifiable {
    var id: String { ticker }
    let ticker: String
    /// Most-recent trading day close price.
    let recentClose: Double
    /// Previous trading day close price.
    let yearAgoClose: Double
    /// Percentage change: positive = gain, negative = loss.
    let percentageChange: Double

    /// Convenience: absolute change amount.
    var changeAmount: Double { recentClose - yearAgoClose }

    /// Convenience: formatted string, e.g. "+2.34%".
    var formattedChange: String {
        String(format: "%+.2f%%", percentageChange)
    }
}

// MARK: - Historical data point

/// One trading day's record returned by `GET /api/stocks/{ticker}/historical`.
struct StockDataPoint: Codable, Identifiable {
    var id: String { "\(ticker)-\(date)" }
    let ticker: String
    let open: Double
    let close: Double
    let high: Double
    let low: Double
    let volume: Int
    let date: String

    /// Parses `date` (yyyy-MM-dd) into a `Date` for use with Swift Charts.
    var parsedDate: Date {
        let fmt = DateFormatter()
        fmt.dateFormat = "yyyy-MM-dd"
        return fmt.date(from: date) ?? .now
    }
}

// MARK: - Metrics

/// Snapshot metrics returned by `GET /api/stocks/{ticker}/metrics`.
struct StockMetrics: Codable {
    let ticker: String
    let currentPrice: Double
    let open: Double
    let high: Double
    let low: Double
    let volume: Int
    let week52High: Double
    let week52Low: Double
    let changeAmount: Double
    let changePercent: Double

    /// Formatted price string, e.g. "$185.40".
    var formattedPrice: String {
        String(format: "$%.2f", currentPrice)
    }

    /// Formatted percentage change, e.g. "+1.23%".
    var formattedChangePercent: String {
        String(format: "%+.2f%%", changePercent)
    }

    /// True if price moved up from open.
    var isPositive: Bool { changePercent >= 0 }
}
