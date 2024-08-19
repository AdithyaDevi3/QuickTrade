import SwiftUI

struct HomeView: View {
    
    let topMovers = [
        (name: "Stock A", todayPrice: 120.0, yesterdayPrice: 100.0),
        (name: "Stock C", todayPrice: 150.0, yesterdayPrice: 150.0)
    ]
    
    let topLosers = [
        (name: "Stock B", todayPrice: 90.0, yesterdayPrice: 95.0)
    ]
    
    let biggest52WeekGrowth = [
        (name: "Stock D", todayPrice: 200.0, fiftyTwoWeekLow: 100.0)
    ]
    
    let biggest52WeekDrop = [
        (name: "Stock E", todayPrice: 50.0, fiftyTwoWeekHigh: 150.0)
    ]
    
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 20) {
                
                SectionView(title: "Top Movers", stocks: topMovers) { stock in
                    let percentGrowth = Double(stock.todayPrice - stock.yesterdayPrice!) / Double(stock.yesterdayPrice!) * 100
                    return StockRowView(stockName: stock.name, todayPrice: stock.todayPrice, percentChange: percentGrowth)
                }
                
                SectionView(title: "Top Losers", stocks: topLosers) { stock in
                    let percentGrowth = Double(stock.todayPrice - stock.yesterdayPrice!) / Double(stock.yesterdayPrice!) * 100
                    return StockRowView(stockName: stock.name, todayPrice: stock.todayPrice, percentChange: percentGrowth)
                }
                
                SectionView(title: "Biggest 52-Week Growth", stocks: biggest52WeekGrowth.map { ($0.name, $0.todayPrice, Optional($0.fiftyTwoWeekLow)) }) { stock in
                    let percentGrowth = Double(stock.todayPrice - stock.yesterdayPrice!) / Double(stock.yesterdayPrice!) * 100
                    return StockRowView(stockName: stock.name, todayPrice: stock.todayPrice, percentChange: percentGrowth)
                }
                
                SectionView(title: "Biggest 52-Week Drop", stocks: biggest52WeekDrop.map { ($0.name, $0.todayPrice, Optional($0.fiftyTwoWeekHigh)) }) { stock in
                    let percentDrop = Double(stock.yesterdayPrice! - stock.todayPrice) / Double(stock.yesterdayPrice!) * 100
                    return StockRowView(stockName: stock.name, todayPrice: stock.todayPrice, percentChange: -percentDrop)
                }
            }
            .navigationTitle("Stock Overview")
            .padding()
        }
    }
    
    struct SectionView<Content: View>: View {
        let title: String
        let stocks: [(name: String, todayPrice: Double, yesterdayPrice: Double?)]
        let content: ((name: String, todayPrice: Double, yesterdayPrice: Double?)) -> Content
        
        var body: some View {
            VStack(alignment: .leading, spacing: 10) {
                Text(title)
                    .font(.title2)
                    .padding(.bottom, 5)
                
                ForEach(stocks, id: \.name) { stock in
                    content(stock)
                }
            }
        }
    }
    
    struct StockRowView: View {
        let stockName: String
        let todayPrice: Double
        let percentChange: Double
        
        var body: some View {
            HStack {
                Text(stockName)
                    .font(.headline)
                Spacer()
                Text("$\(todayPrice, specifier: "%.2f")")
                Spacer()
                HStack {
                    if percentChange > 0 {
                        Image(systemName: "arrow.up.right.circle.fill")
                            .foregroundColor(.green)
                    } else if percentChange < 0 {
                        Image(systemName: "arrow.down.right.circle.fill")
                            .foregroundColor(.red)
                    } else {
                        Image(systemName: "minus.circle.fill")
                            .foregroundColor(.gray)
                    }
                    Text(String(format: "%.2f%%", percentChange))
                }
            }
        }
    }
}


#Preview {
    HomeView()
}
