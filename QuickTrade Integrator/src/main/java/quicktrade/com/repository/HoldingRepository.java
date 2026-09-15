package quicktrade.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import quicktrade.com.entity.Holding;
import quicktrade.com.entity.Portfolio;

import java.util.List;
import java.util.Optional;

public interface HoldingRepository extends JpaRepository<Holding, Long> {
    List<Holding> findByPortfolio(Portfolio portfolio);
    Optional<Holding> findByPortfolioAndTicker(Portfolio portfolio, String ticker);
}
