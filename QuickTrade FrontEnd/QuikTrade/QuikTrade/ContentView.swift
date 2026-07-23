// ContentView.swift — root tab bar for the QuikTrade app.
//
// Five tabs:
//   1. Home       — live market movers + predictions teaser
//   2. Search     — fuzzy stock search → StockDetailView
//   3. Analytics  — expandable rows with live metrics + mini charts
//   4. Predict    — algorithmic signals for top-50 stocks + ETFs (NEW)
//   5. Learn      — financial glossary + quiz mode

import SwiftUI

struct ContentView: View {
    @AppStorage("difficulty") var difficulty: String = "Beginner"

    var body: some View {
        TabView {
            HomeView()
                .tabItem {
                    Image(systemName: "house.fill")
                    Text("Home")
                }

            SearchView()
                .tabItem {
                    Image(systemName: "magnifyingglass")
                    Text("Search")
                }

            AnalyticsView()
                .tabItem {
                    Image(systemName: "chart.line.uptrend.xyaxis")
                    Text("Analytics")
                }

            PredictionView()
                .tabItem {
                    Image(systemName: "wand.and.stars")
                    Text("Predict")
                }

            SettingsView()
                .tabItem {
                    Image(systemName: "book.fill")
                    Text("Learn")
                }
        }
    }
}

#Preview {
    ContentView()
}

}
