package quicktrade.com.entity;

import jakarta.persistence.*;

/**
 * WatchlistEntry — maps to the {@code watchlist_entries} table, seeded at
 * startup by this service's own {@code WatchlistSeeder}.
 */
@Entity
@Table(name = "watchlist_entries")
public class WatchlistEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticker")
    private String ticker;

    @Column(name = "type")
    private String type;

    @Column(name = "is_default")
    private boolean isDefault;

    public WatchlistEntry() {
    }

    public WatchlistEntry(String ticker, String type, boolean isDefault) {
        this.ticker = ticker;
        this.type = type;
        this.isDefault = isDefault;
    }

    public Long getId() { return id; }
    public String getTicker() { return ticker; }
    public String getType() { return type; }
    public boolean isDefault() { return isDefault; }

    public void setTicker(String ticker) { this.ticker = ticker; }
    public void setType(String type) { this.type = type; }
    public void setDefault(boolean isDefault) { this.isDefault = isDefault; }
}
