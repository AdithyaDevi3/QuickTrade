package quicktrade.com.entity;

/**
 * StockMetricsDTO — snapshot of the key metrics for a single ticker used by
 * the iOS StockDetailView metrics tabs (Beginner / Intermediate / Advanced).
 *
 * <p>Values are sourced from the most-recent record in {@code stocks_data} plus
 * the 52-week high/low computed over the rolling year window.</p>
 */
public class StockMetricsDTO {

    private String ticker;
    private double currentPrice;
    private double open;
    private double high;
    private double low;
    private long volume;
    private double week52High;
    private double week52Low;
    /** Absolute change from previous close (recentClose − prevClose). */
    private double changeAmount;
    /** Percentage change from previous close. */
    private double changePercent;

    public StockMetricsDTO() {}

    public StockMetricsDTO(String ticker, double currentPrice, double open,
                           double high, double low, long volume,
                           double week52High, double week52Low,
                           double changeAmount, double changePercent) {
        this.ticker = ticker;
        this.currentPrice = currentPrice;
        this.open = open;
        this.high = high;
        this.low = low;
        this.volume = volume;
        this.week52High = week52High;
        this.week52Low = week52Low;
        this.changeAmount = changeAmount;
        this.changePercent = changePercent;
    }

    public String getTicker() { return ticker; }
    public void setTicker(String ticker) { this.ticker = ticker; }

    public double getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(double currentPrice) { this.currentPrice = currentPrice; }

    public double getOpen() { return open; }
    public void setOpen(double open) { this.open = open; }

    public double getHigh() { return high; }
    public void setHigh(double high) { this.high = high; }

    public double getLow() { return low; }
    public void setLow(double low) { this.low = low; }

    public long getVolume() { return volume; }
    public void setVolume(long volume) { this.volume = volume; }

    public double getWeek52High() { return week52High; }
    public void setWeek52High(double week52High) { this.week52High = week52High; }

    public double getWeek52Low() { return week52Low; }
    public void setWeek52Low(double week52Low) { this.week52Low = week52Low; }

    public double getChangeAmount() { return changeAmount; }
    public void setChangeAmount(double changeAmount) { this.changeAmount = changeAmount; }

    public double getChangePercent() { return changePercent; }
    public void setChangePercent(double changePercent) { this.changePercent = changePercent; }
}
