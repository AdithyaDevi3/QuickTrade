package quicktrade.com.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import quicktrade.com.entity.PredictionResult;
import quicktrade.com.entity.Stocks;
import quicktrade.com.entity.WatchlistEntry;
import quicktrade.com.repository.PredictionResultRepository;
import quicktrade.com.repository.StockRepository;
import quicktrade.com.repository.WatchlistRepository;
import quicktrade.com.service.TechnicalIndicatorService.BollingerResult;
import quicktrade.com.service.TechnicalIndicatorService.MACDResult;

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
 * using fixed weights: RSI 30%, MACD crossover 25%, SMA trend 25%, Bollinger
 * position 20%. The final score maps to a signal: {@code BULLISH} (>65),
 * {@code BEARISH} (<35), or {@code NEUTRAL} (35–65).</p>
 *
 * <p>Results are persisted to the {@code prediction_results} table.  Each
 * call to {@link #computePrediction(String)} inserts a new row; the REST layer
 * always fetches the latest row per ticker via {@link PredictionClientService}.</p>
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

    private static final double WEIGHT_RSI = 0.30;
    private static final double WEIGHT_MACD = 0.25;
    private static final double WEIGHT_SMA = 0.25;
    private static final double WEIGHT_BOLLINGER = 0.20;

    @Autowired private StockRepository stockRepository;
    @Autowired private WatchlistRepository watchlistRepository;
    @Autowired private PredictionResultRepository predictionResultRepository;
    @Autowired private TechnicalIndicatorService indicators;

    /**
     * Refreshes predictions for all default watchlist tickers.
     * Runs weekdays at 06:45 AM after {@link quicktrade.com.stockMarketApiService.ApiCall}
     * has already ingested today's stock data at 06:30 AM.
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

    /**
     * Computes and persists a {@link PredictionResult} for the given ticker.
     *
     * @param ticker stock or ETF ticker symbol
     * @return the persisted {@link PredictionResult}, or empty if data is insufficient
     */
    public Optional<PredictionResult> computePrediction(String ticker) {
        List<Stocks> history = stockRepository.findHistoricalByTicker(ticker, HISTORY_DAYS);

        if (history.size() < RSI_PERIOD + 1) {
            log.debug("Insufficient data for {} ({} records). Skipping.", ticker, history.size());
            return Optional.empty();
        }

        PredictionResult result = new PredictionResult();
        result.setTicker(ticker);
        result.setDataPointsUsed(history.size());

        Double rsi = indicators.calculateRSI(history, RSI_PERIOD);
        result.setRsi(rsi);
        double rsiScore = (rsi != null) ? rsiSubScore(rsi) : 50.0;

        MACDResult macdResult = indicators.calculateMACD(history);
        double macdScore = 50.0;
        if (macdResult != null) {
            result.setMacd(macdResult.macd());
            result.setMacdSignal(macdResult.signal());
            result.setMacdHistogram(macdResult.histogram());
            macdScore = macdCrossoverScore(macdResult);
        }

        Double sma20 = indicators.calculateSMA(history, 20);
        Double sma50 = indicators.calculateSMA(history, 50);
        result.setSma20(sma20);
        result.setSma50(sma50);
        double smaScore = (sma20 != null && sma50 != null) ? smaTrendScore(sma20, sma50) : 50.0;

        BollingerResult bollinger = indicators.calculateBollingerBands(history, BOLLINGER_PERIOD, BOLLINGER_STD_MULT);
        double bollingerScore = 50.0;
        if (bollinger != null) {
            result.setBollingerUpper(bollinger.upper());
            result.setBollingerLower(bollinger.lower());
            double currentClose = history.get(history.size() - 1).getClose();
            bollingerScore = bollingerPositionScore(currentClose, bollinger);
        }

        double composite = rsiScore * WEIGHT_RSI
                + macdScore * WEIGHT_MACD
                + smaScore * WEIGHT_SMA
                + bollingerScore * WEIGHT_BOLLINGER;

        result.setPredictionScore(composite);
        result.setSignal(toSignal(composite));

        return Optional.of(predictionResultRepository.save(result));
    }

    // ── Sub-score helpers ─────────────────────────────────────────────────────

    private static double rsiSubScore(double rsi) {
        if (rsi <= 30) return 85.0 + (30.0 - rsi);
        if (rsi >= 70) return Math.max(0, 100.0 - rsi);
        return 50.0 + (50.0 - rsi);
    }

    private static double macdCrossoverScore(MACDResult r) {
        if (r.signal() == 0) return 50.0;
        double ratio = r.histogram() / Math.abs(r.signal());
        ratio = Math.max(-1.0, Math.min(1.0, ratio));
        return 50.0 + ratio * 50.0;
    }

    private static double smaTrendScore(double sma20, double sma50) {
        if (sma20 > sma50) return 75.0;
        if (sma20 < sma50) return 25.0;
        return 50.0;
    }

    private static double bollingerPositionScore(double close, BollingerResult bollinger) {
        double range = bollinger.upper() - bollinger.lower();
        if (range == 0) return 50.0;
        double position = (close - bollinger.lower()) / range;
        position = Math.max(0.0, Math.min(1.0, position));
        return (1.0 - position) * 100.0;
    }

    private static String toSignal(double score) {
        if (score > 65) return "BULLISH";
        if (score < 35) return "BEARISH";
        return "NEUTRAL";
    }
}
