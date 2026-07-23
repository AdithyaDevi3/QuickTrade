// PredictionModels.swift — Codable structs for the prediction endpoints.
//
// These models map to the JSON returned by `GET /api/predictions` and
// `GET /api/predictions/{ticker}`.  The `PredictionSignal` enum provides
// strongly-typed signal handling with built-in color and label support.

import Foundation
import SwiftUI

// MARK: - Signal

/// The directional signal output by the prediction algorithm.
enum PredictionSignal: String, Codable {
    case bullish = "BULLISH"
    case bearish = "BEARISH"
    case neutral = "NEUTRAL"

    /// SwiftUI color associated with this signal.
    var color: Color {
        switch self {
        case .bullish: return .green
        case .bearish: return .red
        case .neutral: return .gray
        }
    }

    /// Short display label.
    var label: String { rawValue.capitalized }

    /// SF Symbol name for badge icons.
    var symbolName: String {
        switch self {
        case .bullish: return "arrow.up.circle.fill"
        case .bearish: return "arrow.down.circle.fill"
        case .neutral: return "minus.circle.fill"
        }
    }
}

// MARK: - Summary

/// Lightweight prediction returned for each ticker in `GET /api/predictions`.
/// Used to populate the Predictions tab list view.
struct PredictionSummary: Codable, Identifiable {
    var id: String { ticker }
    let ticker: String
    let name: String?
    let logoUrl: String?
    /// Raw signal string — use `parsedSignal` for Swift enum access.
    let signal: String
    /// Composite weighted score 0 (bearish) – 100 (bullish).
    let score: Double
    let rsi: Double?
    let macd: Double?
    let macdSignal: Double?
    /// "STOCK" or "ETF" — nil if watchlist not yet populated.
    let type: String?
    let computedAt: String?

    var parsedSignal: PredictionSignal {
        PredictionSignal(rawValue: signal) ?? .neutral
    }

    /// True if this entry represents an ETF.
    var isETF: Bool { type == "ETF" }
}

// MARK: - Detail

/// Full indicator breakdown returned by `GET /api/predictions/{ticker}`.
/// Used in the PredictionDetailView.
struct PredictionDetail: Codable {
    let ticker: String
    let name: String?
    let logoUrl: String?
    let signal: String
    let score: Double
    let type: String?
    let rsi: Double?
    let macd: Double?
    let macdSignal: Double?
    let macdHistogram: Double?
    let sma20: Double?
    let sma50: Double?
    let bollingerUpper: Double?
    let bollingerLower: Double?
    let dataPointsUsed: Int?
    let computedAt: String?

    var parsedSignal: PredictionSignal {
        PredictionSignal(rawValue: signal) ?? .neutral
    }

    // ── Derived helpers for the UI ─────────────────────────────────────────

    /// Whether SMA-20 is above SMA-50 (golden cross = bullish).
    var isGoldenCross: Bool {
        guard let s20 = sma20, let s50 = sma50 else { return false }
        return s20 > s50
    }

    /// MACD crossover direction: true if MACD is above signal line.
    var isMACDBullish: Bool {
        guard let m = macd, let s = macdSignal else { return false }
        return m > s
    }

    /// RSI zone label for the UI.
    var rsiZone: String {
        guard let r = rsi else { return "N/A" }
        if r > 70 { return "Overbought" }
        if r < 30 { return "Oversold" }
        return "Neutral"
    }

    /// RSI zone color.
    var rsiZoneColor: Color {
        guard let r = rsi else { return .gray }
        if r > 70 { return .red }
        if r < 30 { return .green }
        return .gray
    }
}
