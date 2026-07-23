// HomeViewModel.swift — ViewModel for HomeView.
//
// Fetches top-movers, top-losers, and a teaser set of bullish predictions
// concurrently using async let so both requests are in flight at once.

import Foundation
import Combine

/// ViewModel for the HomeView dashboard.
///
/// Publishes live market-mover data loaded from the QuickTrade REST API.
/// All published properties are set on the `@MainActor` so SwiftUI updates
/// happen on the main thread without an explicit `DispatchQueue.main.async`.
@MainActor
final class HomeViewModel: ObservableObject {

    // MARK: Published state

    /// Top daily gainers (up to 10).
    @Published var topMovers: [StockGrowth] = []

    /// Top daily losers (up to 10).
    @Published var topLosers: [StockGrowth] = []

    /// Three most-bullish predictions for the teaser section.
    @Published var topBullish: [PredictionSummary] = []

    /// True while the initial load is in progress.
    @Published var isLoading = false

    /// Non-nil if the last load failed.
    @Published var errorMessage: String?

    // MARK: - Load

    /// Fetches movers, losers, and predictions concurrently.
    /// Safe to call multiple times — sets `isLoading` before starting.
    func loadData() async {
        isLoading = true
        errorMessage = nil
        defer { isLoading = false }

        do {
            async let movers     = APIService.shared.fetchTopMovers(limit: 10)
            async let losers     = APIService.shared.fetchTopLosers(limit: 10)
            async let predictions = APIService.shared.fetchPredictions()

            let (m, l, p) = try await (movers, losers, predictions)
            topMovers  = m
            topLosers  = l
            // Show only the top 3 bullish picks in the teaser card
            topBullish = p.filter { $0.parsedSignal == .bullish }.prefix(3).map { $0 }
        } catch {
            errorMessage = "Could not load market data. Is the server running?"
        }
    }
}
