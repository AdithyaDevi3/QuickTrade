// PredictionDetailView.swift — full indicator breakdown for a single ticker.
//
// Used by both StockDetailView (Predict tab) and PredictionDetailViewWrapper
// (navigated from PredictionView list).  Displays RSI gauge, MACD histogram,
// Bollinger Band position, SMA cross state, and a "What this means" card.

import SwiftUI
import Charts

// MARK: - PredictionDetailContent

/// The shareable content of the prediction detail screen.
/// Embedded inside ScrollView by both callers.
struct PredictionDetailContent: View {
    let detail: PredictionDetail

    var body: some View {
        VStack(alignment: .leading, spacing: 20) {

            // ── Signal headline ────────────────────────────────────────────
            signalHeader

            // ── Score ring ─────────────────────────────────────────────────
            scoreSection

            // ── RSI gauge ─────────────────────────────────────────────────
            if let rsi = detail.rsi {
                RSIGaugeView(rsi: rsi)
            }

            // ── MACD histogram ─────────────────────────────────────────────
            if let macd = detail.macd, let signal = detail.macdSignal {
                MACDSection(macd: macd, signal: signal, histogram: detail.macdHistogram)
            }

            // ── SMA trend ─────────────────────────────────────────────────
            if let sma20 = detail.sma20, let sma50 = detail.sma50 {
                SMATrendSection(sma20: sma20, sma50: sma50, isGoldenCross: detail.isGoldenCross)
            }

            // ── Bollinger Bands ────────────────────────────────────────────
            if let upper = detail.bollingerUpper, let lower = detail.bollingerLower {
                BollingerSection(upper: upper, lower: lower)
            }

            // ── Educational card ───────────────────────────────────────────
            WhatThisMeansCard(signal: detail.parsedSignal, score: detail.score)

            // ── Data quality note ──────────────────────────────────────────
            if let dp = detail.dataPointsUsed {
                Text("Based on \(dp) trading days of data.")
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
        }
    }

    // MARK: Signal header

    private var signalHeader: some View {
        HStack {
            VStack(alignment: .leading, spacing: 4) {
                Text(detail.ticker).font(.title2).bold()
                Text(detail.name ?? "").foregroundColor(.secondary)
            }
            Spacer()
            VStack(alignment: .trailing, spacing: 4) {
                Label(detail.parsedSignal.label,
                      systemImage: detail.parsedSignal.symbolName)
                    .font(.headline).bold()
                    .foregroundColor(detail.parsedSignal.color)
                Text(detail.type ?? "STOCK")
                    .font(.caption)
                    .padding(.horizontal, 8).padding(.vertical, 2)
                    .background(Color(.systemGray5))
                    .cornerRadius(6)
            }
        }
    }

    // MARK: Score section

    private var scoreSection: some View {
        HStack {
            Text("Composite Score")
                .font(.headline)
            Spacer()
            ScoreRingView(score: detail.score, color: detail.parsedSignal.color)
                .frame(width: 60, height: 60)
        }
        .padding()
        .background(Color(.systemGray6))
        .cornerRadius(12)
    }
}

// MARK: - RSIGaugeView

/// Horizontal RSI gauge with coloured zones and a current-value marker.
struct RSIGaugeView: View {
    let rsi: Double

    private var normalised: Double { rsi / 100.0 }

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                Text("RSI (14)").font(.headline)
                Spacer()
                Text(String(format: "%.1f", rsi))
                    .font(.headline).bold()
                Text("·")
                Text(rsiZoneLabel)
                    .font(.subheadline)
                    .foregroundColor(rsiZoneColor)
            }

            GeometryReader { geo in
                ZStack(alignment: .leading) {
                    // Background gradient: green → gray → red
                    LinearGradient(
                        gradient: Gradient(stops: [
                            .init(color: .green.opacity(0.8), location: 0),
                            .init(color: .gray.opacity(0.4), location: 0.3),
                            .init(color: .gray.opacity(0.4), location: 0.7),
                            .init(color: .red.opacity(0.8), location: 1)
                        ]),
                        startPoint: .leading, endPoint: .trailing
                    )
                    .frame(height: 10)
                    .cornerRadius(5)

                    // Current value marker
                    Circle()
                        .fill(Color.white)
                        .frame(width: 18, height: 18)
                        .shadow(radius: 3)
                        .offset(x: geo.size.width * normalised - 9)
                }
            }
            .frame(height: 18)

            // Zone labels
            HStack {
                Text("Oversold").font(.caption2).foregroundColor(.green)
                Spacer()
                Text("Neutral").font(.caption2).foregroundColor(.secondary)
                Spacer()
                Text("Overbought").font(.caption2).foregroundColor(.red)
            }
        }
        .padding()
        .background(Color(.systemGray6))
        .cornerRadius(12)
    }

    private var rsiZoneLabel: String {
        if rsi < 30 { return "Oversold" }
        if rsi > 70 { return "Overbought" }
        return "Neutral"
    }

    private var rsiZoneColor: Color {
        if rsi < 30 { return .green }
        if rsi > 70 { return .red }
        return .gray
    }
}

// MARK: - MACDSection

/// Shows MACD line, signal line, and histogram value with a direction indicator.
struct MACDSection: View {
    let macd: Double
    let signal: Double
    let histogram: Double?

