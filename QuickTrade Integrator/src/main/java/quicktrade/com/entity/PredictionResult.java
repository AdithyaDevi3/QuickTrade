package quicktrade.com.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * PredictionResult (read-only mirror) — maps to the {@code prediction_results}
 * table written by the springBackend's {@code PredictionService}.
 *
 * <p>The Integrator only reads this table; all writes happen in springBackend.</p>
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
}
