// SettingsView.swift — Financial glossary and quiz-mode learning hub.
//
// The "Learn" tab.  Presents difficulty-filtered definitions from the local
// hardcoded data set (matching the backend's DefinitionsSeeder) and a
// "Quiz Mode" button that opens QuizView.  Progress badges show the user's
// best quiz score per difficulty level.

import SwiftUI

// MARK: - Local GlossaryEntry (keeps SettingsView self-contained for offline use)

/// Hardcoded glossary entry — matches the fields of the API Definition model
/// so content stays in sync between offline and online modes.
private struct GlossaryEntry: Identifiable {
    let id = UUID()
    let term: String
    let definition: String
    let example: String
    let application: String
}

// MARK: - DefinitionDetailView

struct DefinitionDetailView: View {
    let term: String
    let definition: String
    let example: String
    let application: String

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 20) {
                Text(term).font(.largeTitle).bold()
                Text("Definition").font(.headline)
                Text(definition)
                Text("Example in Context").font(.headline)
                Text(example)
                Text("Application in the Market").font(.headline)
                Text(application)
            }
            .padding()
        }
        .background(Color(.systemGray6))
        .navigationTitle(term)
        .navigationBarTitleDisplayMode(.inline)
    }
}

// MARK: - DefinitionListView

private struct DefinitionListView: View {
    let entries: [GlossaryEntry]

    var body: some View {
        List(entries) { entry in
            NavigationLink(destination: DefinitionDetailView(
                term: entry.term,
                definition: entry.definition,
                example: entry.example,
                application: entry.application
            )) {
                Text(entry.term).font(.headline)
            }
        }
    }
}

// MARK: - SettingsView

struct SettingsView: View {
    @AppStorage("difficulty") var difficulty: String = "Beginner"
    @AppStorage("quiz_best_beginner")     private var bestBeginner     = 0
    @AppStorage("quiz_best_intermediate") private var bestIntermediate = 0
    @AppStorage("quiz_best_advanced")     private var bestAdvanced     = 0

    @State private var showQuiz = false

    private var currentBest: Int {
        switch difficulty {
        case "Intermediate": return bestIntermediate
        case "Advanced":     return bestAdvanced
        default:             return bestBeginner
        }
    }

    var body: some View {
        NavigationStack {
            VStack(spacing: 0) {
                Text("Learn")
                    .font(.largeTitle).bold()
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(.horizontal)
                    .padding(.top, 8)

                // ── Difficulty picker ──────────────────────────────────────
                Picker("Difficulty", selection: $difficulty) {
                    Text("Beginner").tag("Beginner")
                    Text("Intermediate").tag("Intermediate")
                    Text("Advanced").tag("Advanced")
                }
                .pickerStyle(.segmented)
                .padding()

                // ── Quiz button + badge ────────────────────────────────────
                Button {
                    showQuiz = true
                } label: {
                    HStack {
                        Image(systemName: "questionmark.app.fill").font(.title2)
                        VStack(alignment: .leading, spacing: 2) {
                            Text("Quiz Mode").font(.headline)
                            Text("\(difficulty) · 5 questions").font(.caption)
                                .foregroundColor(.secondary)
                        }
                        Spacer()
                        if currentBest > 0 {
                            Text("Best: \(currentBest)/5")
                                .font(.caption).bold()
                                .padding(.horizontal, 8).padding(.vertical, 4)
                                .background(Color.green.opacity(0.15))
                                .foregroundColor(.green)
                                .cornerRadius(8)
                        }
                        Image(systemName: "chevron.right").foregroundColor(.secondary)
                    }
                    .padding()
                    .background(Color(.systemGray6))
                    .cornerRadius(12)
                }
                .padding(.horizontal)
                .padding(.bottom, 12)
                .buttonStyle(.plain)
                .sheet(isPresented: $showQuiz) {
                    QuizView(difficulty: difficulty)
                }

                // ── Definition list ────────────────────────────────────────
                if difficulty == "Beginner" {
                    DefinitionListView(entries: beginnerGlossary)
                } else if difficulty == "Intermediate" {
                    DefinitionListView(entries: intermediateGlossary)
                } else {
                    DefinitionListView(entries: advancedGlossary)
                }
            }
        }
    }
}

