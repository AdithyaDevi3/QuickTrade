package quicktrade.com.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import quicktrade.com.entity.StockMatchDTO;
import quicktrade.com.entity.StockNames;
import quicktrade.com.entity.Stocks;
import quicktrade.com.repository.StockNameRepository;
import quicktrade.com.repository.StockRepository;

import java.util.ArrayList;
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
//
//    public List<Object[]> getTopStockChanges() {
//        return stockRepository.findTopStockChanges();
//    }
//    public List<Object[]> getBottomStockChanges() {
//        return stockRepository.findBottomStockChanges();
//    }
//


    @Autowired
    private StockNameRepository stockNameRepository;

    public List<StockMatchDTO> searchStocks(String query) {
        List<StockNames> symbolMatches = stockNameRepository.findBySymbolContainingIgnoreCase(query);
        List<StockNames> nameMatches = stockNameRepository.findByNameContainingIgnoreCase(query);

        List<StockMatchDTO> result = new ArrayList<>();

        for (StockNames stock : symbolMatches) {
            double matchPercentage = calculateMatchPercentage(query, stock.getSymbol());
            if (matchPercentage > 0) {
                result.add(new StockMatchDTO(stock.getSymbol(), stock.getName(), stock.getLogoUrl(), matchPercentage));
            }
        }

        for (StockNames stock : nameMatches) {
            double matchPercentage = calculateMatchPercentage(query, stock.getName());
            if (matchPercentage > 0 && result.stream().noneMatch(s -> s.getSymbol().equals(stock.getSymbol()))) {
                result.add(new StockMatchDTO(stock.getSymbol(), stock.getName(), stock.getLogoUrl(), matchPercentage));
            }
        }

        return result;
    }

    private double calculateMatchPercentage(String query, String target) {
        // Simple example: calculate percentage of matching characters
        int matches = 0;
        for (int i = 0; i < Math.min(query.length(), target.length()); i++) {
            if (query.charAt(i) == target.charAt(i)) {
                matches++;
            }
        }
        return ((double) matches / target.length()) * 100;
    }
}
