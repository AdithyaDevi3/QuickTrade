package com.quicktrade.service;

import com.quicktrade.entity.Stocks;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * TechnicalIndicatorService — pure, stateless calculation methods for the
 * standard technical analysis indicators used by the prediction engine.
 *
 * <p>All methods accept a chronologically ordered {@code List<Stocks>} (oldest
 * first) and return a {@code Double} or a nested result record.  Input lists
 * shorter than the required minimum period return {@code null} so that callers
 * can detect insufficient data and skip the indicator rather than crashing.</p>
 *
 * <h2>Implemented Indicators</h2>
 * <ul>
 *   <li>RSI — Relative Strength Index (Wilder smoothing, configurable period)</li>
 *   <li>SMA — Simple Moving Average</li>
 *   <li>EMA — Exponential Moving Average</li>
 *   <li>MACD — Moving Average Convergence/Divergence (line, signal, histogram)</li>
 *   <li>Bollinger Bands — upper, middle, lower (configurable period and σ-multiplier)</li>
 * </ul>
 */
@Service
public class TechnicalIndicatorService {

    // ── RSI ───────────────────────────────────────────────────────────────────

    /**
     * Computes the Relative Strength Index using Wilder's smoothed moving-average
     * method over the specified period.
     *
     * <p>RSI oscillates between 0 and 100. Readings above 70 are traditionally
     * considered overbought; readings below 30 are oversold.</p>
     *
     * @param history chronologically ordered list of stock records (oldest first)
     * @param period  look-back window (typically 14)
     * @return RSI value in [0, 100], or {@code null} if {@code history.size() <= period}
     */
    public Double calculateRSI(List<Stocks> history, int period) {
        if (history == null || history.size() <= period) return null;

        double avgGain = 0.0;
        double avgLoss = 0.0;

        // Seed initial averages from the first `period` price changes
        for (int i = 1; i <= period; i++) {
            double change = history.get(i).getClose() - history.get(i - 1).getClose();
            if (change > 0) avgGain += change;
            else avgLoss += Math.abs(change);
        }
        avgGain /= period;
        avgLoss /= period;

        // Wilder smoothing for the remainder of the series
        for (int i = period + 1; i < history.size(); i++) {
            double change = history.get(i).getClose() - history.get(i - 1).getClose();
            if (change > 0) {
                avgGain = (avgGain * (period - 1) + change) / period;
                avgLoss = (avgLoss * (period - 1)) / period;
            } else {
                avgGain = (avgGain * (period - 1)) / period;
                avgLoss = (avgLoss * (period - 1) + Math.abs(change)) / period;
            }
        }

        if (avgLoss == 0) return 100.0;
        double rs = avgGain / avgLoss;
        return 100.0 - (100.0 / (1.0 + rs));
    }

    // ── SMA ───────────────────────────────────────────────────────────────────

    /**
     * Computes the Simple Moving Average of close prices for the most-recent
     * {@code period} records.
     *
     * @param history chronologically ordered list of stock records
     * @param period  number of most-recent records to average
     * @return SMA value, or {@code null} if insufficient data
     */
    public Double calculateSMA(List<Stocks> history, int period) {
        if (history == null || history.size() < period) return null;

        int start = history.size() - period;
        double sum = 0.0;
        for (int i = start; i < history.size(); i++) {
            sum += history.get(i).getClose();
        }
        return sum / period;
    }

    // ── EMA ───────────────────────────────────────────────────────────────────

    /**
     * Computes the Exponential Moving Average of close prices using the standard
     * multiplier {@code 2 / (period + 1)}.
     *
     * <p>Seeded from the SMA of the first {@code period} records.</p>
     *
     * @param history chronologically ordered list of stock records
     * @param period  EMA period (e.g. 12 or 26 for MACD)
     * @return EMA value, or {@code null} if insufficient data
     */
    public Double calculateEMA(List<Stocks> history, int period) {
        if (history == null || history.size() < period) return null;

        double multiplier = 2.0 / (period + 1);

        // Seed from SMA of first `period` records
        double ema = 0.0;
        for (int i = 0; i < period; i++) {
            ema += history.get(i).getClose();
        }
        ema /= period;

        for (int i = period; i < history.size(); i++) {
            ema = (history.get(i).getClose() - ema) * multiplier + ema;
        }
        return ema;
    }

    // ── MACD ──────────────────────────────────────────────────────────────────

