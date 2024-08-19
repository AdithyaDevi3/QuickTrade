import SwiftUI
import Combine

// Define the structure for the match results including the new logoUrl and matchPercentage
struct Match: Codable, Identifiable {
    var id: String { symbol }
    let symbol: String
    let name: String
    let logoUrl: String
    let matchPercentage: Double
}

// ViewModel class handling the search functionality
class SearchViewModel: ObservableObject {
    @Published var query: String = ""
    @Published var results: [Match] = []
    
    private let backendUrl = "http://localhost:8080/api/search"

    func searchCompany() {
        guard !query.isEmpty else { return }
        let urlString = "\(backendUrl)?query=\(query)"
        
        guard let url = URL(string: urlString) else { return }
        
        URLSession.shared.dataTask(with: url) { data, response, error in
            if let error = error {
                print("Error: \(error)")
                return
            }
            
            guard let data = data else { return }
            
            do {
                let searchResult = try JSONDecoder().decode([Match].self, from: data)
                DispatchQueue.main.async {
                    self.results = searchResult
                }
            } catch {
                print("Failed to decode JSON: \(error)")
            }
        }.resume()
    }
}

// The SearchView UI structure
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
                HStack {
                    // Display the company logo from logoUrl
                    if let url = URL(string: match.logoUrl) {
                        AsyncImage(url: url) { image in
                            image
                                .resizable()
                                .aspectRatio(contentMode: .fit)
                                .frame(width: 50, height: 50)
                        } placeholder: {
                            ProgressView()
                        }
                    }
                    
                    VStack(alignment: .leading) {
                        Text(match.name)
                            .font(.headline)
                        Text(match.symbol)
                            .font(.subheadline)
                        Text("Match Percentage: \(match.matchPercentage, specifier: "%.2f")%")
                            .font(.subheadline)
                    }
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
