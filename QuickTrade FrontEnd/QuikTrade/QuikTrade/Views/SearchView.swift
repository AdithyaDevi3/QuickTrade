import SwiftUI
import Combine

struct Match: Codable, Identifiable {
    var id: String { symbol }
    let symbol: String
    let name: String
    let type: String
    let region: String
    let marketOpen: String
    let marketClose: String
    let timezone: String
    let currency: String
    let matchScore: String

    enum CodingKeys: String, CodingKey {
        case symbol = "1. symbol"
        case name = "2. name"
        case type = "3. type"
        case region = "4. region"
        case marketOpen = "5. marketOpen"
        case marketClose = "6. marketClose"
        case timezone = "7. timezone"
        case currency = "8. currency"
        case matchScore = "9. matchScore"
    }
}

struct SearchResult: Codable {
    let bestMatches: [Match]?
}

class SearchViewModel: ObservableObject {
    @Published var query: String = ""
    @Published var results: [Match] = []
    
    private let apiKey = "mjuTBMGVNRgdO10k4_2tbNeNYBRUu8Vb"

    func searchCompany() {
        guard !query.isEmpty else { return }
        let urlString = "https://www.alphavantage.co/query?function=SYMBOL_SEARCH&keywords=\(query)&apikey=\(apiKey)"
        
        guard let url = URL(string: urlString) else { return }
        
        URLSession.shared.dataTask(with: url) { data, response, error in
            if let error = error {
                print("Error: \(error)")
                return
            }
            
            guard let data = data else { return }
            
            do {
                let jsonString = String(data: data, encoding: .utf8)
                print("Received JSON: \(jsonString ?? "nil")")
                
                let searchResult = try JSONDecoder().decode(SearchResult.self, from: data)
                DispatchQueue.main.async {
                    self.results = searchResult.bestMatches ?? []
                }
            } catch {
                print("Failed to decode JSON: \(error)")
            }
        }.resume()
    }
}

struct SearchView: View {
    @StateObject private var viewModel = SearchViewModel()
    
    var body: some View {
        VStack {
            HStack {
                TextField("Enter company name or ticker", text: $viewModel.query)
                    .textFieldStyle(RoundedBorderTextFieldStyle())
                    .frame(maxWidth: .infinity)
                    .padding(.horizontal,-5)
                
                Button(action: {
                    viewModel.searchCompany()
                }) {
                    Text("Search")
                        .foregroundColor(.white)
                        .padding()
                        .background(Color.black)
                        .cornerRadius(8)
                }
                
                .padding(.horizontal,4)
            }
            
            .padding(.top, -10) // Padding to push down from the top
         
            List(viewModel.results) { match in
                VStack(alignment: .leading) {
                    Text(match.name)
                        .font(.headline)
                    Text(match.symbol)
                        .font(.subheadline)
                    Text("Match Score: \(match.matchScore)")
                        .font(.subheadline)
                }
            }
            .listStyle(PlainListStyle())
        }
        .padding()
        .navigationBarTitle("Stock Search", displayMode: .inline) // Optional: Title at the top
    }
}

#Preview {
    SearchView()
}
