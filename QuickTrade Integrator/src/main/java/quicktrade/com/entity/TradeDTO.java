package quicktrade.com.entity;

import java.math.BigDecimal;
import java.time.Instant;

public class TradeDTO {

    private String ticker;
    private Trade.Side side;
    private BigDecimal quantity;
    private BigDecimal price;
    private Instant executedAt;

    public TradeDTO(Trade trade) {
        this.ticker = trade.getTicker();
        this.side = trade.getSide();
        this.quantity = trade.getQuantity();
        this.price = trade.getPrice();
        this.executedAt = trade.getExecutedAt();
    }

    public String getTicker() {
        return ticker;
    }

    public Trade.Side getSide() {
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
