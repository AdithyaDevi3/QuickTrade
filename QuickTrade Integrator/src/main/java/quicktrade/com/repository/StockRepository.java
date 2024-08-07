package quicktrade.com.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import quicktrade.com.entity.StockDTO;
import quicktrade.com.entity.StockGrowthDTO;
import quicktrade.com.entity.Stocks;

import java.util.List;
import java.util.Map;

@Repository
public interface StockRepository extends JpaRepository<Stocks, Long> {
    @Query(value = """
        WITH recent_closes AS (
            SELECT
                ticker,
                close AS recent_close,
                date::date AS recent_date
            FROM
                stocks_data
            WHERE
                date::date = (SELECT MAX(date::date) FROM stocks_data)
        ),
        year_ago_closes AS (
            SELECT
                s1.ticker,
                s1.close AS year_ago_close,
                s1.date::date AS year_ago_date
            FROM
                stocks_data s1
            JOIN (
                SELECT
                    ticker,
                    MAX(date::date) AS closest_date
                FROM
                    stocks_data
                WHERE
                    date::date <= (SELECT MAX(date::date) FROM stocks_data) - INTERVAL '1 year'
                GROUP BY
                    ticker
            ) s2
            ON
                s1.ticker = s2.ticker
                AND s1.date::date = s2.closest_date
        ),
        changes AS (
            SELECT
                recent_closes.ticker,
                ((recent_closes.recent_close - year_ago_closes.year_ago_close) / year_ago_closes.year_ago_close) * 100 AS percentage_change
            FROM
                recent_closes
            JOIN
                year_ago_closes
            ON
                recent_closes.ticker = year_ago_closes.ticker
        )
        SELECT
            ticker,
            percentage_change
        FROM
            changes
        ORDER BY
            percentage_change DESC
        LIMIT 5
    """, nativeQuery = true)
  List<Object[]> findTopStockChanges();

    @Query(value = """
        WITH recent_closes AS (
            SELECT
                ticker,
                close AS recent_close,
                date::date AS recent_date
            FROM
                stocks_data
            WHERE
                date::date = (SELECT MAX(date::date) FROM stocks_data)
        ),
        year_ago_closes AS (
            SELECT
                s1.ticker,
                s1.close AS year_ago_close,
                s1.date::date AS year_ago_date
            FROM
                stocks_data s1
            JOIN (
                SELECT
                    ticker,
                    MAX(date::date) AS closest_date
                FROM
                    stocks_data
                WHERE
                    date::date <= (SELECT MAX(date::date) FROM stocks_data) - INTERVAL '1 year'
                GROUP BY
                    ticker
            ) s2
            ON
                s1.ticker = s2.ticker
                AND s1.date::date = s2.closest_date
        ),
        changes AS (
            SELECT
                recent_closes.ticker,
                ((recent_closes.recent_close - year_ago_closes.year_ago_close) / year_ago_closes.year_ago_close) * 100 AS percentage_change
            FROM
                recent_closes
            JOIN
                year_ago_closes
            ON
                recent_closes.ticker = year_ago_closes.ticker
        )
        SELECT
            ticker,
            percentage_change
        FROM
            changes
        ORDER BY
            percentage_change ASC
        LIMIT 5
    """, nativeQuery = true)
    List<Object[]> findBottomStockChanges();

  @Query("WITH recent_closes AS (" +
          "    SELECT s.ticker, s.close AS recent_close, s.date AS recent_date " +
          "    FROM Stock s " +
          "    WHERE s.date = (SELECT MAX(s2.date) FROM Stock s2) " +
          "    AND s.volume > 0 " +
          "), " +
          "year_ago_closes AS (" +
          "    SELECT s1.ticker, s1.close AS year_ago_close, s1.date AS year_ago_date " +
          "    FROM Stock s1 " +
          "    JOIN (" +
          "        SELECT ticker, MAX(date) AS closest_date " +
          "        FROM Stock " +
          "        WHERE date <= (SELECT MAX(date) FROM Stock) - INTERVAL '1 year' " +
          "        AND volume > 0 " +
          "        GROUP BY ticker" +
          "    ) s2 ON s1.ticker = s2.ticker AND s1.date = s2.closest_date " +
          "), " +
          "changes AS (" +
          "    SELECT r.ticker, r.recent_close, y.year_ago_close, " +
          "           ((r.recent_close - y.year_ago_close) / y.year_ago_close) * 100 AS percentage_change " +
          "    FROM recent_closes r " +
          "    JOIN year_ago_closes y ON r.ticker = y.ticker " +
          ") " +
          "SELECT ticker, recent_close, year_ago_close, percentage_change " +
          "FROM changes " +
          "ORDER BY percentage_change DESC " +
          "LIMIT :limit " +
          "UNION ALL " +
          "SELECT ticker, recent_close, year_ago_close, percentage_change " +
          "FROM changes " +
          "ORDER BY percentage_change ASC " +
          "LIMIT :limit")
  List<StockGrowthDTO> findStockGrowth(@Param("limit") int limit);

  @Query("WITH recent_closes AS (" +
          "    SELECT s.ticker, s.close AS recent_close, s.date AS recent_date " +
          "    FROM Stock s " +
          "    WHERE s.date = (SELECT MAX(s2.date) FROM Stock s2) " +
          "    AND s.volume > 0 " +
          ") " +
          "SELECT ticker, recent_close " +
          "FROM recent_closes " +
          "ORDER BY recent_close DESC " +
          "LIMIT :limit")
  List<StockDTO> findTopClosingCosts(@Param("limit") int limit);

  @Query("WITH recent_closes AS (" +
          "    SELECT s.ticker, s.close AS recent_close, s.date AS recent_date " +
          "    FROM Stock s " +
          "    WHERE s.date = (SELECT MAX(s2.date) FROM Stock s2) " +
          "    AND s.volume > 0 " +
          ") " +
          "SELECT ticker, recent_close " +
          "FROM recent_closes " +
          "ORDER BY recent_close ASC " +
          "LIMIT :limit")
  List<StockDTO> findBottomClosingCosts(@Param("limit") int limit);

}
