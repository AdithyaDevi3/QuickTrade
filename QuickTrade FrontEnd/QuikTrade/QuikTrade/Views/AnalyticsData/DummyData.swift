//
//  DummyData.swift
//  QuikTrade
//
//  Created by Adithya Devi on 9/15/25.
//


import SwiftUI

// MARK: - Data Models
struct StockDetail: Identifiable {
    var id: String { symbol }
    let symbol: String
    let name: String
    let description: String
    let metrics: [String: Double]  // Example: ["P/E Ratio": 24.5, "Market Cap": 2.1e12]
    let keyTerms: [KeyTerm]
}

struct KeyTerm: Identifiable {
    var id: String { term }
    let term: String
    let definition: String
    let example: String
    let marketApplication: String
}

// MARK: - Dummy Data
let dummyStocks: [StockDetail] = [
    StockDetail(
        symbol: "AAPL",
        name: "Apple Inc.",
        description: "Apple designs and manufactures consumer electronics, including the iPhone, iPad, and Mac.",
        metrics: [
            "P/E Ratio": 28.7,
            "Market Cap": 2.8e12,
            "Dividend Yield": 0.6
        ],
        keyTerms: [
            KeyTerm(
                term: "Market Capitalization",
                definition: "The total value of all outstanding shares of a publicly traded company.",
                example: "Apple's market capitalization exceeds $2 trillion.",
                marketApplication: "Used to classify companies into small-cap, mid-cap, or large-cap."
            ),
            KeyTerm(
                term: "Dividend Yield",
                definition: "A financial ratio showing how much a company pays out in dividends each year relative to its stock price.",
                example: "Apple’s dividend yield is below 1%, making it a growth-focused company.",
                marketApplication: "Helps investors compare income generation across stocks."
            )
        ]
    ),
    StockDetail(
        symbol: "TSLA",
        name: "Tesla Inc.",
        description: "Tesla develops electric vehicles, battery storage systems, and clean energy solutions.",
        metrics: [
            "P/E Ratio": 72.1,
            "Market Cap": 900e9,
            "Beta": 2.1
        ],
        keyTerms: [
            KeyTerm(
                term: "Beta",
                definition: "A measure of a stock’s volatility relative to the overall market.",
                example: "Tesla’s beta above 2 shows it’s highly volatile.",
                marketApplication: "Helps assess risk; higher beta means higher potential returns and losses."
            )
        ]
    )
]

