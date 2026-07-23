// PredictionsViewModel.swift — ViewModel for the Predictions tab.
//
// Loads all watchlist predictions and groups them into Stocks vs ETFs.
// Auto-refreshes every 5 minutes while the tab is visible.

import Foundation
import Combine

/// ViewModel for the Predictions tab.
///
/// Publishes predictions split into stocks and ETFs so the view can
/// display them in separate segments without extra filtering logic.
@MainActor
final class PredictionsViewModel: ObservableObject {

    // MARK: Published state

    @Published var stocks: [PredictionSummary] = []
    @Published var etfs: [PredictionSummary] = []
    @Published var isLoading = false
    @Published var errorMessage: String?

    // MARK: Private

    private var refreshTimer: Timer?

    // MARK: - Load

    /// Fetches all predictions and groups them by type.
    func loadPredictions() async {
        isLoading = true
        errorMessage = nil
        defer { isLoading = false }

        do {
            let all = try await APIService.shared.fetchPredictions()
            stocks = all.filter { $0.type == "STOCK" || $0.type == nil }
            etfs   = all.filter { $0.type == "ETF" }
        } catch {
            errorMessage = "Could not load predictions. Is the server running?"
        }
    }

    /// Starts a 5-minute periodic refresh while the view is on-screen.
    func startAutoRefresh() {
        refreshTimer?.invalidate()
        refreshTimer = Timer.scheduledTimer(withTimeInterval: 300, repeats: true) { [weak self] _ in
            Task { await self?.loadPredictions() }
        }
    }

    /// Stops the auto-refresh timer when the view disappears.
    func stopAutoRefresh() {
        refreshTimer?.invalidate()
        refreshTimer = nil
    }
}
