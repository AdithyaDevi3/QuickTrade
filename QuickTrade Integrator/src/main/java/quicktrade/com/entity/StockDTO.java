package quicktrade.com.entity;


import java.math.BigDecimal;

public class StockDTO {
    private String ticker;
    private BigDecimal recentClose;
    private Long volume;

    // Constructors
    public StockDTO(String ticker, BigDecimal recentClose, Long volume) {
        this.ticker = ticker;
        this.recentClose = recentClose;
        this.volume = volume;
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

    public Long getVolume() {
        return volume;
    }

    public void setVolume(Long volume) {
        this.volume = volume;
    }
}

