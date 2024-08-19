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
            //                    // The content or functionality here would adjust based on the difficulty level
            //                    if difficulty == "Beginner" {
            //                        // Load or display beginner-level data
            //                        BeginnerContentView()
            //                    } else if difficulty == "Intermediate" {
            //                        // Load or display intermediate-level data
            //                        IntermediateContentView()
            //                    } else if difficulty == "Advanced" {
            //                        // Load or display advanced-level data
            //                        AdvancedContentView()
            //                    }
            
            
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
                    .tabItem() {
                        Image(systemName: "gear")
                        Text("Settings")
                    }
                
            }
        }
    }
}
#Preview {
    ContentView()
}
