import SwiftUI

// MARK: - Models
struct Definition: Identifiable {
    let id = UUID()
    let term: String
    let definition: String
    let example: String
    let application: String
}

// MARK: - Detail View
struct DefinitionDetailView: View{
    let definition: Definition

    var body: some View {
        ScrollView {
            
            VStack(alignment: .leading, spacing: 20) {
                Text(definition.term)
                    .font(.largeTitle)
                    .bold()

                Text("Definition")
                    .font(.headline)
                Text(definition.definition)

                Text("Example in Context")
                    .font(.headline)
                Text(definition.example)

                Text("Application in the Market")
                    .font(.headline)
                Text(definition.application)
            }
            .padding()
        }
        .background(Color(.systemGray6)) // scroll area background
        .navigationTitle(definition.term)
        .navigationBarTitleDisplayMode(.inline)
    }
}

// MARK: - List View
struct DefinitionListView: View {
    let definitions: [Definition]

    var body: some View {
        List(definitions) { definition in
            NavigationLink(destination: DefinitionDetailView(definition: definition)) {
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
let beginnerDefinitions: [Definition] = [
    Definition(
        term: "Current Price",
        definition: "The market price of the stock.",
        example: "If Apple shares are trading at $180, the current price is $180.",
        application: "Investors use the current price to decide entry and exit points for trades."
    ),
    Definition(
        term: "Market Capitalization",
        definition: "Market Cap = Current Price × Number of Outstanding Shares.",
        example: "A company with 10M shares at $50/share has a market cap of $500M.",
        application: "Used to classify companies as small-cap, mid-cap, or large-cap, guiding portfolio strategies."
    ),
    Definition(
        term: "P/E Ratio",
        definition: "P/E Ratio = Current Price / Earnings Per Share (EPS).",
        example: "If a stock trades at $100 and EPS is $5, the P/E ratio is 20.",
        application: "Helps investors gauge whether a stock is overvalued or undervalued compared to peers."
    ),
    Definition(
        term: "EPS",
        definition: "Earnings per Share = (Net Income − Preferred Dividends) / Avg Outstanding Shares.",
        example: "If net income is $1M and 500k shares are outstanding, EPS = $2.",
        application: "A key profitability metric used in valuation models like the P/E ratio."
    ),
    Definition(
        term: "Dividend Yield",
        definition: "Dividend Yield = (Annual Dividends per Share / Current Price) × 100%.",
        example: "A $2 annual dividend with a $50 share price → 4% dividend yield.",
        application: "Used by income-focused investors to evaluate return from dividends."
    ),
    Definition(
        term: "52-Week High/Low",
        definition: "The highest and lowest stock price in the last 52 weeks.",
        example: "Tesla’s stock may have ranged between $150 and $280 in the past year.",
        application: "Helps investors understand volatility and potential support/resistance levels."
    ),
    Definition(
        term: "Volume",
        definition: "The total number of shares traded during a given period.",
        example: "If 5M Apple shares traded today, today’s volume = 5M.",
        application: "High volume often signals strong interest or news-driven movement in a stock."
    )
]

let intermediateDefinitions: [Definition] = [
    Definition(
        term: "P/S Ratio",
        definition: "Price-to-Sales Ratio = Market Cap / Total Revenue.",
        example: "A $500M company with $100M revenue has a P/S of 5.",
        application: "Used to value growth companies that may not yet be profitable."
    ),
    Definition(
        term: "P/B Ratio",
        definition: "Price-to-Book Ratio = Current Price / Book Value per Share.",
        example: "If book value per share is $20 and stock price is $40, P/B = 2.",
        application: "Helps investors evaluate whether a stock is trading above or below its accounting value."
    ),
    Definition(
        term: "ROE",
        definition: "Return on Equity = (Net Income / Shareholder’s Equity) × 100%.",
        example: "If net income is $10M and equity is $50M, ROE = 20%.",
        application: "Measures how effectively a company generates profit from shareholder capital."
    ),
    Definition(
        term: "Revenue Growth",
        definition: "Revenue Growth = ((Current Rev − Previous Rev) / Previous Rev) × 100%.",
        example: "Revenue increased from $100M to $120M → 20% growth.",
        application: "A key measure for identifying expanding companies."
    ),
    Definition(
        term: "Debt-to-Equity Ratio",
        definition: "Debt-to-Equity = Total Liabilities / Shareholder’s Equity.",
        example: "If debt is $200M and equity is $100M, D/E = 2.0.",
        application: "Used to assess financial leverage and risk exposure."
    ),
    Definition(
        term: "Dividend Payout Ratio",
        definition: "Dividend Payout = (Dividends Paid / Net Income) × 100%.",
        example: "If net income is $50M and dividends are $10M, payout ratio = 20%.",
        application: "Shows how much profit is returned to shareholders vs reinvested."
    ),
    Definition(
        term: "Beta",
        definition: "Beta measures volatility relative to the market.",
        example: "A stock with Beta 1.5 moves ~50% more than the market.",
        application: "Used in CAPM to assess risk and expected return."
    ),
    Definition(
        term: "Moving Averages",
        definition: "A calculation of average prices over a set period.",
        example: "A 50-day moving average smooths short-term fluctuations.",
        application: "Traders use moving averages to identify trends and support/resistance levels."
    )
]

let advancedDefinitions: [Definition] = [
    Definition(
        term: "Enterprise Value (EV)",
        definition: "EV = Market Cap + Total Debt − Cash and Equivalents.",
        example: "If Market Cap = $500M, Debt = $200M, Cash = $50M → EV = $650M.",
        application: "Provides a more complete valuation than Market Cap alone."
    ),
    Definition(
        term: "ROA",
        definition: "Return on Assets = (Net Income / Total Assets) × 100%.",
        example: "If net income = $20M and assets = $200M, ROA = 10%.",
        application: "Measures efficiency of asset use in generating profits."
    ),
    Definition(
        term: "Interest Coverage Ratio",
        definition: "ICR = EBIT / Interest Expense.",
        example: "EBIT = $50M, Interest = $10M → ICR = 5.",
        application: "Assesses a company’s ability to pay interest on debt."
    ),
    Definition(
        term: "Quick Ratio",
        definition: "Quick Ratio = (Current Assets − Inventory) / Current Liabilities.",
        example: "($100M − $20M) / $50M = 1.6.",
        application: "Tests liquidity by excluding inventory, which may be harder to liquidate."
    ),
    Definition(
        term: "Asset Turnover Ratio",
        definition: "Asset Turnover = Revenue / Total Assets.",
        example: "Revenue = $400M, Assets = $200M → 2.0.",
        application: "Shows how effectively assets generate revenue."
    ),
    Definition(
        term: "Inventory Turnover Ratio",
        definition: "Inventory Turnover = COGS / Average Inventory.",
        example: "COGS = $500M, Avg Inventory = $100M → 5.",
        application: "Higher turnover means faster movement of goods, reducing holding costs."
    ),
    Definition(
        term: "RSI",
        definition: "Relative Strength Index = 100 − (100 / (1 + Avg Gain / Avg Loss)).",
        example: "An RSI of 70+ suggests overbought, below 30 suggests oversold.",
        application: "Used in technical analysis to identify momentum and reversal points."
    ),
    Definition(
        term: "Alpha",
        definition: "Alpha = Actual Return − [Risk-Free Rate + β × (Market Return − Risk-Free Rate)].",
        example: "If a portfolio returns 12% vs an expected 10%, Alpha = +2%.",
        application: "Measures portfolio performance vs market-adjusted benchmark."
    )
]

// MARK: - Preview
#Preview {
    SettingsView()
}
