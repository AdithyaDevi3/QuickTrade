//
//  AnalyticsView.swift
//  QuikTrade
//
//  Created by Adithya Devi on 8/7/24.
//

import SwiftUI

struct AnalyticsView: View {
    var body: some View {
        ZStack{
            Color.purple
            Image(systemName: "chart.line.uptrend.xyaxis")
                .foregroundColor(Color.white)
                .font(.system(size:100.0))
        }
    }
}

#Preview {
    AnalyticsView()
}
