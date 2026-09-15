package quicktrade.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import quicktrade.com.entity.Portfolio;
import quicktrade.com.entity.User;

import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    Optional<Portfolio> findByUser(User user);
}
