package quicktrade.com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import quicktrade.com.entity.*;
import quicktrade.com.service.DefinitionsSeeder;
import quicktrade.com.service.PredictionClientService;
import quicktrade.com.service.StockService;

import java.util.List;

/**
 * RepositoryController — primary REST controller for the QuickTrade Integrator.
 *
 * <p>All endpoints are prefixed with {@code /api} and permit cross-origin
 * requests so the iOS simulator and any web clients can reach them during
 * development.</p>
 *
 * <h2>Endpoints</h2>
 * <table>
 *   <tr><th>Method</th><th>Path</th><th>Description</th></tr>
 *   <tr><td>GET</td><td>/api/search</td><td>Fuzzy stock/ETF name search</td></tr>
 *   <tr><td>GET</td><td>/api/stocks/top-movers</td><td>Daily top-N gainers</td></tr>
 *   <tr><td>GET</td><td>/api/stocks/top-losers</td><td>Daily top-N losers</td></tr>
 *   <tr><td>GET</td><td>/api/stocks/{ticker}/historical</td><td>Price history chart data</td></tr>
 *   <tr><td>GET</td><td>/api/stocks/{ticker}/metrics</td><td>Snapshot metrics</td></tr>
 *   <tr><td>GET</td><td>/api/predictions</td><td>Full watchlist predictions</td></tr>
 *   <tr><td>GET</td><td>/api/predictions/{ticker}</td><td>Single-ticker prediction detail</td></tr>
 *   <tr><td>GET</td><td>/api/learn/definitions</td><td>Financial term definitions</td></tr>
 *   <tr><td>GET</td><td>/api/learn/quiz</td><td>MCQ quiz questions</td></tr>
 * </table>
 */
@RestController
@CrossOrigin(origins = "*")
public class RepositoryController {

    @Autowired private StockService stockService;
    @Autowired private PredictionClientService predictionClientService;
    @Autowired private DefinitionsSeeder definitionsSeeder;

    // ── Search ────────────────────────────────────────────────────────────────

    /**
     * Fuzzy search for stocks and ETFs by symbol or company name.
     *
     * @param query partial symbol or name string
     * @return list of matching stocks with match percentages
     */
    @GetMapping("/api/search")
    public List<StockMatchDTO> searchStocks(@RequestParam String query) {
        return stockService.searchStocks(query);
    }

    // ── Stocks ────────────────────────────────────────────────────────────────

    /**
     * Returns the top gainers for the most-recent trading day.
     *
     * @param limit maximum number of results (default 10)
     * @return list of growth DTOs ordered by change percent descending
     */
    @GetMapping("/api/stocks/top-movers")
    public List<StockGrowthDTO> getTopMovers(
            @RequestParam(defaultValue = "10") int limit) {
        return stockService.getTopMovers(limit);
    }

    /**
     * Returns the top losers for the most-recent trading day.
     *
     * @param limit maximum number of results (default 10)
     * @return list of growth DTOs ordered by change percent ascending
     */
    @GetMapping("/api/stocks/top-losers")
    public List<StockGrowthDTO> getTopLosers(
            @RequestParam(defaultValue = "10") int limit) {
        return stockService.getTopLosers(limit);
    }

    /**
     * Returns historical close prices for a ticker (for line chart rendering).
     *
     * @param ticker stock or ETF symbol
     * @param days   number of trading days to return (default 90)
     * @return chronologically ordered list of stock records
     */
    @GetMapping("/api/stocks/{ticker}/historical")
    public List<Stocks> getHistorical(
            @PathVariable String ticker,
            @RequestParam(defaultValue = "90") int days) {
        return stockService.getHistoricalData(ticker, days);
    }

    /**
     * Returns snapshot metrics for a single ticker.
     *
     * @param ticker stock or ETF symbol
     * @return metrics DTO, or 404 if no data exists
     */
    @GetMapping("/api/stocks/{ticker}/metrics")
    public ResponseEntity<StockMetricsDTO> getMetrics(@PathVariable String ticker) {
        StockMetricsDTO metrics = stockService.getMetrics(ticker);
        if (metrics == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(metrics);
    }

    // ── Predictions ───────────────────────────────────────────────────────────

    /**
     * Returns the latest prediction for every default watchlist ticker.
     *
     * @return list of prediction summaries sorted by composite score descending
     */
    @GetMapping("/api/predictions")
    public List<PredictionSummaryDTO> getAllPredictions() {
        return predictionClientService.getAllPredictions();
    }

    /**
     * Returns the full indicator breakdown for a single ticker.
     *
     * @param ticker stock or ETF symbol
     * @return prediction detail, or 404 if no prediction exists
     */
    @GetMapping("/api/predictions/{ticker}")
    public ResponseEntity<PredictionDetailDTO> getPrediction(@PathVariable String ticker) {
        return predictionClientService.getPredictionForTicker(ticker)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ── Learn ─────────────────────────────────────────────────────────────────

    /**
     * Returns financial term definitions filtered by difficulty level.
     *
     * @param difficulty "beginner", "intermediate", or "advanced"
     *                   (omit to return all definitions)
     * @return list of definition DTOs
     */
    @GetMapping("/api/learn/definitions")
    public List<DefinitionDTO> getDefinitions(
            @RequestParam(required = false) String difficulty) {
        return definitionsSeeder.getDefinitions(difficulty);
    }

    /**
     * Generates a shuffled multiple-choice quiz for a given difficulty level.
     *
     * @param difficulty "beginner", "intermediate", or "advanced" (default "beginner")
     * @param count      number of questions to generate (default 5)
     * @return list of quiz question DTOs
     */
    @GetMapping("/api/learn/quiz")
    public List<QuizQuestionDTO> getQuiz(
            @RequestParam(defaultValue = "beginner") String difficulty,
            @RequestParam(defaultValue = "5") int count) {
        return definitionsSeeder.generateQuiz(difficulty, count);
    }
}
