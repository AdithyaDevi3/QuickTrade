package com.quicktrade.repository;

import com.quicktrade.entity.PredictionResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link PredictionResult}.
 *
 * <p>Provides named query methods for retrieving the latest prediction per ticker
 * and bulk lookups for the daily predictions dashboard.</p>
 */
@Repository
public interface PredictionResultRepository extends JpaRepository<PredictionResult, Long> {

    /**
     * Returns the most-recent prediction for a given ticker.
     *
     * @param ticker stock or ETF symbol
     * @return the latest {@link PredictionResult}, or empty if none exists
     */
    Optional<PredictionResult> findTopByTickerOrderByComputedAtDesc(String ticker);

    /**
     * Returns all predictions computed after the given cutoff timestamp.
     * Used to retrieve today's full watchlist predictions.
     *
     * @param cutoff lower-bound timestamp (exclusive)
     * @return list of predictions newer than {@code cutoff}
     */
    List<PredictionResult> findAllByComputedAtAfter(LocalDateTime cutoff);

    /**
     * Returns all distinct tickers that have at least one stored prediction.
     *
     * @return list of ticker strings
     */
    List<String> findDistinctTickerBy();
}
