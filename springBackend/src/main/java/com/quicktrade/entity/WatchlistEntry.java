package com.quicktrade.entity;

import jakarta.persistence.*;

/**
 * WatchlistEntry entity — the curated list of tickers for which the prediction
 * engine runs on every scheduled cycle.
 *
 * <p>Default entries are seeded at application startup by
 * {@link com.quicktrade.db.service.WatchlistSeeder}. Additional entries can be
 * inserted directly into the {@code watchlist_entries} table.</p>
 */
@Entity
@Table(name = "watchlist_entries", uniqueConstraints = {
        @UniqueConstraint(name = "uq_watchlist_ticker", columnNames = "ticker")
})
public class WatchlistEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Stock or ETF ticker symbol, e.g. "AAPL" or "VOO". */
    @Column(name = "ticker", nullable = false, length = 10)
    private String ticker;

    /**
     * Category used to group results in the iOS Predictions tab.
     * Values: "STOCK" or "ETF".
     */
    @Column(name = "type", nullable = false, length = 10)
    private String type;

    /**
     * When true this entry was seeded automatically and should not be removed
     * by cleanup jobs. User-added entries have isDefault = false.
     */
    @Column(name = "is_default")
    private boolean isDefault;

    // ── Constructors ──────────────────────────────────────────────────────────

    public WatchlistEntry() {}

    /**
     * @param ticker    ticker symbol
     * @param type      "STOCK" or "ETF"
     * @param isDefault true for auto-seeded entries
     */
    public WatchlistEntry(String ticker, String type, boolean isDefault) {
        this.ticker = ticker;
        this.type = type;
        this.isDefault = isDefault;
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTicker() { return ticker; }
    public void setTicker(String ticker) { this.ticker = ticker; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean aDefault) { isDefault = aDefault; }
}