// MARK: - Hardcoded Glossary Data

private let beginnerGlossary: [GlossaryEntry] = [
    GlossaryEntry(term: "Current Price",
                  definition: "The most recent price at which a stock was traded in the market.",
                  example: "Apple (AAPL) is currently trading at $185.40.",
                  application: "Used to calculate portfolio value and decide entry/exit points."),
    GlossaryEntry(term: "Market Capitalization",
                  definition: "Market Cap = Current Price × Number of Outstanding Shares.",
                  example: "A company with 10M shares at $50/share has a market cap of $500M.",
                  application: "Classifies companies as small-cap, mid-cap, or large-cap."),
    GlossaryEntry(term: "P/E Ratio",
                  definition: "Price-to-Earnings Ratio = Current Price / Earnings Per Share.",
                  example: "A stock trading at $100 with EPS of $5 has a P/E of 20.",
                  application: "Helps investors gauge whether a stock is over- or undervalued."),
    GlossaryEntry(term: "EPS",
                  definition: "Earnings Per Share = Net Income / Average Outstanding Shares.",
                  example: "Net income $1M, 500K shares outstanding → EPS = $2.",
                  application: "A key profitability metric used in valuation models."),
    GlossaryEntry(term: "Dividend Yield",
                  definition: "Annual dividend per share divided by current share price, as a percentage.",
                  example: "$2 annual dividend on a $50 stock → 4% yield.",
                  application: "Used by income-focused investors to evaluate dividend returns."),
    GlossaryEntry(term: "52-Week High/Low",
                  definition: "The highest and lowest stock price over the past 52 weeks.",
                  example: "Tesla ranged between $150 and $280 over the past year.",
                  application: "Helps identify volatility and potential support/resistance levels."),
    GlossaryEntry(term: "Volume",
                  definition: "Total number of shares traded during a given period.",
                  example: "If 5M Apple shares traded today, today's volume = 5M.",
                  application: "High volume often signals strong interest or news-driven moves.")
]

private let intermediateGlossary: [GlossaryEntry] = [
    GlossaryEntry(term: "P/S Ratio",
                  definition: "Price-to-Sales = Market Cap / Total Revenue.",
                  example: "A $500M company with $100M revenue has a P/S of 5.",
                  application: "Used to value growth companies that may not yet be profitable."),
    GlossaryEntry(term: "P/B Ratio",
                  definition: "Price-to-Book = Current Price / Book Value per Share.",
                  example: "Book value $20, price $40 → P/B = 2.",
                  application: "Evaluates whether a stock trades above or below accounting value."),
    GlossaryEntry(term: "ROE",
                  definition: "Return on Equity = (Net Income / Shareholders' Equity) × 100%.",
                  example: "Net income $10M, equity $50M → ROE = 20%.",
                  application: "Measures how effectively a company generates profit from equity."),
    GlossaryEntry(term: "Revenue Growth",
                  definition: "Revenue Growth = ((Current − Previous) / Previous) × 100%.",
                  example: "Revenue $100M → $120M = 20% growth.",
                  application: "A key metric for identifying expanding companies."),
    GlossaryEntry(term: "Debt-to-Equity Ratio",
                  definition: "D/E = Total Liabilities / Shareholders' Equity.",
                  example: "Debt $200M, equity $100M → D/E = 2.0.",
                  application: "Assesses financial leverage and risk."),
    GlossaryEntry(term: "Dividend Payout Ratio",
                  definition: "Payout = (Dividends Paid / Net Income) × 100%.",
                  example: "Net income $50M, dividends $10M → 20% payout.",
                  application: "Shows how much profit is returned vs reinvested."),
    GlossaryEntry(term: "Beta",
                  definition: "Beta measures a stock's volatility relative to the market.",
                  example: "Beta 1.5 means the stock moves ~50% more than the market.",
                  application: "Used in CAPM to assess risk and expected return."),
    GlossaryEntry(term: "Moving Averages",
                  definition: "An average of closing prices over a set number of past days.",
                  example: "A 50-day SMA averages the last 50 days' close prices.",
                  application: "Traders use MAs to identify trend direction and crossover signals.")
]

