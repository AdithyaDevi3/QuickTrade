package com.quicktrade.stockMarketApiService;//package stockMarketApiService;
//

import com.quicktrade.db.service.RepositoryService;
import com.quicktrade.stockMarketApiService.apiResponse.StockData;
import com.quicktrade.stockMarketApiService.apiResponse.StockDataResponse;
//import com.quicktrade.databaseservice.RepositoryService;

//import entity.StockDataResponse;
//import netscape.javascript.JSObject;
import com.quicktrade.entity.Stocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class ApiCall {

    @Autowired
    RepositoryService repositoryService;

    private final WebClient webClient;

    @Autowired
    public ApiCall(WebClient webClient) {
        this.webClient = webClient;
    }

    @Scheduled(fixedRate = 12000)
    public void getData() {
        StockDataResponse data = webClient.get()
                .uri(getBaseUrl())
                .retrieve()
                .bodyToMono(StockDataResponse.class)
                .block();

       for(StockData stock: data.getResults()){
           Stocks stockEntity = getStocks(stock);
        System.out.println(stockEntity.toString());
           repositoryService.save(stockEntity);
       }
    }

    private static Stocks getStocks(StockData stock) {
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
        return stockEntity;
    }

    @Bean
    private String getBaseUrl(){

        LocalDate yesterday = LocalDate.now().minusDays(1);


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = yesterday.format(formatter);

        return "https://api.polygon.io/v2/aggs/grouped/locale/us/market/stocks/" + formattedDate + "?adjusted=false&apiKey=mjuTBMGVNRgdO10k4_2tbNeNYBRUu8Vb";

    }

}