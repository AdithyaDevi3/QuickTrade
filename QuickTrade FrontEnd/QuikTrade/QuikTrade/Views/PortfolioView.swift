// PortfolioView.swift — cash balance, holdings with live P&L, and trade history.

import SwiftUI

struct PortfolioView: View {
    @EnvironmentObject var auth: AuthViewModel
    @StateObject private var viewModel = PortfolioViewModel()

    var body: some View {
        NavigationStack {
            Group {
                if viewModel.isLoading && viewModel.portfolio == nil {
                    ProgressView("Loading portfolio…")
                } else if let error = viewModel.errorMessage {
                    Text(error).foregroundColor(.secondary).padding()
                } else if let portfolio = viewModel.portfolio {
                    List {
                        Section("Account Value") {
                            LabeledContent("Cash", value: currency(portfolio.cashBalance))
                            LabeledContent("Holdings", value: currency(portfolio.holdingsValue))
                            LabeledContent("Total", value: currency(portfolio.totalValue))
                                .bold()
                        }

                        Section("Holdings") {
                            if portfolio.holdings.isEmpty {
                                Text("No holdings yet — search a stock and tap Buy to get started.")
                                    .foregroundColor(.secondary)
                            } else {
                                ForEach(portfolio.holdings) { holding in
                                    HoldingRow(holding: holding)
                                }
                            }
                        }

                        Section("Recent Trades") {
                            if viewModel.trades.isEmpty {
                                Text("No trades yet.").foregroundColor(.secondary)
                            } else {
                                ForEach(viewModel.trades) { trade in
                                    TradeRow(trade: trade)
                                }
                            }
                        }
                    }
                    .refreshable { await viewModel.load() }
                }
            }
            .navigationTitle("Portfolio")
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("Log Out") { auth.logout() }
                }
            }
            .task { await viewModel.load() }
        }
    }

    private func currency(_ value: Double) -> String {
        value.formatted(.currency(code: "USD"))
    }
}

private struct HoldingRow: View {
    let holding: Holding

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            HStack {
                Text(holding.ticker).bold()
                Spacer()
                Text(holding.marketValue.formatted(.currency(code: "USD")))
            }
            HStack {
                Text("\(holding.quantity, specifier: "%.2f") sh @ \(holding.avgCostBasis.formatted(.currency(code: "USD")))")
                    .font(.caption)
                    .foregroundColor(.secondary)
                Spacer()
                Text(holding.unrealizedPnl.formatted(.currency(code: "USD")))
                    .font(.caption)
                    .foregroundColor(holding.unrealizedPnl >= 0 ? .green : .red)
            }
        }
    }
}

private struct TradeRow: View {
    let trade: Trade

    var body: some View {
        HStack {
            Text(trade.side == .buy ? "Buy" : "Sell")
                .foregroundColor(trade.side == .buy ? .green : .red)
                .bold()
            Text(trade.ticker)
            Spacer()
            Text("\(trade.quantity, specifier: "%.2f") @ \(trade.price.formatted(.currency(code: "USD")))")
                .font(.caption)
                .foregroundColor(.secondary)
        }
    }
}

#Preview {
    PortfolioView().environmentObject(AuthViewModel())
}
