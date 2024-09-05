import SwiftUI

struct BeginnerDataView: View {
    var stock: LikedStock
    
    var body: some View {
        VStack(alignment: .leading) {
            Text("Stock: \(stock.name)")
            Text("Current Price: Fetch Data...")
            Text("Market Cap: Fetch Data...")
            Text("P/E Ratio: Fetch Data...")
            Text("Dividend Yield: Fetch Data...")
            Text("52-Week High: Fetch Data...")
            Text("52-Week Low: Fetch Data...")
            Text("Volume: Fetch Data...")
        }
    }
}

struct IntermediateDataView: View {
    var stock: LikedStock
    
    var body: some View {
        VStack(alignment: .leading) {
            Text("P/S Ratio: Fetch Data...")
            Text("P/B Ratio: Fetch Data...")
            Text("ROE: Fetch Data...")
            Text("Revenue Growth: Fetch Data...")
            Text("Debt-to-Equity Ratio: Fetch Data...")
            Text("Dividend Payout Ratio: Fetch Data...")
            Text("Beta: Fetch Data...")
            Text("Moving Averages: Fetch Data...")
        }
    }
}

struct AdvancedDataView: View {
    var stock: LikedStock
    
    var body: some View {
        VStack(alignment: .leading) {
            Text("Enterprise Value: Fetch Data...")
            Text("Return on Assets: Fetch Data...")
            Text("Interest Coverage Ratio: Fetch Data...")
            Text("Quick Ratio: Fetch Data...")
            Text("Asset Turnover Ratio: Fetch Data...")
            Text("Inventory Turnover Ratio: Fetch Data...")
            Text("Relative Strength Index: Fetch Data...")
            Text("Alpha: Fetch Data...")
        }
    }
}
