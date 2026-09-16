package quicktrade.com.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * PredictionResult — maps to the {@code prediction_results} table, computed and
 * persisted by this service's own {@code PredictionService} on a daily schedule.
 */
@Entity
@Table(name = "prediction_results")
public class PredictionResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticker")
    private String ticker;

    @Column(name = "rsi")
    private Double rsi;

    @Column(name = "macd")
    private Double macd;

    @Column(name = "macd_signal")
    private Double macdSignal;

    @Column(name = "macd_histogram")
    private Double macdHistogram;

    @Column(name = "sma_20")
    private Double sma20;

    @Column(name = "sma_50")
    private Double sma50;

    @Column(name = "bollinger_upper")
    private Double bollingerUpper;

    @Column(name = "bollinger_lower")
    private Double bollingerLower;

    @Column(name = "prediction_score")
    private Double predictionScore;

    @Column(name = "signal")
    private String signal;

    @Column(name = "data_points_used")
    private Integer dataPointsUsed;

    @Column(name = "computed_at")
    private LocalDateTime computedAt;

    // ── Getters ───────────────────────────────────────────────────────────────

    public Long getId() { return id; }
    public String getTicker() { return ticker; }
    public Double getRsi() { return rsi; }
    public Double getMacd() { return macd; }
    public Double getMacdSignal() { return macdSignal; }
    public Double getMacdHistogram() { return macdHistogram; }
    public Double getSma20() { return sma20; }
    public Double getSma50() { return sma50; }
    public Double getBollingerUpper() { return bollingerUpper; }
    public Double getBollingerLower() { return bollingerLower; }
    public Double getPredictionScore() { return predictionScore; }
    public String getSignal() { return signal; }
    public Integer getDataPointsUsed() { return dataPointsUsed; }
    public LocalDateTime getComputedAt() { return computedAt; }

    // ── Setters ───────────────────────────────────────────────────────────────

    public void setTicker(String ticker) { this.ticker = ticker; }
    public void setRsi(Double rsi) { this.rsi = rsi; }
    public void setMacd(Double macd) { this.macd = macd; }
    public void setMacdSignal(Double macdSignal) { this.macdSignal = macdSignal; }
    public void setMacdHistogram(Double macdHistogram) { this.macdHistogram = macdHistogram; }
    public void setSma20(Double sma20) { this.sma20 = sma20; }
    public void setSma50(Double sma50) { this.sma50 = sma50; }
    public void setBollingerUpper(Double bollingerUpper) { this.bollingerUpper = bollingerUpper; }
    public void setBollingerLower(Double bollingerLower) { this.bollingerLower = bollingerLower; }
    public void setPredictionScore(Double predictionScore) { this.predictionScore = predictionScore; }
    public void setSignal(String signal) { this.signal = signal; }
    public void setDataPointsUsed(Integer dataPointsUsed) { this.dataPointsUsed = dataPointsUsed; }

    @PrePersist
    private void prePersist() {
        if (computedAt == null) computedAt = LocalDateTime.now();
    }
}
