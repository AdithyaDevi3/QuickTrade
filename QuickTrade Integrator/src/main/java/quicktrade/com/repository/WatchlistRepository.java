package quicktrade.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quicktrade.com.entity.WatchlistEntry;

import java.util.List;
import java.util.Optional;

/**
 * Read-only Spring Data JPA repository for {@link WatchlistEntry}.
 */
@Repository
public interface WatchlistRepository extends JpaRepository<WatchlistEntry, Long> {

    /**
     * Returns all watchlist entries of a given type ("STOCK" or "ETF").
     *
     * @param type entry category
     * @return matching entries
     */
    List<WatchlistEntry> findByType(String type);

    /**
     * Finds a watchlist entry by ticker symbol.
     *
     * @param ticker ticker symbol
     * @return the matching entry, or empty
     */
    Optional<WatchlistEntry> findByTicker(String ticker);
}