private let advancedGlossary: [GlossaryEntry] = [
    GlossaryEntry(term: "Enterprise Value",
                  definition: "EV = Market Cap + Total Debt − Cash.",
                  example: "Market Cap $500M + Debt $200M − Cash $50M = EV $650M.",
                  application: "A more complete valuation than market cap alone."),
    GlossaryEntry(term: "ROA",
                  definition: "Return on Assets = (Net Income / Total Assets) × 100%.",
                  example: "Net income $20M, assets $200M → ROA = 10%.",
                  application: "Measures efficiency of asset use in generating profits."),
    GlossaryEntry(term: "Interest Coverage Ratio",
                  definition: "ICR = EBIT / Interest Expense.",
                  example: "EBIT $50M, interest $10M → ICR = 5.",
                  application: "Assesses ability to service debt obligations."),
    GlossaryEntry(term: "Quick Ratio",
                  definition: "Quick Ratio = (Current Assets − Inventory) / Current Liabilities.",
                  example: "($100M − $20M) / $50M = 1.6.",
                  application: "Tests liquidity excluding inventory."),
    GlossaryEntry(term: "Asset Turnover Ratio",
                  definition: "Asset Turnover = Revenue / Total Assets.",
                  example: "Revenue $400M, assets $200M → 2.0.",
                  application: "Shows how effectively assets generate revenue."),
    GlossaryEntry(term: "Inventory Turnover",
                  definition: "Inventory Turnover = COGS / Average Inventory.",
                  example: "COGS $500M, avg inventory $100M → 5.",
                  application: "Higher turnover = faster goods movement, lower holding costs."),
    GlossaryEntry(term: "RSI",
                  definition: "Relative Strength Index = 100 − (100 / (1 + AvgGain/AvgLoss)). Range 0–100.",
                  example: "RSI > 70 = overbought, RSI < 30 = oversold.",
                  application: "Identifies momentum and potential reversal points."),
    GlossaryEntry(term: "Alpha",
                  definition: "Alpha = Actual Return − Expected Return (CAPM).",
                  example: "Portfolio returns 12%, expected 10% → Alpha = +2%.",
                  application: "Measures risk-adjusted outperformance vs benchmark.")
]

// MARK: - Preview
#Preview {
    SettingsView()
}

                Text(definition.term)
                    .font(.headline)
            }
        }
    }
}

// MARK: - Settings View
struct SettingsView: View {
    @AppStorage("difficulty") var difficulty: String = "Beginner"

    var body: some View {
        NavigationStack {
            VStack {
                Text("Definitions")
                    .font(.largeTitle)
                    .padding()

                Picker("Difficulty", selection: $difficulty) {
                    Text("Beginner").tag("Beginner")
                    Text("Intermediate").tag("Intermediate")
                    Text("Advanced").tag("Advanced")
                }
                .pickerStyle(SegmentedPickerStyle())
                .padding()

                if difficulty == "Beginner" {
                    DefinitionListView(definitions: beginnerDefinitions)
                } else if difficulty == "Intermediate" {
                    DefinitionListView(definitions: intermediateDefinitions)
                } else if difficulty == "Advanced" {
                    DefinitionListView(definitions: advancedDefinitions)
                }

                Spacer()
            }
            .padding()
        }
    }
}

// MARK: - Sample Data
