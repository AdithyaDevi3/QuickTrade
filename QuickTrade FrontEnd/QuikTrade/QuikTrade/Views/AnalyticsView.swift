// AnalyticsView.swift — stock analytics with live data and mini price charts.
//
// The user searches for stocks in the Search tab and they appear here for deeper
// analysis. Each row expands to show difficulty-appropriate metrics + a lazy-
// loaded 30-day mini price chart.  Replaced the 12s timer polling with
// .task/async-await.

import SwiftUI
import Charts

// MARK: - Model

/// A stock item that the user has explored.  Kept Codable for @AppStorage persistence.
struct LikedStock: Codable, Identifiable {
    var id: String { symbol }
    let symbol: String
    let name: String
    let logoUrl: String
    let matchPercentage: Double
}

// MARK: - ViewModel

/// ViewModel for AnalyticsView.  Loads liked stocks from the search endpoint
/// and caches per-ticker metrics and mini-chart data.
@MainActor
final class AnalyticsViewModel: ObservableObject {

    @Published var likedStocks: [LikedStock] = []
    @Published var selectedStock: LikedStock?
    @Published var isLoading = false
    @Published var difficulty: String = "Beginner"

    // Cache: ticker → (metrics, history) to avoid re-fetching on every expand
    private var metricsCache: [String: StockMetrics] = [:]
    private var historyCache: [String: [StockDataPoint]] = [:]
    private var predictionCache: [String: PredictionDetail] = [:]

    init() {
        // Seed with popular stocks for instant first-open experience
        likedStocks = [
            LikedStock(symbol: "AAPL", name: "Apple Inc.", logoUrl: "", matchPercentage: 100),
            LikedStock(symbol: "MSFT", name: "Microsoft Corp.", logoUrl: "", matchPercentage: 100),
            LikedStock(symbol: "NVDA", name: "NVIDIA Corp.", logoUrl: "", matchPercentage: 100),
            LikedStock(symbol: "TSLA", name: "Tesla Inc.", logoUrl: "", matchPercentage: 100),
            LikedStock(symbol: "AMZN", name: "Amazon.com Inc.", logoUrl: "", matchPercentage: 100),
        ]
    }

    func selectStock(_ stock: LikedStock) {
        selectedStock = stock
    }

    /// Lazily loads metrics + 30-day history for a ticker when its row expands.
    func loadDetailsIfNeeded(for ticker: String) async {
        guard metricsCache[ticker] == nil else { return }
        do {
            async let m = APIService.shared.fetchMetrics(ticker: ticker)
            async let h = APIService.shared.fetchHistorical(ticker: ticker, days: 30)
            async let p = APIService.shared.fetchPrediction(ticker: ticker)
            metricsCache[ticker]    = try await m
            historyCache[ticker]    = try await h
            predictionCache[ticker] = try? await p
        } catch {
            // Metrics unavailable — row still shows, just without live data
        }
    }

    func metrics(for ticker: String) -> StockMetrics? { metricsCache[ticker] }
    func history(for ticker: String) -> [StockDataPoint] { historyCache[ticker] ?? [] }
    func prediction(for ticker: String) -> PredictionDetail? { predictionCache[ticker] }
}

// MARK: - AnalyticsView

/// Analytics tab — expandable rows with live metrics and mini charts.
struct AnalyticsView: View {
    @StateObject private var viewModel = AnalyticsViewModel()

    var body: some View {
        NavigationView {
            VStack(alignment: .leading) {
                List(viewModel.likedStocks) { stock in
                    StockRow(stock: stock, viewModel: viewModel)
                }
                .listStyle(PlainListStyle())
            }
            .navigationTitle("Analytics")
        }
    }
}

// MARK: - StockRow

/// An expandable analytics row with lazy-loaded metrics and a 30-day mini chart.
struct StockRow: View {
    let stock: LikedStock
    @ObservedObject var viewModel: AnalyticsViewModel
    @State private var isExpanded = false

    var body: some View {
        VStack(spacing: 8) {

            // ── Collapsed header ──────────────────────────────────────────
            HStack {
                AsyncImage(url: URL(string: stock.logoUrl)) { img in
                    img.resizable().scaledToFit()
                } placeholder: {
                    Image(systemName: "chart.bar").foregroundColor(.gray)
                }
                .frame(width: 32, height: 32)
                .clipShape(RoundedRectangle(cornerRadius: 6))

                VStack(alignment: .leading, spacing: 2) {
                    Text(stock.name).font(.headline)
                    Text(stock.symbol).font(.subheadline).foregroundColor(.secondary)
                }

                Spacer()

                // Live price badge
                if let m = viewModel.metrics(for: stock.symbol) {
                    VStack(alignment: .trailing, spacing: 2) {
                        Text(m.formattedPrice).font(.subheadline).bold()
                        Text(m.formattedChangePercent)
                            .font(.caption)
                            .foregroundColor(m.isPositive ? .green : .red)
                    }
                }

                Button {
                    viewModel.selectStock(stock)
                    withAnimation { isExpanded.toggle() }
                } label: {
                    Image(systemName: isExpanded ? "chevron.down" : "chevron.right")
                        .foregroundColor(.blue)
                }
            }

            // ── Expanded detail ───────────────────────────────────────────
            if isExpanded {
                VStack(spacing: 12) {

                    // Difficulty picker
                    Picker("Difficulty", selection: $viewModel.difficulty) {
                        Text("Beginner").tag("Beginner")
                        Text("Intermediate").tag("Intermediate")
                        Text("Advanced").tag("Advanced")
                    }
                    .pickerStyle(.segmented)

                    // Metrics for selected difficulty
                    let metrics = viewModel.metrics(for: stock.symbol)
                    let pred    = viewModel.prediction(for: stock.symbol)

                    switch viewModel.difficulty {
                    case "Intermediate":
                        IntermediateDataView(stock: stock, metrics: metrics)
                    case "Advanced":
                        AdvancedDataView(stock: stock, metrics: metrics, prediction: pred)
                    default:
                        BeginnerDataView(stock: stock, metrics: metrics)
                    }

                    // Mini 30-day price chart
                    let history = viewModel.history(for: stock.symbol)
                    if !history.isEmpty {
                        PriceLineChart(dataPoints: history, ticker: stock.symbol)
                            .frame(height: 120)
                    }
                }
                .padding()
                .background(Color.gray.opacity(0.08))
                .cornerRadius(8)
                .task { await viewModel.loadDetailsIfNeeded(for: stock.symbol) }
            }
        }
        .padding(.vertical, 4)
    }
}

// MARK: - Preview
#Preview {
    AnalyticsView()
}
