package repository;

import entity.Stocks;
import org.springframework.boot.autoconfigure.integration.IntegrationProperties;

public interface StockRepository implements IntegrationProperties.Jdbc<Stocks, String> {
}
