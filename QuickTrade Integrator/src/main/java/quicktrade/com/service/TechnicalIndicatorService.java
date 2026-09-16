package quicktrade.com.service;

import org.springframework.stereotype.Service;
import quicktrade.com.entity.Stocks;

import java.util.List;

/**
 * TechnicalIndicatorService — pure, stateless calculation methods for the
 * standard technical analysis indicators used by the prediction engine.
 *
 * <p>All methods accept a chronologically ordered {@code List<Stocks>} (oldest
 * first) and return a {@code Double} or a nested result record.  Input lists
 * shorter than the required minimum period return {@code null} so that callers
 * can detect insufficient data and skip the indicator rather than crashing.</p>
 */
@Service
public class TechnicalIndicatorService {

    // ── RSI ───────────────────────────────────────────────────────────────────

    public Double calculateRSI(List<Stocks> history, int period) {
        if (history == null || history.size() <= period) return null;

        double avgGain = 0.0;
        double avgLoss = 0.0;

        for (int i = 1; i <= period; i++) {
            double change = history.get(i).getClose() - history.get(i - 1).getClose();
            if (change > 0) avgGain += change;
            else avgLoss += Math.abs(change);
        }
        avgGain /= period;
        avgLoss /= period;

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

    public Double calculateEMA(List<Stocks> history, int period) {
        if (history == null || history.size() < period) return null;

        double multiplier = 2.0 / (period + 1);

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

    public MACDResult calculateMACD(List<Stocks> history) {
        if (history == null || history.size() < 35) return null;

        int fastPeriod = 12;
        int slowPeriod = 26;
        int signalPeriod = 9;

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

    public record MACDResult(double macd, double signal, double histogram) {}

    public record BollingerResult(double upper, double middle, double lower) {}
}
