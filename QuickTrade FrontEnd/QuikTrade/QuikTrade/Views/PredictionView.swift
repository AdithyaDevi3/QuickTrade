// PredictionView.swift — 5th tab: predictions for top-50 stocks + major ETFs.
//
// Displays a Picker-switched list of BULLISH/BEARISH/NEUTRAL signal rows
// with a composite score ring. Tapping a row opens PredictionDetailView.

import SwiftUI

// MARK: - PredictionView

/// The Predictions tab showing algorithmic buy/sell signals for the watchlist.
struct PredictionView: View {

    @StateObject private var viewModel = PredictionsViewModel()
    @State private var selectedSegment = 0   // 0 = Stocks, 1 = ETFs

    var body: some View {
        NavigationView {
            VStack(spacing: 0) {

                // ── Segment picker ─────────────────────────────────────────
                Picker("", selection: $selectedSegment) {
                    Text("Top Stocks").tag(0)
                    Text("ETFs").tag(1)
                }
                .pickerStyle(.segmented)
                .padding()

                // ── List ───────────────────────────────────────────────────
                if viewModel.isLoading {
                    ProgressView("Loading predictions…")
                        .frame(maxWidth: .infinity, maxHeight: .infinity)
                } else if let err = viewModel.errorMessage {
                    VStack(spacing: 8) {
                        Image(systemName: "exclamationmark.triangle").font(.largeTitle)
                        Text(err).multilineTextAlignment(.center)
                            .foregroundColor(.secondary)
                    }
                    .padding()
                    .frame(maxHeight: .infinity)
                } else {
                    let items = selectedSegment == 0 ? viewModel.stocks : viewModel.etfs
                    List(items) { pred in
                        NavigationLink(destination: PredictionDetailViewWrapper(ticker: pred.ticker)) {
                            PredictionRow(prediction: pred)
                        }
                    }
                    .listStyle(PlainListStyle())
                    .refreshable { await viewModel.loadPredictions() }
                }
            }
            .navigationTitle("Predictions")
            .task { await viewModel.loadPredictions() }
            .onAppear { viewModel.startAutoRefresh() }
            .onDisappear { viewModel.stopAutoRefresh() }
        }
    }
}

// MARK: - PredictionRow

/// A single list row showing the signal badge, score ring, and key indicators.
struct PredictionRow: View {
    let prediction: PredictionSummary

    var body: some View {
        HStack(spacing: 12) {

            // Logo
            AsyncImage(url: URL(string: prediction.logoUrl ?? "")) { img in
                img.resizable().scaledToFit()
            } placeholder: {
                Image(systemName: "chart.bar.fill").foregroundColor(.gray)
            }
            .frame(width: 36, height: 36)
            .clipShape(RoundedRectangle(cornerRadius: 8))

            // Name + ticker
            VStack(alignment: .leading, spacing: 2) {
                Text(prediction.ticker).font(.headline)
                Text(prediction.name ?? "").font(.caption).foregroundColor(.secondary)
            }

            Spacer()

            // Score ring + signal
            VStack(alignment: .trailing, spacing: 4) {
                ScoreRingView(score: prediction.score, color: prediction.parsedSignal.color)
                    .frame(width: 44, height: 44)
                Label(prediction.parsedSignal.label,
                      systemImage: prediction.parsedSignal.symbolName)
                    .font(.caption2).bold()
                    .foregroundColor(prediction.parsedSignal.color)
                    .labelStyle(.titleAndIcon)
            }

            // Mini RSI
            if let rsi = prediction.rsi {
                VStack(alignment: .trailing, spacing: 2) {
                    Text("RSI").font(.caption2).foregroundColor(.secondary)
                    Text(String(format: "%.0f", rsi))
                        .font(.caption).bold()
                        .foregroundColor(rsi > 70 ? .red : (rsi < 30 ? .green : .primary))
                }
            }
        }
        .padding(.vertical, 6)
    }
}

// MARK: - ScoreRingView

/// Circular progress ring showing the composite prediction score (0–100).
struct ScoreRingView: View {
    let score: Double
    let color: Color

    private var fraction: Double { score / 100.0 }

    var body: some View {
        ZStack {
            Circle().stroke(color.opacity(0.2), lineWidth: 5)
            Circle()
                .trim(from: 0, to: fraction)
                .stroke(color, style: StrokeStyle(lineWidth: 5, lineCap: .round))
                .rotationEffect(.degrees(-90))
            Text(String(format: "%.0f", score))
                .font(.system(size: 10, weight: .bold))
        }
    }
}

// MARK: - PredictionDetailViewWrapper

/// Loads and wraps PredictionDetailContent for a ticker when navigated from the list.
struct PredictionDetailViewWrapper: View {
    let ticker: String

    @State private var detail: PredictionDetail?
    @State private var isLoading = true

    var body: some View {
        Group {
            if let d = detail {
                ScrollView {
                    PredictionDetailContent(detail: d)
                        .padding()
                }
            } else if isLoading {
                ProgressView("Loading…")
            } else {
                Text("No prediction data available.")
                    .foregroundColor(.secondary)
            }
        }
        .navigationTitle(ticker)
        .navigationBarTitleDisplayMode(.inline)
        .task {
            detail = try? await APIService.shared.fetchPrediction(ticker: ticker)
            isLoading = false
        }
    }
}

// MARK: - Preview

#Preview {
    PredictionView()
}
