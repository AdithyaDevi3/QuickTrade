package quicktrade.com.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import quicktrade.com.config.JwtService;
import quicktrade.com.entity.AuthResponseDTO;
import quicktrade.com.entity.Portfolio;
import quicktrade.com.entity.User;
import quicktrade.com.repository.PortfolioRepository;
import quicktrade.com.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PortfolioRepository portfolioRepository;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerProvisionsPortfolioWithStartingCashAndReturnsToken() {
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(jwtService.generateToken("new@example.com")).thenReturn("jwt-token");

        AuthResponseDTO response = authService.register("new@example.com", "password123");

        assertEquals("jwt-token", response.getToken());
        assertEquals("new@example.com", response.getEmail());

        var portfolioCaptor = org.mockito.ArgumentCaptor.forClass(Portfolio.class);
        verify(portfolioRepository).save(portfolioCaptor.capture());
        assertEquals(AuthService.STARTING_CASH_BALANCE, portfolioCaptor.getValue().getCashBalance());
    }

    @Test
    void registerRejectsDuplicateEmail() {
        when(userRepository.existsByEmail("taken@example.com")).thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> authService.register("taken@example.com", "password123"));
        verify(portfolioRepository, never()).save(any());
    }

    @Test
    void registerRejectsShortPassword() {
        assertThrows(IllegalArgumentException.class,
                () -> authService.register("short@example.com", "short"));
        verifyNoInteractions(userRepository, portfolioRepository);
    }

    @Test
    void loginSucceedsWithCorrectPassword() {
        User user = new User("user@example.com", new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("password123"));
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken("user@example.com")).thenReturn("jwt-token");

        AuthResponseDTO response = authService.login("user@example.com", "password123");

        assertEquals("jwt-token", response.getToken());
    }

    @Test
    void loginRejectsWrongPassword() {
        User user = new User("user@example.com", new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("password123"));
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class,
                () -> authService.login("user@example.com", "wrong-password"));
    }

    @Test
    void loginRejectsUnknownEmail() {
        when(userRepository.findByEmail("ghost@example.com")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> authService.login("ghost@example.com", "password123"));
    }
}
