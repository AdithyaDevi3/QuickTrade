// HomeView.swift — Market overview dashboard tab.
//
// Loads live top-movers, top-losers, and a 3-stock predictions teaser from the
// backend via HomeViewModel.  Uses .redacted(.placeholder) skeleton cards while
// data is in flight so the layout never jumps.

import SwiftUI

struct HomeView: View {

    @StateObject private var viewModel = HomeViewModel()

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 20) {

                // ── Market Overview Header ────────────────────────────────────
                VStack(alignment: .leading, spacing: 4) {
                    Text("Market Overview")
                        .font(.largeTitle).bold()
                    Text(Date.now.formatted(date: .long, time: .omitted))
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                }
                .padding(.horizontal)
                .padding(.top, 8)

                // ── Error banner ─────────────────────────────────────────────
                if let err = viewModel.errorMessage {
                    HStack {
                        Image(systemName: "exclamationmark.triangle.fill").foregroundColor(.orange)
                        Text(err).font(.caption).foregroundColor(.secondary)
                    }
                    .padding(.horizontal)
                }

                // ── Predictions Teaser ────────────────────────────────────────
                if !viewModel.topBullish.isEmpty {
                    VStack(alignment: .leading, spacing: 8) {
                        HStack {
                            Text("Today's Top Picks")
                                .font(.title2).bold()
                            Spacer()
                            Image(systemName: "wand.and.stars").foregroundColor(.blue)
                        }
                        ForEach(viewModel.topBullish) { pred in
                            PredictionTeaserRow(prediction: pred)
                        }
                    }
                    .padding(.horizontal)
                }

                // ── Top Movers ────────────────────────────────────────────────
                SectionView(title: "Top Movers") {
                    if viewModel.isLoading {
                        ForEach(0..<5, id: \.self) { _ in
                            SkeletonRow()
                        }
                    } else {
                        ForEach(viewModel.topMovers) { stock in
                            LiveStockRow(growth: stock)
                        }
                    }
                }

                // ── Top Losers ────────────────────────────────────────────────
                SectionView(title: "Top Losers") {
                    if viewModel.isLoading {
                        ForEach(0..<5, id: \.self) { _ in
                            SkeletonRow()
                        }
                    } else {
                        ForEach(viewModel.topLosers) { stock in
                            LiveStockRow(growth: stock)
                        }
                    }
                }
            }
            .padding(.bottom, 20)
        }
        .navigationTitle("Stocks")
        .task {
            await viewModel.loadData()
        }
    }
}

// MARK: - Sub-views

/// Generic titled section container.
private struct SectionView<Content: View>: View {
    let title: String
    @ViewBuilder let content: () -> Content

    var body: some View {
        VStack(alignment: .leading, spacing: 10) {
            Text(title)
                .font(.title2).bold()
                .padding(.horizontal)
            content()
                .padding(.horizontal)
        }
    }
}

/// A single live-data stock row using `StockGrowth`.
struct LiveStockRow: View {
    let growth: StockGrowth

    var body: some View {
        HStack {
            Text(growth.ticker)
                .font(.headline)
            Spacer()
            Text(String(format: "$%.2f", growth.recentClose))
            Spacer()
            HStack(spacing: 4) {
                Image(systemName: growth.percentageChange > 0
                      ? "arrow.up.right.circle.fill"
                      : (growth.percentageChange < 0
                         ? "arrow.down.right.circle.fill"
                         : "minus.circle.fill"))
                    .foregroundColor(growth.percentageChange > 0 ? .green
                                     : (growth.percentageChange < 0 ? .red : .gray))
                Text(growth.formattedChange)
                    .foregroundColor(growth.percentageChange > 0 ? .green
                                     : (growth.percentageChange < 0 ? .red : .gray))
                    .font(.subheadline)
            }
        }
        .padding(.vertical, 4)
    }
}

/// Skeleton placeholder row shown while data loads.
private struct SkeletonRow: View {
    var body: some View {
        HStack {
            RoundedRectangle(cornerRadius: 4).fill(Color.gray.opacity(0.2))
                .frame(width: 60, height: 14)
            Spacer()
            RoundedRectangle(cornerRadius: 4).fill(Color.gray.opacity(0.2))
                .frame(width: 70, height: 14)
        }
        .padding(.vertical, 6)
    }
}

/// Teaser row in the "Today's Top Picks" section.
private struct PredictionTeaserRow: View {
    let prediction: PredictionSummary

    var body: some View {
        HStack {
            AsyncImage(url: URL(string: prediction.logoUrl ?? "")) { img in
                img.resizable().scaledToFit()
            } placeholder: {
                Image(systemName: "chart.line.uptrend.xyaxis")
                    .foregroundColor(.blue)
            }
            .frame(width: 28, height: 28)
            .clipShape(RoundedRectangle(cornerRadius: 6))

            VStack(alignment: .leading, spacing: 2) {
                Text(prediction.ticker).font(.headline)
                Text(prediction.name ?? "").font(.caption).foregroundColor(.secondary)
            }
            Spacer()
            Text(String(format: "%.0f", prediction.score))
                .font(.title3).bold()
                .foregroundColor(prediction.parsedSignal.color)
            Image(systemName: prediction.parsedSignal.symbolName)
                .foregroundColor(prediction.parsedSignal.color)
        }
        .padding(10)
        .background(Color(.systemGray6))
        .cornerRadius(10)
    }
}

// Keep legacy SectionView/StockRowView so existing callers inside
// the file (e.g. 52-week section placeholders) still compile.
struct StockRowView: View {
    let stockName: String
    let todayPrice: Double
    let percentChange: Double

    var body: some View {
        HStack {
            Text(stockName).font(.headline)
            Spacer()
            Text(String(format: "$%.2f", todayPrice))
            Spacer()
            HStack(spacing: 4) {
                Image(systemName: percentChange > 0
                      ? "arrow.up.right.circle.fill"
                      : (percentChange < 0
                         ? "arrow.down.right.circle.fill"
                         : "minus.circle.fill"))
                    .foregroundColor(percentChange > 0 ? .green : (percentChange < 0 ? .red : .gray))
                Text(String(format: "%.2f%%", percentChange))
            }
        }
    }
}

#Preview {
    NavigationView { HomeView() }
}
