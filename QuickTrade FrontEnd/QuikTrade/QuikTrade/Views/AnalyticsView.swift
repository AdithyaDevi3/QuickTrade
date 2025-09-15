import SwiftUI

// MARK: - Model
struct LikedStock: Codable, Identifiable {
    var id: String { symbol }
    let symbol: String
    let name: String
    let logoUrl: String
    let matchPercentage: Double
}

// MARK: - ViewModel
class AnalyticsViewModel: ObservableObject {
    @Published var likedStocks: [LikedStock] = []
    @Published var selectedStock: LikedStock?
    @Published var difficulty: String = "Beginner"
    
    private let backendUrl = "http://localhost:8080/api/search"
    private var apiCallTimer: Timer?
    
    init() {
        // Optionally preload with demo data in dev mode:
        #if DEBUG
        if likedStocks.isEmpty {
            likedStocks = [
                LikedStock(symbol: "AAPL", name: "Apple Inc.", logoUrl: "", matchPercentage: 0.92),
                LikedStock(symbol: "MSFT", name: "Microsoft Corp.", logoUrl: "", matchPercentage: 0.88),
                LikedStock(symbol: "TSLA", name: "Tesla, Inc.", logoUrl: "", matchPercentage: 0.78)
            ]
        }
        #endif
    }
    
    func selectStock(_ stock: LikedStock) {
        self.selectedStock = stock
    }
    
    /// Fetch the list of liked stocks (used onAppear)
    func fetchLikedStocks() {
        guard let url = URL(string: backendUrl) else { return }
        
        URLSession.shared.dataTask(with: url) { [weak self] data, response, error in
            if let error = error {
                print("fetchLikedStocks error:", error)
                return
            }
            guard let data = data else { return }
            do {
                let result = try JSONDecoder().decode([LikedStock].self, from: data)
                DispatchQueue.main.async {
                    self?.likedStocks = result
                }
            } catch {
                print("Failed to decode liked stocks:", error)
            }
        }.resume()
        
        // throttle next fetch by 12s (optional)
        apiCallTimer?.invalidate()
        apiCallTimer = Timer.scheduledTimer(withTimeInterval: 12.0, repeats: false) { [weak self] _ in
            self?.fetchLikedStocks()
        }
    }
    
    /// Fetch details for a single stock (if your backend supports that endpoint)
    func fetchStockDetails(for stock: LikedStock) {
        let urlString = "\(backendUrl)?query=\(stock.symbol)"
        guard let url = URL(string: urlString) else { return }
        
        URLSession.shared.dataTask(with: url) { [weak self] data, response, error in
            if let error = error {
                print("fetchStockDetails error:", error)
                return
            }
            guard let data = data else { return }
            // Example: decode a single LikedStock or some detail model
            // Adjust decoding to match your backend response shape
            do {
                let fetched = try JSONDecoder().decode([LikedStock].self, from: data)
                DispatchQueue.main.async {
                    // If backend returns a list, replace likedStocks or update selectedStock as needed
                    if !fetched.isEmpty {
                        // update the list and selectedStock with the first (or find matching)
                        self?.likedStocks = fetched
                        if let found = fetched.first(where: { $0.symbol == stock.symbol }) {
                            self?.selectedStock = found
                        }
                    }
                }
            } catch {
                print("Failed to decode stock details:", error)
            }
        }.resume()
    }
}

// MARK: - Parent View
struct AnalyticsView: View {
    @StateObject private var viewModel = AnalyticsViewModel()
    
    var body: some View {
        NavigationView {
            VStack {
                Text("Liked Stocks")
                    .font(.title)
                    .padding(.top)
                
                List(viewModel.likedStocks) { stock in
                    StockRow(stock: stock, viewModel: viewModel)
                }
                .listStyle(PlainListStyle())
            }
            .padding(.horizontal)
            .onAppear {
                viewModel.fetchLikedStocks()
            }
            .navigationTitle("Analytics")
        }
    }
}

// MARK: - Row View
struct StockRow: View {
    let stock: LikedStock
    @ObservedObject var viewModel: AnalyticsViewModel
    @State private var isExpanded = false
    
    var body: some View {
        VStack(spacing: 8) {
            HStack {
                VStack(alignment: .leading) {
                    Text(stock.name)
                        .font(.headline)
                    Text(stock.symbol)
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                }
                
                Spacer()
                
                Button(action: {
                    // select in VM and toggle expansion
                    viewModel.selectStock(stock)
                    // optionally fetch details for this stock
                    viewModel.fetchStockDetails(for: stock)
                    withAnimation { isExpanded.toggle() }
                }) {
                    Image(systemName: isExpanded ? "chevron.down" : "chevron.right")
                        .foregroundColor(.blue)
                        .padding(.trailing, 8)
                }
            }
            
            if isExpanded {
                VStack(spacing: 12) {
                    Picker("Difficulty Level", selection: $viewModel.difficulty) {
                        Text("Beginner").tag("Beginner")
                        Text("Intermediate").tag("Intermediate")
                        Text("Advanced").tag("Advanced")
                    }
                    .pickerStyle(SegmentedPickerStyle())
                    
                    if let selectedStock = viewModel.selectedStock {
                        if viewModel.difficulty == "Beginner" {
                            BeginnerDataView(stock: selectedStock)
                        } else if viewModel.difficulty == "Intermediate" {
                            IntermediateDataView(stock: selectedStock)
                        } else {
                            AdvancedDataView(stock: selectedStock)
                        }
                    } else {
                        // Fallback content while details load / if not available
                        Text("No details available")
                            .foregroundColor(.secondary)
                    }
                }
                .padding()
                .background(Color.gray.opacity(0.08))
                .cornerRadius(8)
            }
        }
        .padding(.vertical, 8)
    }
}

// MARK: - Previews
#Preview {
    AnalyticsView()
}
