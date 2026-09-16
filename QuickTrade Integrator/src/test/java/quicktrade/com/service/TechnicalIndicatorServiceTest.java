package quicktrade.com.service;

import org.junit.jupiter.api.Test;
import quicktrade.com.entity.Stocks;
import quicktrade.com.service.TechnicalIndicatorService.BollingerResult;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TechnicalIndicatorServiceTest {

    private final TechnicalIndicatorService service = new TechnicalIndicatorService();

    private List<Stocks> closes(double... values) {
        List<Stocks> history = new ArrayList<>();
        for (double v : values) {
            history.add(new Stocks("TEST", 1000, v, v, v, v, v, 0, 1, "2024-01-01"));
        }
        return history;
    }

    @Test
    void smaAveragesTheMostRecentPeriod() {
        List<Stocks> history = closes(10, 20, 30, 40, 50);

        Double sma3 = service.calculateSMA(history, 3);

        assertEquals(40.0, sma3); // avg(30, 40, 50)
    }

    @Test
    void smaReturnsNullWhenInsufficientData() {
        assertNull(service.calculateSMA(closes(10, 20), 5));
    }

    @Test
    void rsiIsHundredWhenNoLosses() {
        // strictly increasing closes -> zero average loss -> RSI = 100
        List<Stocks> history = closes(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);

        Double rsi = service.calculateRSI(history, 14);

        assertEquals(100.0, rsi);
    }

    @Test
    void rsiReturnsNullWhenInsufficientData() {
        assertNull(service.calculateRSI(closes(1, 2, 3), 14));
    }

    @Test
    void bollingerBandsWidenWithVolatility() {
        List<Stocks> flat = closes(50, 50, 50, 50, 50, 50, 50, 50, 50, 50,
                50, 50, 50, 50, 50, 50, 50, 50, 50, 50);
        List<Stocks> volatile_ = closes(40, 60, 40, 60, 40, 60, 40, 60, 40, 60,
                40, 60, 40, 60, 40, 60, 40, 60, 40, 60);

        BollingerResult flatBands = service.calculateBollingerBands(flat, 20, 2.0);
        BollingerResult volatileBands = service.calculateBollingerBands(volatile_, 20, 2.0);

        assertEquals(50.0, flatBands.upper());
        assertEquals(50.0, flatBands.lower());
        assertTrue(volatileBands.upper() > volatileBands.middle());
        assertTrue(volatileBands.lower() < volatileBands.middle());
    }

    @Test
    void macdReturnsNullForShortHistory() {
        assertNull(service.calculateMACD(closes(new double[10])));
    }
}
