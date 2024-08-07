package com.quicktrade.stockMarketApiService;//package stockMarketApiService;
//
//
//import com.quicktrade.db.service.RepositoryService;
//import com.quicktrade.stockMarketApiService.apiResponse.stockData.StockData;
//import com.quicktrade.stockMarketApiService.apiResponse.stockData.StockDataResponse;
////import com.quicktrade.databaseservice.RepositoryService;
//
////import entity.StockDataResponse;
////import netscape.javascript.JSObject;
//import com.quicktrade.entity.Stocks;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import org.springframework.web.reactive.function.client.WebClient;
//
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//
//@Component
//public class ApiCall {
//
//    @Autowired
//    RepositoryService repositoryService;
//
//    private final WebClient webClient;
//
//    @Autowired
//    public ApiCall(WebClient webClient) {
//        this.webClient = webClient;
//    }
//
//    @Scheduled(fixedRate = 12000)
//    public void getData() {
//        StockDataResponse data = webClient.get()
//                .uri(getBaseUrl())
//                .retrieve()
//                .bodyToMono(StockDataResponse.class)
//                .block();
//
//       for(StockData stock: data.getResults()){
//           Stocks stockEntity = getStocks(stock);
//        System.out.println(stockEntity.toString());
//           repositoryService.save(stockEntity);
//       }
//    }
//
//    private static Stocks getStocks(StockData stock) {
//        Stocks stockEntity = new Stocks();
//        stockEntity.setTicker(stock.getTicker());
//        stockEntity.setVolume(stock.getVolume());
//        stockEntity.setVolumeWeighted(stock.getVolumeWeighted());
//        stockEntity.setOpen(stock.getOpen());
//        stockEntity.setClose(stock.getClose());
//        stockEntity.setHigh(stock.getHigh());
//        stockEntity.setLow(stock.getLow());
//        stockEntity.setTimestamp(stock.getTimestamp());
//        stockEntity.setTransactions(stock.getTransactions());
//        return stockEntity;
//    }
//
//    @Bean
//    private String getBaseUrl(){
//
//        LocalDate yesterday = LocalDate.now().minusDays(1);
//
//
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        String formattedDate = yesterday.format(formatter);
//
//        return "https://api.polygon.io/v2/aggs/grouped/locale/us/market/stocks/" + formattedDate + "?adjusted=false&apiKey=mjuTBMGVNRgdO10k4_2tbNeNYBRUu8Vb";
//
//    }
//
//}
import com.quicktrade.db.service.RepositoryService;
import com.quicktrade.entity.Stocks;
import com.quicktrade.stockMarketApiService.apiResponse.stockDataJson.StockData;
import com.quicktrade.stockMarketApiService.apiResponse.stockDataJson.StockDataResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class ApiCall {
    @Autowired
    RepositoryService repositoryService;

    private final WebClient webClient;
    private LocalDate targetDate = LocalDate.of(2023, 7, 1); // Start from July 1, 2023
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final String apiKey = "mjuTBMGVNRgdO10k4_2tbNeNYBRUu8Vb"; // Directly included API key

    @Autowired
    public ApiCall(WebClient webClient, RepositoryService repositoryService) {
        this.webClient = webClient;
        this.repositoryService = repositoryService;
    }

    @Scheduled(fixedRate = 12000)
    public void getData() {
        if (targetDate.isAfter(LocalDate.of(2023, 8, 6))) {
            System.out.println("Data fetching completed.");
            return;
        }

        String requestUrl = getBaseUrl(targetDate);

        webClient.get()
                .uri(requestUrl)
                .retrieve()
                .bodyToMono(StockDataResponse.class)
                .subscribe(data -> {
                    List<StockData> stockList = data.getResults();
                    if (stockList == null) {
                        System.out.println("No data for " + targetDate + ". Moving to the previous day.");
                        targetDate = targetDate.plusDays(1);
                        return;
                    }
                    String formattedDate = targetDate.format(dateFormatter);
                    for (StockData stock : stockList) {
                        Stocks stockEntity = getStocks(stock, formattedDate);
                        System.out.println(stockEntity.toString());
                        repositoryService.save(stockEntity);
                    }
                    targetDate = targetDate.plusDays(1);
                }, error -> {
                    System.err.println("Error fetching data for " + targetDate + ": " + error.getMessage());
                    targetDate = targetDate.plusDays(1); // Move to the next day in case of error
                });
    }

    private String getBaseUrl(LocalDate date) {
        String formattedDate = date.format(dateFormatter);
        return "https://api.polygon.io/v2/aggs/grouped/locale/us/market/stocks/" + formattedDate + "?adjusted=false&apiKey=" + apiKey;
    }

    private static Stocks getStocks(StockData stock, String date) {
        Stocks stockEntity = new Stocks();
        stockEntity.setTicker(stock.getTicker());
        stockEntity.setVolume(stock.getVolume());
        stockEntity.setVolumeWeighted(stock.getVolumeWeighted());
        stockEntity.setOpen(stock.getOpen());
        stockEntity.setClose(stock.getClose());
        stockEntity.setHigh(stock.getHigh());
        stockEntity.setLow(stock.getLow());
        stockEntity.setTimestamp(stock.getTimestamp());
        stockEntity.setTransactions(stock.getTransactions());
        stockEntity.setDate(date);

        return stockEntity;
    }
}
