import SwiftUI

struct SettingsView: View {
    @AppStorage("difficulty") var difficulty: String = "Beginner"

    var body: some View {
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

            // Display terms based on difficulty level
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

// DefinitionListView to show a list of terms for each difficulty level
struct DefinitionListView: View {
    let definitions: [Definition]

    var body: some View {
        List(definitions) { definition in
            Button(action: {
                // Handle showing pop-up for each definition
            }) {
                Text(definition.term)
                    .font(.headline)
            }
            .alert(isPresented: .constant(false)) {
                Alert(
                    title: Text(definition.term),
                    message: Text(definition.definition),
                    dismissButton: .default(Text("Got it!"))
                )
            }
        }
    }
}

// Definition model to hold the term and its description
struct Definition: Identifiable {
    let id = UUID()
    let term: String
    let definition: String
}

// Sample data for definitions
let beginnerDefinitions: [Definition] = [
    Definition(term: "Current Price", definition: "The market price of the stock."),
    Definition(term: "Market Capitalization", definition: "Market Cap = Current Price × Number of Outstanding Shares."),
    Definition(term: "P/E Ratio", definition: "P/E Ratio = Current Price / Earnings Per Share (EPS)."),
    Definition(term: "EPS", definition: "EPS = (Net Income − Dividends on Preferred Stock) / Average Outstanding Shares."),
    Definition(term: "Dividend Yield", definition: "Dividend Yield = (Annual Dividends Per Share / Current Price) × 100%."),
    Definition(term: "52-Week High/Low", definition: "The highest and lowest price in the last 52 weeks."),
    Definition(term: "Volume", definition: "Total number of shares traded in a given period.")
]

let intermediateDefinitions: [Definition] = [
    Definition(term: "P/S Ratio", definition: "Price-to-Sales Ratio = Market Cap / Total Revenue."),
    Definition(term: "P/B Ratio", definition: "Price-to-Book Ratio = Current Price / Book Value Per Share."),
    Definition(term: "ROE", definition: "Return on Equity = (Net Income / Shareholder’s Equity) × 100%."),
    Definition(term: "Revenue Growth", definition: "Revenue Growth = ((Current Period Revenue − Previous Period Revenue) / Previous Period Revenue) × 100%."),
    Definition(term: "Debt-to-Equity Ratio", definition: "Debt-to-Equity Ratio = Total Liabilities / Shareholder’s Equity."),
    Definition(term: "Dividend Payout Ratio", definition: "Dividend Payout Ratio = (Dividends Paid / Net Income) × 100%."),
    Definition(term: "Beta", definition: "Beta measures the volatility of a stock relative to the market."),
    Definition(term: "Moving Averages", definition: "A calculation to analyze data points by creating a series of averages of different subsets of the full data set.")
]

let advancedDefinitions: [Definition] = [
    Definition(term: "Enterprise Value (EV)", definition: "EV = Market Cap + Total Debt − Cash and Cash Equivalents."),
    Definition(term: "ROA", definition: "Return on Assets = (Net Income / Total Assets) × 100%."),
    Definition(term: "Interest Coverage Ratio", definition: "Interest Coverage Ratio = EBIT / Interest Expense."),
    Definition(term: "Quick Ratio", definition: "Quick Ratio = (Current Assets − Inventories) / Current Liabilities."),
    Definition(term: "Asset Turnover Ratio", definition: "Asset Turnover Ratio = Revenue / Total Assets."),
    Definition(term: "Inventory Turnover Ratio", definition: "Inventory Turnover Ratio = COGS / Average Inventory."),
    Definition(term: "RSI", definition: "Relative Strength Index = 100 − (100 / (1 + (Average Gain / Average Loss)))."),
    Definition(term: "Alpha", definition: "Alpha = Actual Return − (Risk-Free Rate + β × (Market Return − Risk-Free Rate)).")
]

#Preview {
    SettingsView()
}
