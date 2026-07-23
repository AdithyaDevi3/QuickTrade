package quicktrade.com.entity;

import java.time.LocalDateTime;

/**
 * PredictionDetailDTO — full indicator breakdown for a single ticker used by
 * the iOS PredictionDetailView.
 *
 * <p>Extends {@link PredictionSummaryDTO} data with Bollinger Band values,
 * SMA-20/50, MACD histogram, and a confidence narrative.</p>
 */
public class PredictionDetailDTO {

    private String ticker;
    private String name;
    private String logoUrl;
    private String signal;
    private double score;
    private String type;

    // ── Indicators ────────────────────────────────────────────────────────────
    private Double rsi;
    private Double macd;
    private Double macdSignal;
    private Double macdHistogram;
    private Double sma20;
    private Double sma50;
    private Double bollingerUpper;
    private Double bollingerLower;
    private Integer dataPointsUsed;
    private LocalDateTime computedAt;

    public PredictionDetailDTO() {}

    // ── Getters / Setters ─────────────────────────────────────────────────────

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

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Double getRsi() { return rsi; }
    public void setRsi(Double rsi) { this.rsi = rsi; }

    public Double getMacd() { return macd; }
    public void setMacd(Double macd) { this.macd = macd; }

    public Double getMacdSignal() { return macdSignal; }
    public void setMacdSignal(Double macdSignal) { this.macdSignal = macdSignal; }

    public Double getMacdHistogram() { return macdHistogram; }
    public void setMacdHistogram(Double macdHistogram) { this.macdHistogram = macdHistogram; }

    public Double getSma20() { return sma20; }
    public void setSma20(Double sma20) { this.sma20 = sma20; }

    public Double getSma50() { return sma50; }
    public void setSma50(Double sma50) { this.sma50 = sma50; }

    public Double getBollingerUpper() { return bollingerUpper; }
    public void setBollingerUpper(Double bollingerUpper) { this.bollingerUpper = bollingerUpper; }

    public Double getBollingerLower() { return bollingerLower; }
    public void setBollingerLower(Double bollingerLower) { this.bollingerLower = bollingerLower; }

    public Integer getDataPointsUsed() { return dataPointsUsed; }
    public void setDataPointsUsed(Integer dataPointsUsed) { this.dataPointsUsed = dataPointsUsed; }

    public LocalDateTime getComputedAt() { return computedAt; }
    public void setComputedAt(LocalDateTime computedAt) { this.computedAt = computedAt; }
}
