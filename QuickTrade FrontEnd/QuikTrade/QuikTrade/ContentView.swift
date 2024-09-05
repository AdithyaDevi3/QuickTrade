//
//  ContentView.swift
//  QuikTrade
//
//  Created by Adithya Devi on 8/7/24.
//

import SwiftUI

struct ContentView: View {
    @AppStorage("difficulty") var difficulty: String = "Beginner"
    
    var body: some View {
        VStack {
           
            
            
            TabView {
                HomeView()
                    .tabItem() {
                        Image(systemName: "house.fill")
                        Text("Home")
                    }
                SearchView()
                    .tabItem() {
                        Image(systemName: "magnifyingglass")
                        Text("Search")
                    }
                AnalyticsView()
                    .tabItem() {
                        Image(systemName: "chart.line.uptrend.xyaxis")
                        Text("Analytics")
                    }
                SettingsView()
                    .tabItem {
                        Image(systemName: "book.fill")
                        Text("Definitions")
                    }

            }
        }
    }
}
#Preview {
    ContentView()
}
