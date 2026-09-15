// OrderSheetView.swift — buy/sell confirmation sheet, opened from StockDetailView.

import SwiftUI

struct OrderSheetView: View {
    let ticker: String
    let currentPrice: Double
    @ObservedObject var portfolioViewModel: PortfolioViewModel

    @Environment(\.dismiss) private var dismiss
    @State private var side: TradeSide = .buy
    @State private var quantityText = ""
    @State private var isSubmitting = false

    private var quantity: Double? {
        Double(quantityText).flatMap { $0 > 0 ? $0 : nil }
    }

    private var estimatedTotal: Double {
        (quantity ?? 0) * currentPrice
    }

    var body: some View {
        NavigationStack {
            Form {
                Section {
                    Picker("Side", selection: $side) {
                        Text("Buy").tag(TradeSide.buy)
                        Text("Sell").tag(TradeSide.sell)
                    }
                    .pickerStyle(.segmented)
                }

                Section("\(ticker) @ \(currentPrice.formatted(.currency(code: "USD"))) (latest close)") {
                    TextField("Quantity", text: $quantityText)
                        .keyboardType(.decimalPad)
                    LabeledContent("Estimated total", value: estimatedTotal.formatted(.currency(code: "USD")))
                }

                if let error = portfolioViewModel.orderErrorMessage {
                    Text(error).foregroundColor(.red)
                }
            }
            .navigationTitle("\(side == .buy ? "Buy" : "Sell") \(ticker)")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancel") { dismiss() }
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button(isSubmitting ? "Placing…" : "Confirm") {
                        guard let quantity else { return }
                        isSubmitting = true
                        Task {
                            let success = await portfolioViewModel.placeOrder(
                                ticker: ticker, side: side, quantity: quantity)
                            isSubmitting = false
                            if success { dismiss() }
                        }
                    }
                    .disabled(quantity == nil || isSubmitting)
                }
            }
        }
    }
}
