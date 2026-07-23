// SearchView.swift — stock / ETF search with live API results.
//
// Tapping a result opens StockDetailView for the full metrics, chart, and
// prediction breakdown.  The existing `Match` struct is kept as a typealias
// so legacy code continues to compile.

import SwiftUI
import Combine

// MARK: - Legacy alias

/// Backward-compatibility typealias — new code should use `StockMatch` directly.
typealias Match = StockMatch

// MARK: - ViewModel

/// Manages the search text field and debounces API calls to avoid flooding
/// the backend on every keystroke.
@MainActor
final class SearchViewModel: ObservableObject {
    @Published var query = ""
    @Published var results: [StockMatch] = []
    @Published var isSearching = false

    private var searchTask: Task<Void, Never>?

    /// Fires the search after a 400 ms debounce.
    func searchCompany() {
        searchTask?.cancel()
        guard !query.trimmingCharacters(in: .whitespaces).isEmpty else {
            results = []
            return
        }
        searchTask = Task {
            try? await Task.sleep(nanoseconds: 400_000_000)
            guard !Task.isCancelled else { return }
            isSearching = true
            defer { isSearching = false }
            results = (try? await APIService.shared.searchStocks(query: query)) ?? []
        }
    }
}

// MARK: - View

/// Stock search tab — type a ticker or company name, get results, tap for details.
struct SearchView: View {
    @StateObject private var viewModel = SearchViewModel()

    var body: some View {
        NavigationView {
            VStack(spacing: 0) {

                // ── Search bar ─────────────────────────────────────────────
                HStack {
                    TextField("Enter company name or ticker", text: $viewModel.query)
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                        .onChange(of: viewModel.query) { _ in
                            viewModel.searchCompany()
                        }

                    if viewModel.isSearching {
                        ProgressView().padding(.leading, 4)
                    } else {
                        Button(action: { viewModel.searchCompany() }) {
                            Text("Search")
                                .foregroundColor(.white)
                                .padding(.horizontal, 14)
                                .padding(.vertical, 8)
                                .background(Color.black)
                                .cornerRadius(8)
                        }
                    }
                }
                .padding()

                // ── Results ────────────────────────────────────────────────
                List(viewModel.results) { match in
                    NavigationLink(destination: StockDetailView(match: match)) {
                        HStack(spacing: 12) {
                            AsyncImage(url: URL(string: match.logoUrl)) { image in
                                image.resizable().scaledToFit()
                            } placeholder: {
                                Image(systemName: "chart.line.uptrend.xyaxis")
                                    .foregroundColor(.gray)
                            }
                            .frame(width: 44, height: 44)
                            .clipShape(RoundedRectangle(cornerRadius: 8))

                            VStack(alignment: .leading, spacing: 2) {
                                Text(match.symbol).font(.headline)
                                Text(match.name).font(.subheadline).foregroundColor(.secondary)
                                Text(String(format: "Match: %.0f%%", match.matchPercentage))
                                    .font(.caption).foregroundColor(.secondary)
                            }
                        }
                    }
                }
                .listStyle(PlainListStyle())
            }
            .navigationBarTitle("Stock Search", displayMode: .inline)
        }
    }
}

#Preview {
    SearchView()
}
