package quicktrade.com.entity;

import java.math.BigDecimal;

/** A single holding line within a {@link PortfolioDTO}, enriched with live market value and P&L. */
public class HoldingDTO {

    private String ticker;
    private BigDecimal quantity;
    private BigDecimal avgCostBasis;
    private BigDecimal currentPrice;
    private BigDecimal marketValue;
    private BigDecimal unrealizedPnl;

    public HoldingDTO(String ticker, BigDecimal quantity, BigDecimal avgCostBasis,
                       BigDecimal currentPrice, BigDecimal marketValue, BigDecimal unrealizedPnl) {
        this.ticker = ticker;
        this.quantity = quantity;
        this.avgCostBasis = avgCostBasis;
        this.currentPrice = currentPrice;
        this.marketValue = marketValue;
        this.unrealizedPnl = unrealizedPnl;
    }

    public String getTicker() {
        return ticker;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getAvgCostBasis() {
        return avgCostBasis;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public BigDecimal getMarketValue() {
        return marketValue;
    }

    public BigDecimal getUnrealizedPnl() {
        return unrealizedPnl;
    }
}
