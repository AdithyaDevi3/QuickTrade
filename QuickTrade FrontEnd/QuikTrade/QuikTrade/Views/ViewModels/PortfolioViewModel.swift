// PortfolioViewModel.swift — ViewModel for PortfolioView.

import Foundation

@MainActor
final class PortfolioViewModel: ObservableObject {
    @Published var portfolio: Portfolio?
    @Published var trades: [Trade] = []
    @Published var isLoading = false
    @Published var errorMessage: String?
    @Published var orderErrorMessage: String?

    func load() async {
        isLoading = true
        errorMessage = nil
        defer { isLoading = false }
        do {
            async let portfolioResult = APIService.shared.fetchPortfolio()
            async let tradesResult = APIService.shared.fetchTrades()
            let (p, t) = try await (portfolioResult, tradesResult)
            portfolio = p
            trades = t
        } catch {
            errorMessage = "Could not load your portfolio. Is the server running?"
        }
    }

    /// Places an order and refreshes the portfolio on success.
    /// Returns true on success so the caller can dismiss the order sheet.
    @discardableResult
    func placeOrder(ticker: String, side: TradeSide, quantity: Double) async -> Bool {
        orderErrorMessage = nil
        do {
            _ = try await APIService.shared.placeOrder(ticker: ticker, side: side, quantity: quantity)
            await load()
            return true
        } catch {
            orderErrorMessage = (error as? LocalizedError)?.errorDescription ?? "Order failed."
            return false
        }
    }
}
