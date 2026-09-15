package quicktrade.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import quicktrade.com.entity.Portfolio;
import quicktrade.com.entity.Trade;

import java.util.List;

public interface TradeRepository extends JpaRepository<Trade, Long> {
    List<Trade> findByPortfolioOrderByExecutedAtDesc(Portfolio portfolio);
}
