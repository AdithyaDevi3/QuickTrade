package quicktrade.com.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "trades")
public class Trade {

    public enum Side {
        BUY, SELL
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @Column(name = "ticker", nullable = false)
    private String ticker;

    @Enumerated(EnumType.STRING)
    @Column(name = "side", nullable = false)
    private Side side;

    @Column(name = "quantity", nullable = false, precision = 19, scale = 6)
    private BigDecimal quantity;

    /** Execution price — the ticker's latest available close at the time of the trade. */
    @Column(name = "price", nullable = false, precision = 19, scale = 4)
    private BigDecimal price;

    @Column(name = "executed_at", nullable = false)
    private Instant executedAt = Instant.now();

    public Trade() {
    }

    public Trade(Portfolio portfolio, String ticker, Side side, BigDecimal quantity, BigDecimal price) {
        this.portfolio = portfolio;
        this.ticker = ticker;
        this.side = side;
        this.quantity = quantity;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public String getTicker() {
        return ticker;
    }

    public Side getSide() {
        return side;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Instant getExecutedAt() {
        return executedAt;
    }
}
