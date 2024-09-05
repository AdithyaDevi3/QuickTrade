import SwiftUI

// Define the structure for liked stocks
struct LikedStock: Codable, Identifiable {
    var id: String { symbol }
    let symbol: String
    let name: String
    let logoUrl: String
    let matchPercentage: Double
}

// ViewModel class handling the liked stocks and API calls
class AnalyticsViewModel: ObservableObject {
    @Published var likedStocks: [LikedStock] = []
    @Published var selectedStock: LikedStock?
    @Published var difficulty: String = "Beginner"
    
    private let backendUrl = "http://localhost:8080/api/search"
    private var apiCallTimer: Timer?
    
    init() {
        // Fetch liked stocks from persistent storage or any other source
        // For example: UserDefaults, CoreData, etc.
    }
    
    func selectStock(_ stock: LikedStock) {
        self.selectedStock = stock
    }
    
    func fetchStockDetails() {
        guard let selectedStock = selectedStock else { return }
        
        let urlString = "\(backendUrl)?query=\(selectedStock.symbol)"
        
        guard let url = URL(string: urlString) else { return }
        
        URLSession.shared.dataTask(with: url) { data, response, error in
            if let error = error {
                print("Error: \(error)")
                return
            }
            
            guard let data = data else { return }
            
            do {
                let searchResult = try JSONDecoder().decode([LikedStock].self, from: data)
                DispatchQueue.main.async {
                    self.likedStocks = searchResult
                }
            } catch {
                print("Failed to decode JSON: \(error)")
            }
        }.resume()
        
        // Manage API call limitation (every 12 seconds)
        apiCallTimer?.invalidate()
        apiCallTimer = Timer.scheduledTimer(withTimeInterval: 12.0, repeats: false) { _ in
            self.fetchStockDetails()
        }
    }
}

struct AnalyticsView: View {
    @StateObject private var viewModel = AnalyticsViewModel()
    
    var body: some View {
        VStack {
            Text("Liked Stocks")
                .font(.title)
                .padding()
            
            List(viewModel.likedStocks) { stock in
                StockRow(stock: stock, viewModel: viewModel)
            }
            .listStyle(PlainListStyle())
        }
        .padding()
        .onAppear {
            viewModel.fetchStockDetails()
        }
    }
}

struct StockRow: View {
    let stock: LikedStock
    @StateObject var viewModel: AnalyticsViewModel
    @State private var isExpanded = false
    
    var body: some View {
        VStack {
            HStack {
                Text(stock.name)
                    .font(.headline)
                
                Spacer()
                
                Button(action: {
                    viewModel.selectStock(stock)
                    isExpanded.toggle()
                }) {
                    Image(systemName: isExpanded ? "chevron.down" : "chevron.right")
                }
                .padding(.trailing)
            }
            
            if isExpanded {
                VStack {
                    Picker("Difficulty Level", selection: $viewModel.difficulty) {
                        Text("Beginner").tag("Beginner")
                        Text("Intermediate").tag("Intermediate")
                        Text("Advanced").tag("Advanced")
                    }
                    .pickerStyle(SegmentedPickerStyle())
                    .padding()
                    
                    if let selectedStock = viewModel.selectedStock {
                        if viewModel.difficulty == "Beginner" {
                            BeginnerDataView(stock: selectedStock)
                        } else if viewModel.difficulty == "Intermediate" {
                            IntermediateDataView(stock: selectedStock)
                        } else if viewModel.difficulty == "Advanced" {
                            AdvancedDataView(stock: selectedStock)
                        }
                    }
                }
                .padding()
                .background(Color.gray.opacity(0.1))
                .cornerRadius(8)
            }
        }
        .padding()
    }
}

#Preview {
    AnalyticsView()
}
