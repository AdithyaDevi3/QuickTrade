package quicktrade.com.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import quicktrade.com.entity.Stocks;
import quicktrade.com.repository.StockRepository;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class StockService {
    @Autowired
    private StockRepository stockRepository;

//    public List<Object[]> getTopGrowingStocks(int limit) {
//        return stockRepository.findTopGrowingStocks(limit);
//    }
//
//    public List<Stocks> getTopLosingStocks(int limit) {
//        return stockRepository.findTopLosingStocks(limit);
//    }

    public List<Object[]> getTopStockChanges() {
        return stockRepository.findTopStockChanges();
    }
    public List<Object[]> getBottomStockChanges() {
        return stockRepository.findBottomStockChanges();
    }
}
