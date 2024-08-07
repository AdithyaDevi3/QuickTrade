package com.quicktrade.stockMarketApiService.apiResponse.stockDataJson;//package com.quicktrade.entity;
import com.fasterxml.jackson.annotation.JsonProperty;

public class StockData {
    @JsonProperty("T")
    private String ticker;
    @JsonProperty("v")
    private int volume;
    @JsonProperty("vw")
    private double volumeWeighted;
    @JsonProperty("o")
    private double open;
    @JsonProperty("c")
    private double close;
    @JsonProperty("h")
    private double high;
    @JsonProperty("l")
    private double low;
    @JsonProperty("t")
    private long timestamp;
    @JsonProperty("n")
    private int transactions;

    // Getters and Setters
    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public double getVolumeWeighted() {
        return volumeWeighted;
    }

    public void setVolumeWeighted(double volumeWeighted) {
        this.volumeWeighted = volumeWeighted;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getTransactions() {
        return transactions;
    }

    public void setTransactions(int transactions) {
        this.transactions = transactions;
    }
}
