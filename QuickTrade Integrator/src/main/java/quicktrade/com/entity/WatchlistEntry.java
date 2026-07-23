package quicktrade.com.entity;

import jakarta.persistence.*;

/**
 * WatchlistEntry (read-only mirror) — maps to the {@code watchlist_entries}
 * table seeded by the springBackend's {@code WatchlistSeeder}.
 *
 * <p>The Integrator reads this table to know the type ("STOCK" / "ETF") of
 * each ticker when building prediction response DTOs.</p>
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

    public Long getId() { return id; }
    public String getTicker() { return ticker; }
    public String getType() { return type; }
    public boolean isDefault() { return isDefault; }
}
