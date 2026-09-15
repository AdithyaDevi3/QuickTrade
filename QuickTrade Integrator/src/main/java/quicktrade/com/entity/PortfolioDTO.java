package quicktrade.com.entity;

import java.math.BigDecimal;
import java.util.List;

public class PortfolioDTO {

    private BigDecimal cashBalance;
    private BigDecimal holdingsValue;
    private BigDecimal totalValue;
    private List<HoldingDTO> holdings;

    public PortfolioDTO(BigDecimal cashBalance, BigDecimal holdingsValue,
                         BigDecimal totalValue, List<HoldingDTO> holdings) {
        this.cashBalance = cashBalance;
        this.holdingsValue = holdingsValue;
        this.totalValue = totalValue;
        this.holdings = holdings;
    }

    public BigDecimal getCashBalance() {
        return cashBalance;
    }

    public BigDecimal getHoldingsValue() {
        return holdingsValue;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public List<HoldingDTO> getHoldings() {
        return holdings;
    }
}
