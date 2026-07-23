package com.quicktrade.repository;

import com.quicktrade.entity.WatchlistEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link WatchlistEntry}.
 *
 * <p>Used by {@link com.quicktrade.db.service.WatchlistSeeder} to seed default
 * tickers and by {@link com.quicktrade.service.PredictionService} to retrieve
 * the full watchlist before running the daily prediction cycle.</p>
 */
@Repository
public interface WatchlistRepository extends JpaRepository<WatchlistEntry, Long> {

    /**
     * Returns all watchlist entries of a given type.
     *
     * @param type "STOCK" or "ETF"
     * @return matching entries
     */
    List<WatchlistEntry> findByType(String type);

    /**
     * Checks whether a ticker already exists in the watchlist.
     * Used during seeding to avoid duplicate inserts.
     *
     * @param ticker ticker symbol to check
     * @return true if the ticker is already present
     */
    boolean existsByTicker(String ticker);

    /**
     * Returns all default (auto-seeded) watchlist entries.
     *
     * @param isDefault true to retrieve only seeded entries
     * @return list of default entries
     */
    List<WatchlistEntry> findByIsDefault(boolean isDefault);
}
