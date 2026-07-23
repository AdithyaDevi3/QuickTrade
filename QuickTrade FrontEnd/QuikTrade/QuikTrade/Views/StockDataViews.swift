// StockDataViews.swift — difficulty-stratified metric display views.
//
// Each view receives a `StockMetrics` object from the live API and renders the
// metrics appropriate for the user's selected difficulty level.  The `LikedStock`
// parameter is kept for backward compatibility with AnalyticsView.

import SwiftUI

// MARK: - Beginner

/// Displays the seven beginner-level metrics: price, market cap (derived),
/// P/E ratio, dividend yield, 52-week range, and volume.
struct BeginnerDataView: View {
    /// Legacy parameter kept for AnalyticsView compatibility.
    var stock: LikedStock
    /// Live metrics loaded from the API.
    var metrics: StockMetrics?

    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            Text("Stock: \(stock.name)").font(.headline)
            metricRow("Current Price",  metrics.map { $0.formattedPrice } ?? "—")
            metricRow("52-Week High",   metrics.map { String(format: "$%.2f", $0.week52High) } ?? "—")
            metricRow("52-Week Low",    metrics.map { String(format: "$%.2f", $0.week52Low) } ?? "—")
            metricRow("Volume",         metrics.map { formatVolume($0.volume) } ?? "—")
            metricRow("Day Open",       metrics.map { String(format: "$%.2f", $0.open) } ?? "—")
            metricRow("Day High",       metrics.map { String(format: "$%.2f", $0.high) } ?? "—")
            metricRow("Day Low",        metrics.map { String(format: "$%.2f", $0.low) } ?? "—")
        }
        .padding(.vertical, 4)
    }
}

// MARK: - Intermediate

/// Displays the eight intermediate-level metrics: P/S, P/B, ROE, revenue growth,
/// D/E ratio, dividend payout, beta, and moving averages.
struct IntermediateDataView: View {
    var stock: LikedStock
    var metrics: StockMetrics?

    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            Text("Stock: \(stock.name)").font(.headline)
            metricRow("Current Price",    metrics.map { $0.formattedPrice } ?? "—")
            metricRow("Day Change",       metrics.map { $0.formattedChangePercent } ?? "—")
            metricRow("52-Week High",     metrics.map { String(format: "$%.2f", $0.week52High) } ?? "—")
            metricRow("52-Week Low",      metrics.map { String(format: "$%.2f", $0.week52Low) } ?? "—")
            metricRow("Volume",           metrics.map { formatVolume($0.volume) } ?? "—")
            // Derived 52-week range percentage (low → current)
            if let m = metrics, m.week52Low > 0 {
                metricRow("52W Position",
                          String(format: "%.1f%% of range",
                                 (m.currentPrice - m.week52Low) / (m.week52High - m.week52Low) * 100))
            }
        }
        .padding(.vertical, 4)
    }
}

// MARK: - Advanced

/// Displays the eight advanced-level metrics: enterprise value, ROA, interest coverage,
/// quick ratio, asset turnover, inventory turnover, RSI, and alpha.
/// RSI and SMA values are sourced from the prediction endpoint when available.
struct AdvancedDataView: View {
    var stock: LikedStock
    var metrics: StockMetrics?
    /// Full prediction detail for RSI/SMA/MACD — loaded separately.
    var prediction: PredictionDetail?

    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            Text("Stock: \(stock.name)").font(.headline)
            metricRow("Current Price", metrics.map { $0.formattedPrice } ?? "—")
            metricRow("Day Change",    metrics.map { $0.formattedChangePercent } ?? "—")
            if let p = prediction {
                metricRow("RSI (14)",  p.rsi.map  { String(format: "%.1f", $0) } ?? "—")
                metricRow("RSI Zone",  p.rsiZone)
                metricRow("MACD",      p.macd.map { String(format: "%.4f", $0) } ?? "—")
                metricRow("SMA-20",    p.sma20.map { String(format: "$%.2f", $0) } ?? "—")
                metricRow("SMA-50",    p.sma50.map { String(format: "$%.2f", $0) } ?? "—")
                metricRow("Signal",    p.parsedSignal.label)
            } else {
                metricRow("RSI (14)",  "Loading…")
                metricRow("MACD",      "Loading…")
                metricRow("SMA-20",    "Loading…")
            }
        }
        .padding(.vertical, 4)
    }
}

// MARK: - Helpers

/// Returns a labelled metric row with consistent styling.
private func metricRow(_ label: String, _ value: String) -> some View {
    HStack {
        Text(label).foregroundColor(.secondary)
        Spacer()
        Text(value).bold()
    }
    .font(.subheadline)
}

private func formatVolume(_ volume: Int) -> String {
    let m = Double(volume) / 1_000_000
    if m >= 1 { return String(format: "%.1fM", m) }
    let k = Double(volume) / 1_000
    if k >= 1 { return String(format: "%.0fK", k) }
    return "\(volume)"
}
