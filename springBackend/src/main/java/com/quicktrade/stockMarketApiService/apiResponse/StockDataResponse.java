package com.quicktrade.stockMarketApiService.apiResponse;

import java.util.List;

public class StockDataResponse {
    private int queryCount;
    private int resultsCount;
    private boolean adjusted;
    private List<StockData> results;

    public List<StockData> getResults() {
        return results;
    }

    public void setResults(List<StockData> results) {
        this.results = results;
    }
}