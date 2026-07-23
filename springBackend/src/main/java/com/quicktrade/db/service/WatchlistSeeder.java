package com.quicktrade.db.service;

import com.quicktrade.entity.WatchlistEntry;
import com.quicktrade.repository.WatchlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * WatchlistSeeder — seeds the default set of tickers into {@code watchlist_entries}
 * on every application startup if they are not already present.
 *
 * <p>The seed list covers the top-50 US equities by market cap plus a curated
 * set of popular broad-market and sector ETFs.  Existing rows are never
 * overwritten; new tickers are inserted via {@link WatchlistRepository#saveAll}.</p>
 */
@Component
public class WatchlistSeeder implements ApplicationRunner {

    @Autowired
    private WatchlistRepository watchlistRepository;

    /** Top-50 US stocks by approximate market cap (as of 2025). */
    private static final List<String> DEFAULT_STOCKS = List.of(
            "AAPL", "MSFT", "NVDA", "AMZN", "GOOGL", "GOOG", "META", "TSLA",
            "BRK.B", "LLY", "AVGO", "JPM", "V", "XOM", "MA", "UNH", "JNJ",
            "PG", "COST", "HD", "MRK", "ORCL", "CRM", "BAC", "CVX", "ABBV",
            "ACN", "AMD", "TMO", "NFLX", "ADBE", "LIN", "MCD", "CSCO", "WMT",
            "PEP", "ABT", "MS", "TXN", "PM", "ISRG", "DIS", "RTX", "HON",
            "AMGN", "INTU", "SPGI", "LOW", "GILD", "BKNG"
    );

    /** Popular broad-market and sector ETFs. */
    private static final List<String> DEFAULT_ETFS = List.of(
            "VOO", "VTI", "VGT", "QQQ", "SPY", "IVV", "VXUS", "VEA",
            "VNQ", "BND", "ARKK", "XLK", "VUG", "VYM", "XLV", "XLF"
    );

    /**
     * Inserts any missing default tickers into the watchlist table at startup.
     *
     * @param args application arguments (unused)
     */
    @Override
    public void run(ApplicationArguments args) {
        for (String ticker : DEFAULT_STOCKS) {
            if (!watchlistRepository.existsByTicker(ticker)) {
                watchlistRepository.save(new WatchlistEntry(ticker, "STOCK", true));
            }
        }
        for (String ticker : DEFAULT_ETFS) {
            if (!watchlistRepository.existsByTicker(ticker)) {
                watchlistRepository.save(new WatchlistEntry(ticker, "ETF", true));
            }
        }
    }
}