    var isBullish: Bool { macd > signal }

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                Text("MACD").font(.headline)
                Spacer()
                Image(systemName: isBullish ? "arrow.up.circle.fill" : "arrow.down.circle.fill")
                    .foregroundColor(isBullish ? .green : .red)
                Text(isBullish ? "Bullish crossover" : "Bearish crossover")
                    .font(.subheadline)
                    .foregroundColor(isBullish ? .green : .red)
            }
            HStack(spacing: 20) {
                VStack(alignment: .leading) {
                    Text("MACD Line").font(.caption).foregroundColor(.secondary)
                    Text(String(format: "%.4f", macd)).bold()
                }
                VStack(alignment: .leading) {
                    Text("Signal Line").font(.caption).foregroundColor(.secondary)
                    Text(String(format: "%.4f", signal)).bold()
                }
                if let h = histogram {
                    VStack(alignment: .leading) {
                        Text("Histogram").font(.caption).foregroundColor(.secondary)
                        Text(String(format: "%+.4f", h))
                            .bold()
                            .foregroundColor(h >= 0 ? .green : .red)
                    }
                }
            }
            .font(.subheadline)
        }
        .padding()
        .background(Color(.systemGray6))
        .cornerRadius(12)
    }
}

// MARK: - SMATrendSection

/// Shows the SMA-20 / SMA-50 relationship with golden-cross / death-cross label.
struct SMATrendSection: View {
    let sma20: Double
    let sma50: Double
    let isGoldenCross: Bool

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                Text("Moving Averages").font(.headline)
                Spacer()
                Text(isGoldenCross ? "Golden Cross ✓" : "Death Cross ✗")
                    .font(.subheadline).bold()
                    .foregroundColor(isGoldenCross ? .green : .red)
            }
            HStack(spacing: 30) {
                VStack(alignment: .leading) {
                    Text("SMA-20").font(.caption).foregroundColor(.secondary)
                    Text(String(format: "$%.2f", sma20)).bold()
                }
                VStack(alignment: .leading) {
                    Text("SMA-50").font(.caption).foregroundColor(.secondary)
                    Text(String(format: "$%.2f", sma50)).bold()
                }
                Image(systemName: isGoldenCross ? "arrow.up.right" : "arrow.down.right")
                    .font(.title3)
                    .foregroundColor(isGoldenCross ? .green : .red)
            }
            .font(.subheadline)
        }
        .padding()
        .background(Color(.systemGray6))
        .cornerRadius(12)
    }
}

// MARK: - BollingerSection

/// Shows the Bollinger Band upper/lower values.
struct BollingerSection: View {
    let upper: Double
    let lower: Double

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text("Bollinger Bands (20-day, 2σ)").font(.headline)
            HStack(spacing: 30) {
                VStack(alignment: .leading) {
                    Text("Upper Band").font(.caption).foregroundColor(.secondary)
                    Text(String(format: "$%.2f", upper)).bold().foregroundColor(.red)
                }
                VStack(alignment: .leading) {
                    Text("Lower Band").font(.caption).foregroundColor(.secondary)
                    Text(String(format: "$%.2f", lower)).bold().foregroundColor(.green)
                }
            }
            .font(.subheadline)
        }
        .padding()
        .background(Color(.systemGray6))
        .cornerRadius(12)
    }
}

// MARK: - WhatThisMeansCard

/// A plain-English explanation of the composite signal for learning purposes.
struct WhatThisMeansCard: View {
    let signal: PredictionSignal
    let score: Double

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                Image(systemName: "book.closed.fill").foregroundColor(.blue)
                Text("What This Means").font(.headline)
            }
            Text(explanation)
                .font(.subheadline)
                .foregroundColor(.secondary)
        }
        .padding()
        .background(Color.blue.opacity(0.08))
        .cornerRadius(12)
    }

    private var explanation: String {
        switch signal {
        case .bullish:
            return "The technical indicators suggest \(String(format: "%.0f", score))% bullish momentum. RSI may be in oversold territory, the MACD line is crossing above its signal line, and/or the 20-day SMA has crossed above the 50-day SMA (golden cross). These are historically associated with upward price movement."
        case .bearish:
            return "The technical indicators suggest \(String(format: "%.0f", 100 - score))% bearish momentum. RSI may be overbought, the MACD line is crossing below its signal line, and/or the 20-day SMA has crossed below the 50-day SMA (death cross). Past performance is not a guarantee — always do your own research."
        case .neutral:
            return "The technical indicators are mixed, producing a neutral reading of \(String(format: "%.0f", score)) / 100. No strong directional bias is detected from RSI, MACD, SMA trend, or Bollinger Band position. A wait-and-see approach may be appropriate."
        }
    }
}

// MARK: - Preview

#Preview {
    ScrollView {
        PredictionDetailContent(detail: PredictionDetail(
            ticker: "AAPL", name: "Apple Inc.", logoUrl: nil,
            signal: "BULLISH", score: 72, type: "STOCK",
            rsi: 42.5, macd: 0.0123, macdSignal: 0.0098, macdHistogram: 0.0025,
            sma20: 189.50, sma50: 184.30, bollingerUpper: 195.20, bollingerLower: 183.80,
            dataPointsUsed: 200, computedAt: nil
        ))
        .padding()
    }
}
