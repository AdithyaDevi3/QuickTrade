package quicktrade.com.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import quicktrade.com.entity.*;
import quicktrade.com.repository.HoldingRepository;
import quicktrade.com.repository.PortfolioRepository;
import quicktrade.com.repository.TradeRepository;
import quicktrade.com.repository.UserRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TradingServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PortfolioRepository portfolioRepository;
    @Mock
    private HoldingRepository holdingRepository;
    @Mock
    private TradeRepository tradeRepository;
    @Mock
    private StockService stockService;

    @InjectMocks
    private TradingService tradingService;

    private User user;
    private Portfolio portfolio;

    private void givenUserWithCash(String cash) {
        user = new User("trader@example.com", "hash");
        portfolio = new Portfolio(user, new BigDecimal(cash));
        when(userRepository.findByEmail("trader@example.com")).thenReturn(Optional.of(user));
        when(portfolioRepository.findByUser(user)).thenReturn(Optional.of(portfolio));
    }

    private void givenPrice(String ticker, double price) {
        when(stockService.getMetrics(ticker)).thenReturn(
                new StockMetricsDTO(ticker, price, price, price, price, 1000, price, price, 0, 0));
    }

    @Test
    void buyWithSufficientCashCreatesHoldingAndDeductsCash() {
        givenUserWithCash("10000.00");
        givenPrice("AAPL", 100.0);
        when(tradeRepository.save(any(Trade.class))).thenAnswer(inv -> inv.getArgument(0));

        TradeDTO trade = tradingService.placeOrder("trader@example.com", "AAPL", Trade.Side.BUY, new BigDecimal("10"));

        assertEquals(Trade.Side.BUY, trade.getSide());
        assertEquals(0, new BigDecimal("9000.00").compareTo(portfolio.getCashBalance()));
        verify(holdingRepository).save(argThat(h ->
                h.getTicker().equals("AAPL")
                        && h.getQuantity().compareTo(new BigDecimal("10")) == 0
                        && h.getAvgCostBasis().compareTo(new BigDecimal("100.0")) == 0));
    }

    @Test
    void buyWithInsufficientCashThrows() {
        givenUserWithCash("500.00");
        givenPrice("AAPL", 100.0);

        assertThrows(IllegalStateException.class,
                () -> tradingService.placeOrder("trader@example.com", "AAPL", Trade.Side.BUY, new BigDecimal("10")));
        verify(holdingRepository, never()).save(any());
        verify(portfolioRepository, never()).save(any());
    }

    @Test
    void secondBuyAveragesCostBasis() {
        givenUserWithCash("100000.00");
        givenPrice("AAPL", 200.0);
        Holding existing = new Holding(portfolio, "AAPL", new BigDecimal("10"), new BigDecimal("100.0000"));
        when(holdingRepository.findByPortfolioAndTicker(portfolio, "AAPL")).thenReturn(Optional.of(existing));
        when(tradeRepository.save(any(Trade.class))).thenAnswer(inv -> inv.getArgument(0));

        tradingService.placeOrder("trader@example.com", "AAPL", Trade.Side.BUY, new BigDecimal("10"));

        // (10 @ 100) + (10 @ 200) = 20 shares @ avg 150
        assertEquals(0, new BigDecimal("20").compareTo(existing.getQuantity()));
        assertEquals(0, new BigDecimal("150.0000").compareTo(existing.getAvgCostBasis()));
    }

    @Test
    void sellReducesHoldingAndAddsCash() {
        givenUserWithCash("1000.00");
        givenPrice("AAPL", 100.0);
        Holding existing = new Holding(portfolio, "AAPL", new BigDecimal("10"), new BigDecimal("80.0000"));
        when(holdingRepository.findByPortfolioAndTicker(portfolio, "AAPL")).thenReturn(Optional.of(existing));
        when(tradeRepository.save(any(Trade.class))).thenAnswer(inv -> inv.getArgument(0));

        TradeDTO trade = tradingService.placeOrder("trader@example.com", "AAPL", Trade.Side.SELL, new BigDecimal("4"));

        assertEquals(Trade.Side.SELL, trade.getSide());
        assertEquals(0, new BigDecimal("1400.00").compareTo(portfolio.getCashBalance()));
        assertEquals(0, new BigDecimal("6").compareTo(existing.getQuantity()));
        verify(holdingRepository).save(existing);
    }

    @Test
    void sellingAllSharesDeletesHolding() {
        givenUserWithCash("0.00");
        givenPrice("AAPL", 100.0);
        Holding existing = new Holding(portfolio, "AAPL", new BigDecimal("5"), new BigDecimal("80.0000"));
        when(holdingRepository.findByPortfolioAndTicker(portfolio, "AAPL")).thenReturn(Optional.of(existing));
        when(tradeRepository.save(any(Trade.class))).thenAnswer(inv -> inv.getArgument(0));

        tradingService.placeOrder("trader@example.com", "AAPL", Trade.Side.SELL, new BigDecimal("5"));

        verify(holdingRepository).delete(existing);
        verify(holdingRepository, never()).save(any());
    }

    @Test
    void sellWithInsufficientSharesThrows() {
        givenUserWithCash("1000.00");
        givenPrice("AAPL", 100.0);
        when(holdingRepository.findByPortfolioAndTicker(portfolio, "AAPL")).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class,
                () -> tradingService.placeOrder("trader@example.com", "AAPL", Trade.Side.SELL, new BigDecimal("1")));
    }

    @Test
    void unknownTickerThrows() {
        givenUserWithCash("1000.00");
        when(stockService.getMetrics("ZZZZ")).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> tradingService.placeOrder("trader@example.com", "ZZZZ", Trade.Side.BUY, new BigDecimal("1")));
    }

    @Test
    void nonPositiveQuantityThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> tradingService.placeOrder("trader@example.com", "AAPL", Trade.Side.BUY, BigDecimal.ZERO));
    }
}
