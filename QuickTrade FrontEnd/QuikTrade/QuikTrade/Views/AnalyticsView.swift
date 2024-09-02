import SwiftUI

struct AnalyticsView: View {
    @AppStorage("difficulty") var difficulty: String = "Beginner"
    
    var body: some View {
        VStack {

            Text("Select Knowledge Level")
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
            //    beginnerData()
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
struct beginnerData: View {
    var stockName:String;
    var currentPrice:Double;
    var marketCap:Double;
    var PERatio:Double;
    var dividendYield: Double;
    var high: Double;
    var low:Double;
    var volume:Double;
    var body: some View {
        return;
    }
}
struct intermediateData: View {
var stockName:String;
    var PSRatio:Double;
    var PBRatio:Double;
    var ROE: Double;
    var RevenueGrowth: Double;
    var DtoERatio: Double;
    var dividendPayoutRatio:Double;
    var beta: Double;
    var movingAverages: Double;
    var body: some View {
        beginnerData();
        return;
    
    
    }
}

struct advancedData: View {
var stockName:String;
    var enterpriseValue:Double;
    var returnOnAssets:Double;
    var interestCoverageRatio: Double;
    var quickRatio: Double;
    var assetTurnoverRatio: Double;
    var inventoryTurnoverRatio:Double;
    var relativeStrengthIndex: Double;
    var alpha: Double;
    var body: some View {
        intermediateData();
    
    
    }
}

#Preview {
    AnalyticsView()
}
