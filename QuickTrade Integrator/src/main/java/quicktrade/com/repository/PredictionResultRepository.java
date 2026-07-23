package quicktrade.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quicktrade.com.entity.PredictionResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Read-only Spring Data JPA repository for {@link PredictionResult}.
 *
 * <p>The Integrator only reads prediction data; all writes are performed by
 * the springBackend service.  Both services share the same PostgreSQL instance.</p>
 */
@Repository
public interface PredictionResultRepository extends JpaRepository<PredictionResult, Long> {

    /**
     * Returns the most-recent prediction for the given ticker.
     *
     * @param ticker stock or ETF symbol
     * @return the latest prediction, or empty if none exists
     */
    Optional<PredictionResult> findTopByTickerOrderByComputedAtDesc(String ticker);

    /**
     * Returns all predictions computed after the given cutoff.
     * Used to retrieve today's full watchlist predictions.
     *
     * @param cutoff lower-bound timestamp
     * @return predictions newer than {@code cutoff}
     */
    List<PredictionResult> findAllByComputedAtAfter(LocalDateTime cutoff);
}
