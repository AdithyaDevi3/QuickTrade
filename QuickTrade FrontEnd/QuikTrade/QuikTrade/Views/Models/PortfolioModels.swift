// PortfolioModels.swift — Codable structs for the /api/portfolio/* endpoints.

import Foundation

enum TradeSide: String, Codable {
    case buy = "BUY"
    case sell = "SELL"
}

struct Holding: Codable, Identifiable {
    var id: String { ticker }
    let ticker: String
    let quantity: Double
    let avgCostBasis: Double
    let currentPrice: Double
    let marketValue: Double
    let unrealizedPnl: Double
}

struct Portfolio: Codable {
    let cashBalance: Double
    let holdingsValue: Double
    let totalValue: Double
    let holdings: [Holding]
}

struct Trade: Codable, Identifiable {
    var id: String { "\(ticker)-\(executedAt)" }
    let ticker: String
    let side: TradeSide
    let quantity: Double
    let price: Double
    let executedAt: String
}

struct OrderRequest: Codable {
    let ticker: String
    let side: TradeSide
    let quantity: Double
}
