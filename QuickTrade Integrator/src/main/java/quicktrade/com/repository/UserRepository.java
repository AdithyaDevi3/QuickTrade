package quicktrade.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import quicktrade.com.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
