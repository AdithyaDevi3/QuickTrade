package quicktrade.com.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import quicktrade.com.config.JwtService;
import quicktrade.com.entity.AuthResponseDTO;
import quicktrade.com.entity.Portfolio;
import quicktrade.com.entity.User;
import quicktrade.com.repository.PortfolioRepository;
import quicktrade.com.repository.UserRepository;

import java.math.BigDecimal;

/** Handles registration and login, including provisioning the starting paper-trading portfolio. */
@Service
public class AuthService {

    /** Starting virtual cash balance granted to every new portfolio. */
    public static final BigDecimal STARTING_CASH_BALANCE = new BigDecimal("100000.00");

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PortfolioRepository portfolioRepository;
    @Autowired
    private JwtService jwtService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthResponseDTO register(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.length() < 8) {
            throw new IllegalArgumentException("Email is required and password must be at least 8 characters");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException("An account with this email already exists");
        }

        User user = new User(email, passwordEncoder.encode(password));
        user = userRepository.save(user);

        Portfolio portfolio = new Portfolio(user, STARTING_CASH_BALANCE);
        portfolioRepository.save(portfolio);

        return new AuthResponseDTO(jwtService.generateToken(user.getEmail()), user.getEmail());
    }

    public AuthResponseDTO login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        return new AuthResponseDTO(jwtService.generateToken(user.getEmail()), user.getEmail());
    }
}
