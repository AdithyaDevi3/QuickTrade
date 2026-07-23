package quicktrade.com.entity;

import java.time.LocalDateTime;

/**
 * PredictionSummaryDTO — lightweight prediction summary returned in the
 * iOS Predictions tab list view.
 *
 * <p>Contains the key signal and score together with raw RSI and MACD values
 * so the list can display mini-indicators without a second API call.</p>
 */
public class PredictionSummaryDTO {

    private String ticker;
    private String name;
    private String logoUrl;
    /** "BULLISH", "BEARISH", or "NEUTRAL". */
    private String signal;
    /** Composite weighted score, 0 (most bearish) – 100 (most bullish). */
    private double score;
    private Double rsi;
    private Double macd;
    private Double macdSignal;
    private String type;          // "STOCK" or "ETF"
    private LocalDateTime computedAt;

    public PredictionSummaryDTO() {}

    public String getTicker() { return ticker; }
    public void setTicker(String ticker) { this.ticker = ticker; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public String getSignal() { return signal; }
    public void setSignal(String signal) { this.signal = signal; }

    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }

    public Double getRsi() { return rsi; }
    public void setRsi(Double rsi) { this.rsi = rsi; }

    public Double getMacd() { return macd; }
    public void setMacd(Double macd) { this.macd = macd; }

    public Double getMacdSignal() { return macdSignal; }
    public void setMacdSignal(Double macdSignal) { this.macdSignal = macdSignal; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public LocalDateTime getComputedAt() { return computedAt; }
    public void setComputedAt(LocalDateTime computedAt) { this.computedAt = computedAt; }
}
