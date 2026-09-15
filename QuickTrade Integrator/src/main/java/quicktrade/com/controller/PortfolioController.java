package quicktrade.com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import quicktrade.com.entity.OrderRequestDTO;
import quicktrade.com.service.TradingService;

import java.util.List;

/** Paper-trading endpoints — all require a valid JWT (see {@link quicktrade.com.config.JwtAuthFilter}). */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/portfolio")
public class PortfolioController {

    @Autowired
    private TradingService tradingService;

    @GetMapping
    public ResponseEntity<?> getPortfolio(Authentication authentication) {
        return ResponseEntity.ok(tradingService.getPortfolioSummary(authentication.getName()));
    }

    @GetMapping("/trades")
    public ResponseEntity<?> getTrades(Authentication authentication) {
        return ResponseEntity.ok(tradingService.getTradeHistory(authentication.getName()));
    }

    @PostMapping("/orders")
    public ResponseEntity<?> placeOrder(Authentication authentication, @RequestBody OrderRequestDTO order) {
        try {
            return ResponseEntity.ok(tradingService.placeOrder(
                    authentication.getName(), order.getTicker(), order.getSide(), order.getQuantity()));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
