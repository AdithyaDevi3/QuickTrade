package quicktrade.com.entity;

import java.math.BigDecimal;

public class StockGrowthDTO {
    private String ticker;
    private BigDecimal recentClose;
    private BigDecimal yearAgoClose;
    private BigDecimal percentageChange;

    // Constructors
    public StockGrowthDTO(String ticker, BigDecimal recentClose, BigDecimal yearAgoClose, BigDecimal percentageChange) {
        this.ticker = ticker;
        this.recentClose = recentClose;
        this.yearAgoClose = yearAgoClose;
        this.percentageChange = percentageChange;
    }

    // Getters and Setters
    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public BigDecimal getRecentClose() {
        return recentClose;
    }

    public void setRecentClose(BigDecimal recentClose) {
        this.recentClose = recentClose;
    }

    public BigDecimal getYearAgoClose() {
        return yearAgoClose;
    }

    public void setYearAgoClose(BigDecimal yearAgoClose) {
        this.yearAgoClose = yearAgoClose;
    }

    public BigDecimal getPercentageChange() {
        return percentageChange;
    }

    public void setPercentageChange(BigDecimal percentageChange) {
        this.percentageChange = percentageChange;
    }
}
