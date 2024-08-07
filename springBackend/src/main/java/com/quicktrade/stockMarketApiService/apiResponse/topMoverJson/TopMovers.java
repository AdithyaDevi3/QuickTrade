package com.quicktrade.stockMarketApiService.apiResponse.topMoverJson;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TopMovers {
    @JsonProperty("ticker")
    private String ticker;
    @JsonProperty("price")
    private double price;
    @JsonProperty("change_amount")
    private double changeAmount;
    @JsonProperty("change_percentage")
    private double changePercentage;
    @JsonProperty("volume")
    private int volume;
}
