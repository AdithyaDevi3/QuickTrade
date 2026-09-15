package quicktrade.com.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quicktrade.com.entity.*;
import quicktrade.com.repository.HoldingRepository;
import quicktrade.com.repository.PortfolioRepository;
import quicktrade.com.repository.TradeRepository;
import quicktrade.com.repository.UserRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/** Executes paper-trading buy/sell orders and reports portfolio state, all priced off the latest daily close. */
@Service
public class TradingService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PortfolioRepository portfolioRepository;
    @Autowired
    private HoldingRepository holdingRepository;
    @Autowired
    private TradeRepository tradeRepository;
    @Autowired
    private StockService stockService;

    public Portfolio getPortfolioForUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found"));
        return portfolioRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("Portfolio not found for user"));
    }

    public PortfolioDTO getPortfolioSummary(String email) {
        Portfolio portfolio = getPortfolioForUser(email);
        List<Holding> holdings = holdingRepository.findByPortfolio(portfolio);

        List<HoldingDTO> holdingDTOs = new ArrayList<>();
        BigDecimal holdingsValue = BigDecimal.ZERO;
        for (Holding holding : holdings) {
            BigDecimal currentPrice = currentPrice(holding.getTicker());
            BigDecimal marketValue = currentPrice.multiply(holding.getQuantity()).setScale(4, RoundingMode.HALF_UP);
            BigDecimal costValue = holding.getAvgCostBasis().multiply(holding.getQuantity());
            BigDecimal unrealizedPnl = marketValue.subtract(costValue).setScale(4, RoundingMode.HALF_UP);
            holdingsValue = holdingsValue.add(marketValue);
            holdingDTOs.add(new HoldingDTO(holding.getTicker(), holding.getQuantity(), holding.getAvgCostBasis(),
                    currentPrice, marketValue, unrealizedPnl));
        }

        BigDecimal totalValue = portfolio.getCashBalance().add(holdingsValue);
        return new PortfolioDTO(portfolio.getCashBalance(), holdingsValue, totalValue, holdingDTOs);
    }

    public List<TradeDTO> getTradeHistory(String email) {
        Portfolio portfolio = getPortfolioForUser(email);
        return tradeRepository.findByPortfolioOrderByExecutedAtDesc(portfolio)
                .stream().map(TradeDTO::new).toList();
    }

    @Transactional
    public TradeDTO placeOrder(String email, String ticker, Trade.Side side, BigDecimal quantity) {
        if (ticker == null || ticker.isBlank()) {
            throw new IllegalArgumentException("Ticker is required");
        }
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        ticker = ticker.toUpperCase();

        Portfolio portfolio = getPortfolioForUser(email);
        BigDecimal price = currentPrice(ticker);
        BigDecimal cost = price.multiply(quantity);

        Holding holding = holdingRepository.findByPortfolioAndTicker(portfolio, ticker).orElse(null);

        if (side == Trade.Side.BUY) {
            if (portfolio.getCashBalance().compareTo(cost) < 0) {
                throw new IllegalStateException("Insufficient cash balance for this order");
            }
            portfolio.setCashBalance(portfolio.getCashBalance().subtract(cost));

            if (holding == null) {
                holding = new Holding(portfolio, ticker, quantity, price);
            } else {
                BigDecimal totalCost = holding.getAvgCostBasis().multiply(holding.getQuantity()).add(cost);
                BigDecimal totalQuantity = holding.getQuantity().add(quantity);
                holding.setQuantity(totalQuantity);
                holding.setAvgCostBasis(totalCost.divide(totalQuantity, 4, RoundingMode.HALF_UP));
            }
            holdingRepository.save(holding);
        } else {
            if (holding == null || holding.getQuantity().compareTo(quantity) < 0) {
                throw new IllegalStateException("Insufficient shares to sell");
            }
            portfolio.setCashBalance(portfolio.getCashBalance().add(cost));
            BigDecimal remaining = holding.getQuantity().subtract(quantity);
            if (remaining.compareTo(BigDecimal.ZERO) == 0) {
                holdingRepository.delete(holding);
            } else {
                holding.setQuantity(remaining);
                holdingRepository.save(holding);
            }
        }

        portfolioRepository.save(portfolio);
        Trade trade = tradeRepository.save(new Trade(portfolio, ticker, side, quantity, price));
        return new TradeDTO(trade);
    }

    private BigDecimal currentPrice(String ticker) {
        StockMetricsDTO metrics = stockService.getMetrics(ticker);
        if (metrics == null) {
            throw new IllegalArgumentException("Unknown ticker or no market data available: " + ticker);
        }
        return BigDecimal.valueOf(metrics.getCurrentPrice());
    }
}
