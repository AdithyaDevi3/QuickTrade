// StockDetailView.swift — full-screen detail view for a single stock or ETF.
//
// Opened via NavigationLink from SearchView when the user taps a search result.
// Contains three tabs:
//   1. Metrics — difficulty-stratified snapshot data (StockDataViews)
//   2. Chart   — 90-day closing price line chart (Swift Charts, iOS 16+)
//   3. Predict — full indicator breakdown (PredictionDetailView)

import SwiftUI
import Charts

// MARK: - StockDetailView

/// Detail screen that loads live metrics, historical chart data, and the
/// prediction breakdown for a single ticker.
struct StockDetailView: View {

    /// The search-result match that was tapped.
    let match: StockMatch

    @AppStorage("difficulty") private var difficulty: String = "Beginner"

    // ── State ──────────────────────────────────────────────────────────────
    @State private var metrics: StockMetrics?
    @State private var history: [StockDataPoint] = []
    @State private var prediction: PredictionDetail?
    @State private var selectedTab = 0
    @State private var isLoading = true
    @State private var errorMessage: String?

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {

                // ── Header ─────────────────────────────────────────────────
                headerSection

                // ── Tab picker ─────────────────────────────────────────────
                Picker("", selection: $selectedTab) {
                    Text("Metrics").tag(0)
                    Text("Chart").tag(1)
                    Text("Predict").tag(2)
                }
                .pickerStyle(.segmented)
                .padding(.horizontal)

                // ── Tab content ────────────────────────────────────────────
                Group {
                    switch selectedTab {
                    case 0: metricsTab
                    case 1: chartTab
                    case 2: predictionTab
                    default: EmptyView()
                    }
                }
                .padding(.horizontal)
            }
        }
        .navigationTitle(match.symbol)
        .navigationBarTitleDisplayMode(.inline)
        .task { await loadAll() }
    }

    // MARK: - Header

    private var headerSection: some View {
        HStack(spacing: 12) {
            AsyncImage(url: URL(string: match.logoUrl)) { image in
                image.resizable().scaledToFit()
            } placeholder: {
                Image(systemName: "chart.line.uptrend.xyaxis")
                    .font(.title2).foregroundColor(.blue)
            }
            .frame(width: 44, height: 44)
            .clipShape(RoundedRectangle(cornerRadius: 10))

            VStack(alignment: .leading, spacing: 2) {
                Text(match.symbol).font(.title2).bold()
                Text(match.name).font(.subheadline).foregroundColor(.secondary)
            }

            Spacer()

            if let m = metrics {
                VStack(alignment: .trailing, spacing: 2) {
                    Text(m.formattedPrice).font(.title3).bold()
                    Text(m.formattedChangePercent)
                        .font(.subheadline)
                        .foregroundColor(m.isPositive ? .green : .red)
                }
            }
        }
        .padding(.horizontal)
        .padding(.top, 8)
    }

    // MARK: - Metrics tab

    private var metricsTab: some View {
        let likedStock = LikedStock(symbol: match.symbol, name: match.name,
                                    logoUrl: match.logoUrl, matchPercentage: match.matchPercentage)
        return Group {
            switch difficulty.lowercased() {
            case "intermediate":
                IntermediateDataView(stock: likedStock, metrics: metrics)
            case "advanced":
                AdvancedDataView(stock: likedStock, metrics: metrics, prediction: prediction)
            default:
                BeginnerDataView(stock: likedStock, metrics: metrics)
            }
        }
        .padding(.vertical, 8)
    }

    // MARK: - Chart tab

    private var chartTab: some View {
        Group {
            if history.isEmpty {
                Text("No chart data available.")
                    .foregroundColor(.secondary)
                    .frame(maxWidth: .infinity, minHeight: 200)
            } else {
                PriceLineChart(dataPoints: history, ticker: match.symbol)
                    .frame(height: 240)
            }
        }
    }

    // MARK: - Prediction tab

    private var predictionTab: some View {
        Group {
            if let p = prediction {
                PredictionDetailContent(detail: p)
            } else {
                Text(isLoading ? "Loading prediction…" : "No prediction available.")
                    .foregroundColor(.secondary)
                    .frame(maxWidth: .infinity, minHeight: 200)
            }
        }
    }

    // MARK: - Data loading

    private func loadAll() async {
        isLoading = true
        defer { isLoading = false }
        do {
            async let m = APIService.shared.fetchMetrics(ticker: match.symbol)
            async let h = APIService.shared.fetchHistorical(ticker: match.symbol, days: 90)
            async let p = APIService.shared.fetchPrediction(ticker: match.symbol)
            metrics = try await m
            history = try await h
            prediction = try? await p  // Optional — prediction may not exist yet
        } catch {
            errorMessage = error.localizedDescription
        }
    }
}

// MARK: - PriceLineChart

/// A Swift Charts line chart rendering close prices over time.
///
/// Styled with the app's green colour for upward-trending prices.
struct PriceLineChart: View {
    let dataPoints: [StockDataPoint]
    let ticker: String

    private var lineColor: Color {
        guard let first = dataPoints.first, let last = dataPoints.last else { return .green }
        return last.close >= first.close ? .green : .red
    }

    var body: some View {
        Chart {
            ForEach(dataPoints) { point in
                LineMark(
                    x: .value("Date", point.parsedDate),
                    y: .value("Close", point.close)
                )
                .foregroundStyle(lineColor)
                .interpolationMethod(.catmullRom)
            }
            // Gradient area fill
            ForEach(dataPoints) { point in
                AreaMark(
                    x: .value("Date", point.parsedDate),
                    y: .value("Close", point.close)
                )
                .foregroundStyle(
                    LinearGradient(
                        colors: [lineColor.opacity(0.25), lineColor.opacity(0)],
                        startPoint: .top, endPoint: .bottom
                    )
                )
                .interpolationMethod(.catmullRom)
            }
        }
        .chartXAxis {
            AxisMarks(values: .stride(by: .month, count: 1)) { _ in
                AxisGridLine(stroke: StrokeStyle(lineWidth: 0.5, dash: [4]))
                    .foregroundStyle(Color.gray.opacity(0.3))
                AxisValueLabel(format: .dateTime.month(.abbreviated))
            }
        }
        .chartYAxis {
            AxisMarks(position: .leading) { _ in
                AxisGridLine(stroke: StrokeStyle(lineWidth: 0.5, dash: [4]))
                    .foregroundStyle(Color.gray.opacity(0.3))
                AxisValueLabel(format: .number.precision(.fractionLength(0)))
            }
        }
    }
}

// MARK: - Preview

#Preview {
    NavigationView {
        StockDetailView(match: StockMatch(
            symbol: "AAPL", name: "Apple Inc.",
            logoUrl: "", matchPercentage: 100
        ))
    }
}
