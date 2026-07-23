package quicktrade.com.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import quicktrade.com.entity.*;
import quicktrade.com.repository.StockNameRepository;
import quicktrade.com.repository.StockRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * StockService — business logic for stock data operations exposed by the
 * {@link quicktrade.com.controller.RepositoryController}.
 *
 * <p>Provides top-movers, top-losers, historical price series, per-ticker
 * metrics, and fuzzy stock-name search — all backed by the shared
 * {@code stocks_data} PostgreSQL table.</p>
 */
@Service
public class StockService {

    @Autowired private StockRepository stockRepository;
    @Autowired private StockNameRepository stockNameRepository;

    // ── Top Movers ────────────────────────────────────────────────────────────

    /**
     * Returns the top {@code limit} stocks with the largest single-day
     * percentage gain on the most-recent available trading date.
     *
     * @param limit maximum number of results
     * @return list of growth DTOs ordered by changePercent descending
     */
    public List<StockGrowthDTO> getTopMovers(int limit) {
        List<Object[]> rows = stockRepository.findTopMovers(limit);
        return mapToGrowthDTOs(rows);
    }

    /**
     * Returns the top {@code limit} stocks with the largest single-day
     * percentage decline on the most-recent available trading date.
     *
     * @param limit maximum number of results
     * @return list of growth DTOs ordered by changePercent ascending (most negative first)
     */
    public List<StockGrowthDTO> getTopLosers(int limit) {
        List<Object[]> rows = stockRepository.findTopLosers(limit);
        return mapToGrowthDTOs(rows);
    }

    // ── Historical Data ───────────────────────────────────────────────────────

    /**
     * Returns up to {@code days} trading-day records for a ticker ordered
     * oldest-first, suitable for feeding into a line chart.
     *
     * @param ticker stock symbol
     * @param days   number of past records to fetch (e.g. 90)
     * @return chronologically ordered list of {@link Stocks} records
     */
    public List<Stocks> getHistoricalData(String ticker, int days) {
        return stockRepository.findHistoricalByTicker(ticker, days);
    }

    // ── Metrics ───────────────────────────────────────────────────────────────

    /**
     * Returns key price and volume metrics for a single ticker.
     *
     * @param ticker stock symbol
     * @return metrics DTO, or {@code null} if no data exists for the ticker
     */
    public StockMetricsDTO getMetrics(String ticker) {
        Object[] row = stockRepository.findMetricsByTicker(ticker);
        if (row == null || row.length < 8) return null;

        double close    = toDouble(row[1]);
        double open     = toDouble(row[2]);
        double high     = toDouble(row[3]);
        double low      = toDouble(row[4]);
        long   volume   = toLong(row[5]);
        double w52High  = toDouble(row[6]);
        double w52Low   = toDouble(row[7]);

        double changeAmt = close - open;
        double changePct = open > 0 ? (changeAmt / open) * 100 : 0;

        return new StockMetricsDTO(ticker, close, open, high, low, volume,
                w52High, w52Low, changeAmt, changePct);
    }

    // ── Search ────────────────────────────────────────────────────────────────

    /**
     * Performs a fuzzy search over stock symbols and company names.
     * Results are deduplicated and filtered to those with a positive match score.
     *
     * @param query partial symbol or company name
     * @return list of matching stocks with computed match percentages
     */
    public List<StockMatchDTO> searchStocks(String query) {
        List<StockNames> symbolMatches = stockNameRepository.findBySymbolContainingIgnoreCase(query);
        List<StockNames> nameMatches   = stockNameRepository.findByNameContainingIgnoreCase(query);

        List<StockMatchDTO> result = new ArrayList<>();

        for (StockNames stock : symbolMatches) {
            double matchPct = calculateMatchPercentage(query, stock.getSymbol());
            if (matchPct > 0) {
                result.add(new StockMatchDTO(stock.getSymbol(), stock.getName(),
                        stock.getLogoUrl(), matchPct));
            }
        }

        for (StockNames stock : nameMatches) {
            double matchPct = calculateMatchPercentage(query, stock.getName());
            boolean alreadyAdded = result.stream()
                    .anyMatch(s -> s.getSymbol().equals(stock.getSymbol()));
            if (matchPct > 0 && !alreadyAdded) {
                result.add(new StockMatchDTO(stock.getSymbol(), stock.getName(),
                        stock.getLogoUrl(), matchPct));
            }
        }

        return result;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Maps raw {@code [ticker, recentClose, prevClose, changePct]} rows from
     * native queries into {@link StockGrowthDTO} objects.
     *
     * @param rows raw query result rows
     * @return mapped DTO list
     */
    private List<StockGrowthDTO> mapToGrowthDTOs(List<Object[]> rows) {
        List<StockGrowthDTO> result = new ArrayList<>();
        for (Object[] row : rows) {
            if (row == null || row.length < 4) continue;
            String ticker     = (String) row[0];
            BigDecimal recent = BigDecimal.valueOf(toDouble(row[1]));
            BigDecimal prev   = BigDecimal.valueOf(toDouble(row[2]));
            BigDecimal change = BigDecimal.valueOf(toDouble(row[3]));
            result.add(new StockGrowthDTO(ticker, recent, prev, change));
        }
        return result;
    }

    private double toDouble(Object val) {
        if (val == null) return 0.0;
        if (val instanceof Number n) return n.doubleValue();
        return 0.0;
    }

    private long toLong(Object val) {
        if (val == null) return 0L;
        if (val instanceof Number n) return n.longValue();
        return 0L;
    }

    /**
     * Calculates the percentage of leading characters that match between
     * {@code query} and {@code target}.
     *
     * @param query  search input
     * @param target candidate string
     * @return match percentage (0–100)
     */
    private double calculateMatchPercentage(String query, String target) {
        int matches = 0;
        for (int i = 0; i < Math.min(query.length(), target.length()); i++) {
            if (Character.toLowerCase(query.charAt(i)) ==
                    Character.toLowerCase(target.charAt(i))) {
                matches++;
            }
        }
        return ((double) matches / target.length()) * 100;
    }
}
