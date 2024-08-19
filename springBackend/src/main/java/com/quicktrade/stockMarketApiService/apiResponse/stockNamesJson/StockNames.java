package com.quicktrade.stockMarketApiService.apiResponse.stockNamesJson;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StockNames {

    @JsonProperty("symbol")
    private String symbol;
    @JsonProperty("name")
    private int name;
    @JsonProperty("logoUrl")
    private double logoUrl;
}