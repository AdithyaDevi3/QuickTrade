package com.quicktrade.service;

import com.quicktrade.entity.PredictionResult;
import com.quicktrade.entity.Stocks;
import com.quicktrade.entity.WatchlistEntry;
import com.quicktrade.repository.PredictionResultRepository;
import com.quicktrade.repository.StockRepository;
import com.quicktrade.repository.WatchlistRepository;
import com.quicktrade.service.TechnicalIndicatorService.BollingerResult;
import com.quicktrade.service.TechnicalIndicatorService.MACDResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * PredictionService — orchestrates the technical-analysis prediction pipeline
 * for every ticker in the watchlist.
 *
 * <h2>Algorithm</h2>
 * <p>For each ticker the service retrieves the most-recent 200 trading-day
 * records from the database and passes them through four indicators.  The
 * individual sub-scores are combined into a single composite prediction score
 * using fixed weights:</p>
 *
 * <table>
 *   <tr><th>Indicator</th>   <th>Weight</th></tr>
 *   <tr><td>RSI</td>          <td>30 %</td></tr>
 *   <tr><td>MACD crossover</td><td>25 %</td></tr>
 *   <tr><td>SMA trend</td>    <td>25 %</td></tr>
 *   <tr><td>Bollinger position</td><td>20 %</td></tr>
 * </table>
 *
 * <p>The final score maps to a signal: {@code BULLISH} (>65), {@code BEARISH}
 * (<35), or {@code NEUTRAL} (35–65).</p>
 *
 * <p>Results are persisted to the {@code prediction_results} table.  Each
 * call to {@link #computePrediction(String)} inserts a new row; the REST layer
 * always fetches the latest row per ticker.</p>
 *
 * <h2>Scheduling</h2>
 * <p>The full watchlist refresh runs automatically at 06:45 AM on every
 * weekday so predictions are ready before the US market open.</p>
 */
@Service
public class PredictionService {

    private static final Logger log = LoggerFactory.getLogger(PredictionService.class);

    private static final int HISTORY_DAYS = 200;
    private static final int RSI_PERIOD = 14;
    private static final int BOLLINGER_PERIOD = 20;
    private static final double BOLLINGER_STD_MULT = 2.0;

    // Composite-score weights (must sum to 1.0)
    private static final double WEIGHT_RSI = 0.30;
    private static final double WEIGHT_MACD = 0.25;
    private static final double WEIGHT_SMA = 0.25;
    private static final double WEIGHT_BOLLINGER = 0.20;

    @Autowired private StockRepository stockRepository;
    @Autowired private WatchlistRepository watchlistRepository;
    @Autowired private PredictionResultRepository predictionResultRepository;
    @Autowired private TechnicalIndicatorService indicators;

    // ── Scheduled entry-point ─────────────────────────────────────────────────

    /**
     * Refreshes predictions for all default watchlist tickers.
     * Runs weekdays at 06:45 AM after {@link com.quicktrade.stockMarketApiService.ApiCall}
     * has already reloaded today's stock-name data at 06:00 AM.
     */
    @Scheduled(cron = "0 45 6 * * MON-FRI")
    public void refreshAllPredictions() {
        List<WatchlistEntry> watchlist = watchlistRepository.findByIsDefault(true);
        log.info("Starting prediction refresh for {} tickers.", watchlist.size());
        int computed = 0;
        for (WatchlistEntry entry : watchlist) {
            try {
                computePrediction(entry.getTicker());
                computed++;
            } catch (Exception ex) {
                log.warn("Prediction failed for {}: {}", entry.getTicker(), ex.getMessage());
            }
        }
        log.info("Prediction refresh complete: {}/{} succeeded.", computed, watchlist.size());
    }

    // ── Core computation ──────────────────────────────────────────────────────

    /**
     * Computes and persists a {@link PredictionResult} for the given ticker.
     *
     * <p>Fetches the most-recent {@value #HISTORY_DAYS} trading-day records,
     * calculates each indicator, derives sub-scores, and stores the weighted
     * composite result.  If the ticker has fewer than 15 data points the method
     * returns an empty Optional and logs a warning instead of persisting partial
     * data.</p>
     *
     * @param ticker stock or ETF ticker symbol
     * @return the persisted {@link PredictionResult}, or empty if data is insufficient
     */
    public Optional<PredictionResult> computePrediction(String ticker) {
        List<Stocks> history = stockRepository.findRecentByTicker(ticker, HISTORY_DAYS);

        if (history.size() < RSI_PERIOD + 1) {
            log.debug("Insufficient data for {} ({} records). Skipping.", ticker, history.size());
            return Optional.empty();
        }

        PredictionResult result = new PredictionResult();
        result.setTicker(ticker);
        result.setDataPointsUsed(history.size());

        // ── 1. RSI ────────────────────────────────────────────────────────────
        Double rsi = indicators.calculateRSI(history, RSI_PERIOD);
        result.setRsi(rsi);
        double rsiScore = (rsi != null) ? rsiSubScore(rsi) : 50.0;

        // ── 2. MACD ───────────────────────────────────────────────────────────
        MACDResult macdResult = indicators.calculateMACD(history);
        double macdScore = 50.0;
        if (macdResult != null) {
            result.setMacd(macdResult.macd());
            result.setMacdSignal(macdResult.signal());
            result.setMacdHistogram(macdResult.histogram());
            macdScore = macdCrossoverScore(macdResult);
        }

        // ── 3. SMA trend ──────────────────────────────────────────────────────
        Double sma20 = indicators.calculateSMA(history, 20);
        Double sma50 = indicators.calculateSMA(history, 50);
        result.setSma20(sma20);
        result.setSma50(sma50);
        double smaScore = (sma20 != null && sma50 != null) ? smaTrendScore(sma20, sma50) : 50.0;

        // ── 4. Bollinger Band position ────────────────────────────────────────
        BollingerResult bollinger = indicators.calculateBollingerBands(history, BOLLINGER_PERIOD, BOLLINGER_STD_MULT);
        double bollingerScore = 50.0;
        if (bollinger != null) {
            result.setBollingerUpper(bollinger.upper());
            result.setBollingerLower(bollinger.lower());
            double currentClose = history.get(history.size() - 1).getClose();
            bollingerScore = bollingerPositionScore(currentClose, bollinger);
        }

        // ── Composite score ───────────────────────────────────────────────────
        double composite = rsiScore * WEIGHT_RSI
                + macdScore * WEIGHT_MACD
                + smaScore * WEIGHT_SMA
                + bollingerScore * WEIGHT_BOLLINGER;

        result.setPredictionScore(composite);
        result.setSignal(toSignal(composite));

        return Optional.of(predictionResultRepository.save(result));
    }

    // ── Sub-score helpers ─────────────────────────────────────────────────────

    /**
     * Maps RSI to a 0–100 sub-score.
     * RSI below 30 is oversold → bullish (high score).
     * RSI above 70 is overbought → bearish (low score).
     *
     * @param rsi RSI value in [0, 100]
     * @return sub-score in [0, 100]
     */
    private static double rsiSubScore(double rsi) {
        // Invert: oversold = bullish signal
        if (rsi <= 30) return 85.0 + (30.0 - rsi);       // 85–115 capped to 100
        if (rsi >= 70) return Math.max(0, 100.0 - rsi);   // 0–30
        // Linear interpolation in neutral zone
        return 50.0 + (50.0 - rsi);                        // 0–100 around 50
    }

    /**
     * Maps MACD crossover state to a 0–100 sub-score.
     * A positive histogram (MACD above signal) is bullish; negative is bearish.
     *
     * @param r MACD result
     * @return sub-score in [0, 100]
     */
    private static double macdCrossoverScore(MACDResult r) {
        // Normalise histogram relative to signal magnitude
        if (r.signal() == 0) return 50.0;
        double ratio = r.histogram() / Math.abs(r.signal());
        // Clamp ratio to [-1, +1] and map to [0, 100]
        ratio = Math.max(-1.0, Math.min(1.0, ratio));
        return 50.0 + ratio * 50.0;
    }

    /**
     * Maps SMA-20/SMA-50 crossover state to a 0–100 sub-score.
     * SMA-20 above SMA-50 is a bullish golden-cross signal.
     *
     * @param sma20 20-day SMA
     * @param sma50 50-day SMA
     * @return 75.0 if golden-cross, 25.0 if death-cross, 50.0 if equal
     */
    private static double smaTrendScore(double sma20, double sma50) {
        if (sma20 > sma50) return 75.0;
        if (sma20 < sma50) return 25.0;
        return 50.0;
    }

    /**
     * Maps the current price position within the Bollinger Band to a sub-score.
     * Price near the lower band is oversold → bullish.
     * Price near the upper band is overbought → bearish.
     *
     * @param close     most-recent closing price
     * @param bollinger Bollinger Band values
     * @return sub-score in [0, 100]
     */
    private static double bollingerPositionScore(double close, BollingerResult bollinger) {
        double range = bollinger.upper() - bollinger.lower();
        if (range == 0) return 50.0;
        // position: 0 = at lower band, 1 = at upper band
        double position = (close - bollinger.lower()) / range;
        position = Math.max(0.0, Math.min(1.0, position));
        // Invert: low position is bullish
        return (1.0 - position) * 100.0;
    }

    /**
     * Converts a composite score to a signal string.
     *
     * @param score composite score in [0, 100]
     * @return "BULLISH", "BEARISH", or "NEUTRAL"
     */
    private static String toSignal(double score) {
        if (score > 65) return "BULLISH";
        if (score < 35) return "BEARISH";
        return "NEUTRAL";
    }
}
