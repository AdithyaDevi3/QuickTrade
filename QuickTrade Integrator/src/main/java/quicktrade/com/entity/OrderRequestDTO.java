package quicktrade.com.entity;

import java.math.BigDecimal;

/** Request body for POST /api/portfolio/orders. */
public class OrderRequestDTO {

    private String ticker;
    private Trade.Side side;
    private BigDecimal quantity;

    public OrderRequestDTO() {
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public Trade.Side getSide() {
        return side;
    }

    public void setSide(Trade.Side side) {
        this.side = side;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
}
