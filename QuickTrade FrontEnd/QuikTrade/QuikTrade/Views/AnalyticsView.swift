import SwiftUI

struct AnalyticsView: View {
    @AppStorage("difficulty") var difficulty: String = "Beginner"
    
    var body: some View {
        VStack {
            Text("Settings")
                .font(.largeTitle)
                .padding()

            Text("Select Difficulty Level")
                .font(.headline)
                .padding(.top)

            Picker("Difficulty", selection: $difficulty) {
                Text("Beginner").tag("Beginner")
                Text("Intermediate").tag("Intermediate")
                Text("Advanced").tag("Advanced")
            }
            .pickerStyle(SegmentedPickerStyle())
            .padding()

            Spacer()
        }
        .padding()
    }
}

struct Analytics: View {
    @AppStorage("difficulty") var difficulty: String = "Beginner"

    var body: some View {
        VStack {
            Text("Current Difficulty: \(difficulty)")
                .font(.title)
                .padding()
            
           
            if difficulty == "Beginner" {
                Text("Beginner Content")
            } else if difficulty == "Intermediate" {
                Text("Intermediate Content")
            } else if difficulty == "Advanced" {
                Text("Advanced Content")
            }
            
            Spacer()
        }
        .padding()
    }
}

#Preview {
    AnalyticsView()
}
