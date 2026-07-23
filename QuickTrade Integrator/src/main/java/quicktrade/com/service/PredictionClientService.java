package quicktrade.com.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import quicktrade.com.entity.*;
import quicktrade.com.repository.PredictionResultRepository;
import quicktrade.com.repository.StockNameRepository;
import quicktrade.com.repository.WatchlistRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * PredictionClientService — reads the {@code prediction_results} table that is
 * written by the springBackend's {@code PredictionService} and assembles the
 * response DTOs consumed by the iOS Predictions tab.
 *
 * <p>Both services share the same PostgreSQL database.  No inter-service HTTP
 * call is needed; the Integrator queries the table directly.</p>
 */
@Service
public class PredictionClientService {

    @Autowired private PredictionResultRepository predictionResultRepository;
    @Autowired private WatchlistRepository watchlistRepository;
    @Autowired private StockNameRepository stockNameRepository;

    /**
     * Returns the latest prediction summary for every default watchlist ticker.
     *
     * <p>Enriches each prediction with the company name and logo URL from the
     * {@code stock_names} table and the type ("STOCK" / "ETF") from
     * {@code watchlist_entries}.</p>
     *
     * @return list of prediction summaries ordered by score descending
     */
    public List<PredictionSummaryDTO> getAllPredictions() {
        // Fetch predictions computed in the last 7 days (covers weekends)
        LocalDateTime since = LocalDate.now().minusDays(7).atStartOfDay();
        List<PredictionResult> results = predictionResultRepository.findAllByComputedAtAfter(since);

        List<PredictionSummaryDTO> summaries = new ArrayList<>();
        for (PredictionResult pr : results) {
            PredictionSummaryDTO dto = toSummary(pr);
            summaries.add(dto);
        }

        // Sort by score descending (most bullish first)
        summaries.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
        return summaries;
    }

    /**
     * Returns the full prediction detail for a single ticker.
     *
     * @param ticker stock or ETF symbol
     * @return prediction detail, or empty if no prediction exists for the ticker
     */
    public Optional<PredictionDetailDTO> getPredictionForTicker(String ticker) {
        return predictionResultRepository
                .findTopByTickerOrderByComputedAtDesc(ticker)
                .map(this::toDetail);
    }

    // ── Mapping helpers ───────────────────────────────────────────────────────

    private PredictionSummaryDTO toSummary(PredictionResult pr) {
        PredictionSummaryDTO dto = new PredictionSummaryDTO();
        dto.setTicker(pr.getTicker());
        dto.setSignal(pr.getSignal());
        dto.setScore(pr.getPredictionScore() != null ? pr.getPredictionScore() : 50.0);
        dto.setRsi(pr.getRsi());
        dto.setMacd(pr.getMacd());
        dto.setMacdSignal(pr.getMacdSignal());
        dto.setComputedAt(pr.getComputedAt());
        enrichWithNameAndLogo(dto, pr.getTicker());
        return dto;
    }

    private PredictionDetailDTO toDetail(PredictionResult pr) {
        PredictionDetailDTO dto = new PredictionDetailDTO();
        dto.setTicker(pr.getTicker());
        dto.setSignal(pr.getSignal());
        dto.setScore(pr.getPredictionScore() != null ? pr.getPredictionScore() : 50.0);
        dto.setRsi(pr.getRsi());
        dto.setMacd(pr.getMacd());
        dto.setMacdSignal(pr.getMacdSignal());
        dto.setMacdHistogram(pr.getMacdHistogram());
        dto.setSma20(pr.getSma20());
        dto.setSma50(pr.getSma50());
        dto.setBollingerUpper(pr.getBollingerUpper());
        dto.setBollingerLower(pr.getBollingerLower());
        dto.setDataPointsUsed(pr.getDataPointsUsed());
        dto.setComputedAt(pr.getComputedAt());

        // Enrich with name, logo, type
        List<StockNames> names = stockNameRepository.findBySymbolContainingIgnoreCase(pr.getTicker());
        if (!names.isEmpty()) {
            dto.setName(names.get(0).getName());
            dto.setLogoUrl(names.get(0).getLogoUrl());
        }
        watchlistRepository.findByTicker(pr.getTicker())
                .ifPresent(w -> dto.setType(w.getType()));

        return dto;
    }

    /**
     * Populates name, logo, and type fields on a {@link PredictionSummaryDTO}
     * from the stock-names and watchlist tables.
     *
     * @param dto    DTO to enrich
     * @param ticker ticker symbol
     */
    private void enrichWithNameAndLogo(PredictionSummaryDTO dto, String ticker) {
        List<StockNames> names = stockNameRepository.findBySymbolContainingIgnoreCase(ticker);
        if (!names.isEmpty()) {
            dto.setName(names.get(0).getName());
            dto.setLogoUrl(names.get(0).getLogoUrl());
        }
        watchlistRepository.findByTicker(ticker)
                .ifPresent(w -> dto.setType(w.getType()));
    }
}
