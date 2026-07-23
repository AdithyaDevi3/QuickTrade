package com.quicktrade.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * PredictionResult entity — stores the output of the technical-indicator algorithm
 * for a single ticker at a point in time.
 *
 * <p>Fields capture the raw indicator values together with the composite
 * {@code predictionScore} (0–100) and the human-readable {@code signal}.
 * New predictions overwrite conceptually by always inserting; callers query
 * {@code findTopByTickerOrderByComputedAtDesc} to get the latest reading.</p>
 */
@Entity
@Table(name = "prediction_results", indexes = {
        @Index(name = "idx_prediction_ticker_date", columnList = "ticker, computed_at")
})
public class PredictionResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Stock ticker symbol, e.g. "AAPL". */
    @Column(name = "ticker", nullable = false, length = 10)
    private String ticker;

    // ── Raw indicator values ──────────────────────────────────────────────────

    /** Relative Strength Index (14-period). Range: 0–100. */
    @Column(name = "rsi")
    private Double rsi;

    /** MACD line value (EMA-12 minus EMA-26). */
    @Column(name = "macd")
    private Double macd;

    /** MACD signal line (9-period EMA of MACD). */
    @Column(name = "macd_signal")
    private Double macdSignal;

    /** MACD histogram (macd minus signal). */
    @Column(name = "macd_histogram")
    private Double macdHistogram;

    /** 20-day simple moving average of close price. */
    @Column(name = "sma_20")
    private Double sma20;

    /** 50-day simple moving average of close price. */
    @Column(name = "sma_50")
    private Double sma50;

    /** Upper Bollinger Band (SMA-20 + 2σ). */
    @Column(name = "bollinger_upper")
    private Double bollingerUpper;

    /** Lower Bollinger Band (SMA-20 − 2σ). */
    @Column(name = "bollinger_lower")
    private Double bollingerLower;

    // ── Composite output ──────────────────────────────────────────────────────

    /**
     * Weighted composite score from 0 (most bearish) to 100 (most bullish).
     * Derived from: RSI 30%, MACD crossover 25%, SMA trend 25%, Bollinger 20%.
     */
    @Column(name = "prediction_score")
    private Double predictionScore;

    /**
     * Human-readable signal derived from {@code predictionScore}.
     * Values: "BULLISH" (>65), "BEARISH" (<35), "NEUTRAL" (35–65).
     */
    @Column(name = "signal", length = 10)
    private String signal;

    /** Number of days of historical data used in the calculation. */
    @Column(name = "data_points_used")
    private Integer dataPointsUsed;

    /** UTC timestamp when this prediction was computed. */
    @Column(name = "computed_at", nullable = false)
    private LocalDateTime computedAt;

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    @PrePersist
    void prePersist() {
        if (computedAt == null) computedAt = LocalDateTime.now();
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTicker() { return ticker; }
    public void setTicker(String ticker) { this.ticker = ticker; }

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

    public Double getPredictionScore() { return predictionScore; }
    public void setPredictionScore(Double predictionScore) { this.predictionScore = predictionScore; }

    public String getSignal() { return signal; }
    public void setSignal(String signal) { this.signal = signal; }

    public Integer getDataPointsUsed() { return dataPointsUsed; }
    public void setDataPointsUsed(Integer dataPointsUsed) { this.dataPointsUsed = dataPointsUsed; }

    public LocalDateTime getComputedAt() { return computedAt; }
    public void setComputedAt(LocalDateTime computedAt) { this.computedAt = computedAt; }
}
