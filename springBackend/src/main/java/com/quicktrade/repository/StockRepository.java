package com.quicktrade.repository;

import com.quicktrade.entity.Stocks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the {@link Stocks} entity (table: {@code stocks_data}).
 *
 * <p>Provides both standard CRUD from {@link JpaRepository} and custom JPQL
 * queries for retrieving per-ticker historical price series used by the
 * prediction engine.</p>
 */
@Repository
public interface StockRepository extends JpaRepository<Stocks, Long> {

    /**
     * Returns the most-recent N trading-day records for a given ticker,
     * ordered oldest-first so callers receive a chronological series.
     *
     * @param ticker ticker symbol (case-sensitive, e.g. "AAPL")
     * @param limit  maximum number of records to return
     * @return chronologically ordered list of stock records
     */
    @Query(value = """
            SELECT * FROM (
                SELECT * FROM stocks_data
                WHERE ticker = :ticker
                ORDER BY date DESC
                LIMIT :limit
            ) sub
            ORDER BY date ASC
            """, nativeQuery = true)
    List<Stocks> findRecentByTicker(@Param("ticker") String ticker, @Param("limit") int limit);
}
