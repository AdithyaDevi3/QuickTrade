package quicktrade.com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import quicktrade.com.entity.StockMatchDTO;
import quicktrade.com.entity.Stocks;
import quicktrade.com.service.StockService;

import java.util.List;
import java.util.Map;

@RestController
public class RepositoryController {


        @Autowired
        private StockService stockService;


//        @GetMapping("/top-growing-stocks")
//        public List<Object[]> getTopGrowingStocks(@RequestParam(defaultValue = "5") int limit) {
//            return stockService.getTopGrowingStocks(limit);
//        }
//
//        @GetMapping("/top-losing-stocks")
//        public List<Stocks> getTopLosingStocks(@RequestParam(defaultValue = "5") int limit) {
//            return stockService.getTopLosingStocks(limit);
////        }
//    @GetMapping("/top-growing-stocks")
//    public List<Object[]> getTopGrowingStocks() {
//            return stockService.getTopStockChanges();
//        }
//
//    @GetMapping("/bottom-losing-stocks")
//    public List<Object[]> getBottomLosingStocks() {
//        return stockService.getBottomStockChanges();
//    }

    @GetMapping("/api/search")
    public List<StockMatchDTO> searchStocks(@RequestParam String query) {
        return stockService.searchStocks(query);
    }

}