    /**
     * Computes the standard MACD indicator (12-26-9 settings).
     *
     * <ul>
     *   <li><b>macd</b> — EMA-12 minus EMA-26</li>
     *   <li><b>signal</b> — 9-period EMA of the MACD line</li>
     *   <li><b>histogram</b> — macd minus signal</li>
     * </ul>
     *
     * @param history chronologically ordered list of stock records
     * @return {@link MACDResult} containing macd, signal and histogram values,
     *         or {@code null} if there are fewer than 35 data points
     */
    public MACDResult calculateMACD(List<Stocks> history) {
        if (history == null || history.size() < 35) return null;

        // Build MACD-line series using a rolling approach
        int fastPeriod = 12;
        int slowPeriod = 26;
        int signalPeriod = 9;

        // Compute daily MACD values across the whole history
        // We need at least slowPeriod records before the first MACD value
        List<Double> macdValues = new java.util.ArrayList<>();
        double fastEma = seedEma(history, 0, fastPeriod);
        double slowEma = seedEma(history, 0, slowPeriod);
        double fastMult = 2.0 / (fastPeriod + 1);
        double slowMult = 2.0 / (slowPeriod + 1);

        for (int i = slowPeriod; i < history.size(); i++) {
            double close = history.get(i).getClose();
            fastEma = (close - fastEma) * fastMult + fastEma;
            slowEma = (close - slowEma) * slowMult + slowEma;
            macdValues.add(fastEma - slowEma);
        }

        if (macdValues.size() < signalPeriod) return null;

        // Signal line = EMA-9 of MACD values
        double signalMult = 2.0 / (signalPeriod + 1);
        double signal = 0;
        for (int i = 0; i < signalPeriod; i++) signal += macdValues.get(i);
        signal /= signalPeriod;
        for (int i = signalPeriod; i < macdValues.size(); i++) {
            signal = (macdValues.get(i) - signal) * signalMult + signal;
        }

        double macd = macdValues.get(macdValues.size() - 1);
        return new MACDResult(macd, signal, macd - signal);
    }

    // ── Bollinger Bands ───────────────────────────────────────────────────────

    /**
     * Computes Bollinger Bands using the most-recent {@code period} records.
     *
     * <ul>
     *   <li><b>middle</b> — SMA of close prices</li>
     *   <li><b>upper</b> — middle + {@code stdDevMultiplier} × standard deviation</li>
     *   <li><b>lower</b> — middle − {@code stdDevMultiplier} × standard deviation</li>
     * </ul>
     *
     * @param history           chronologically ordered list of stock records
     * @param period            look-back window (typically 20)
     * @param stdDevMultiplier  band width multiplier (typically 2.0)
     * @return {@link BollingerResult}, or {@code null} if insufficient data
     */
    public BollingerResult calculateBollingerBands(List<Stocks> history, int period, double stdDevMultiplier) {
        if (history == null || history.size() < period) return null;

        int start = history.size() - period;
        double sum = 0.0;
        for (int i = start; i < history.size(); i++) sum += history.get(i).getClose();
        double sma = sum / period;

        double variance = 0.0;
        for (int i = start; i < history.size(); i++) {
            double diff = history.get(i).getClose() - sma;
            variance += diff * diff;
        }
        double stdDev = Math.sqrt(variance / period);

        return new BollingerResult(sma + stdDevMultiplier * stdDev, sma, sma - stdDevMultiplier * stdDev);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private double seedEma(List<Stocks> history, int startIdx, int period) {
        double sum = 0;
        for (int i = startIdx; i < startIdx + period && i < history.size(); i++) {
            sum += history.get(i).getClose();
        }
        return sum / Math.min(period, history.size() - startIdx);
    }

    // ── Result Records ────────────────────────────────────────────────────────

    /**
     * Immutable result container for {@link #calculateMACD(List)}.
     *
     * @param macd      current MACD line value
     * @param signal    current signal line value (9-period EMA of MACD)
     * @param histogram difference between MACD and signal
     */
    public record MACDResult(double macd, double signal, double histogram) {}

    /**
     * Immutable result container for {@link #calculateBollingerBands(List, int, double)}.
     *
     * @param upper  upper band value
     * @param middle middle band (SMA)
     * @param lower  lower band value
     */
    public record BollingerResult(double upper, double middle, double lower) {}
}
