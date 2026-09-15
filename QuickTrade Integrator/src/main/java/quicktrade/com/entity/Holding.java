package quicktrade.com.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "holdings", uniqueConstraints = @UniqueConstraint(columnNames = {"portfolio_id", "ticker"}))
public class Holding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @Column(name = "ticker", nullable = false)
    private String ticker;

    @Column(name = "quantity", nullable = false, precision = 19, scale = 6)
    private BigDecimal quantity;

    /** Weighted-average price paid per share across all open lots. */
    @Column(name = "avg_cost_basis", nullable = false, precision = 19, scale = 4)
    private BigDecimal avgCostBasis;

    public Holding() {
    }

    public Holding(Portfolio portfolio, String ticker, BigDecimal quantity, BigDecimal avgCostBasis) {
        this.portfolio = portfolio;
        this.ticker = ticker;
        this.quantity = quantity;
        this.avgCostBasis = avgCostBasis;
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

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getAvgCostBasis() {
        return avgCostBasis;
    }

    public void setAvgCostBasis(BigDecimal avgCostBasis) {
        this.avgCostBasis = avgCostBasis;
    }
}
