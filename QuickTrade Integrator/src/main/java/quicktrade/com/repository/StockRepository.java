package quicktrade.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import quicktrade.com.entity.Stocks;

import java.util.List;

/**
 * Spring Data JPA repository for the {@link Stocks} entity (table: {@code stocks_data}).
 *
 * <p>Provides native SQL queries for the top-movers and historical-data endpoints
 * served by {@link quicktrade.com.controller.RepositoryController}.</p>
 */
@Repository
public interface StockRepository extends JpaRepository<Stocks, Long> {

    /**
     * Returns the top {@code limit} tickers ranked by single-day percentage
     * change (close vs previous-day close) on the most-recent available date.
     * Used for the "Top Movers" section in the iOS HomeView.
     *
     * @param limit maximum number of results
     * @return list of {@code [ticker, recentClose, prevClose, changePercent]} rows
     */
    @Query(value = """
            WITH latest AS (
                SELECT ticker, close, date,
                       LAG(close) OVER (PARTITION BY ticker ORDER BY date) AS prev_close
                FROM stocks_data
            ),
            ranked AS (
                SELECT ticker, close AS recent_close, prev_close,
                       CASE WHEN prev_close > 0
                            THEN ((close - prev_close) / prev_close) * 100
                            ELSE 0 END AS change_pct
                FROM latest
                WHERE date = (SELECT MAX(date) FROM stocks_data)
                  AND prev_close IS NOT NULL
            )
            SELECT ticker, recent_close, prev_close, change_pct
            FROM ranked
            ORDER BY change_pct DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<Object[]> findTopMovers(@Param("limit") int limit);

    /**
     * Returns the top {@code limit} tickers ranked by worst single-day
     * percentage change (biggest losers) on the most-recent available date.
     *
     * @param limit maximum number of results
     * @return list of {@code [ticker, recentClose, prevClose, changePercent]} rows
     */
    @Query(value = """
            WITH latest AS (
                SELECT ticker, close, date,
                       LAG(close) OVER (PARTITION BY ticker ORDER BY date) AS prev_close
                FROM stocks_data
            ),
            ranked AS (
                SELECT ticker, close AS recent_close, prev_close,
                       CASE WHEN prev_close > 0
                            THEN ((close - prev_close) / prev_close) * 100
                            ELSE 0 END AS change_pct
                FROM latest
                WHERE date = (SELECT MAX(date) FROM stocks_data)
                  AND prev_close IS NOT NULL
            )
            SELECT ticker, recent_close, prev_close, change_pct
            FROM ranked
            ORDER BY change_pct ASC
            LIMIT :limit
            """, nativeQuery = true)
    List<Object[]> findTopLosers(@Param("limit") int limit);

    /**
     * Returns the most-recent {@code days} trading-day close prices for a
     * given ticker, ordered chronologically (oldest first).  Used for charting
     * price history in the iOS StockDetailView.
     *
     * @param ticker ticker symbol (case-sensitive)
     * @param days   number of most-recent records to return
     * @return chronologically ordered list of stocks records
     */
    @Query(value = """
            SELECT * FROM (
                SELECT * FROM stocks_data
                WHERE ticker = :ticker
                ORDER BY date DESC
                LIMIT :days
            ) sub
            ORDER BY date ASC
            """, nativeQuery = true)
    List<Stocks> findHistoricalByTicker(@Param("ticker") String ticker, @Param("days") int days);

    /**
     * Returns summary metrics for a single ticker (latest close, open, high,
     * low, volume, 52-week high and low) from the stored data.
     *
     * @param ticker ticker symbol
     * @return single row {@code [ticker, close, open, high, low, volume, week52High, week52Low]}
     */
    @Query(value = """
            WITH latest_day AS (
                SELECT ticker, close, open, high, low, volume
                FROM stocks_data
                WHERE ticker = :ticker
                ORDER BY date DESC
                LIMIT 1
            ),
            yearly AS (
                SELECT MAX(high) AS week52_high, MIN(low) AS week52_low
                FROM stocks_data
                WHERE ticker = :ticker
                  AND date >= (SELECT MAX(date) FROM stocks_data) - INTERVAL '1 year'
            )
            SELECT l.ticker, l.close, l.open, l.high, l.low, l.volume,
                   y.week52_high, y.week52_low
            FROM latest_day l, yearly y
            """, nativeQuery = true)
    Object[] findMetricsByTicker(@Param("ticker") String ticker);
}
