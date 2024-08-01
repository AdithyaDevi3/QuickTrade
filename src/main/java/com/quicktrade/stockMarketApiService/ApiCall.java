package com.quicktrade.stockMarketApiService;//package stockMarketApiService;
//

import com.quicktrade.databaseservice.RepositoryService;
import com.quicktrade.repository.StockRepository;
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
    //@Scheduled(cron = "0 0 2 * * *")
    @Scheduled(fixedRate = 60000)
    public void getData() {
        StockDataResponse data = webClient.get()
                .uri(getBaseUrl())
                .retrieve()
                .bodyToMono(StockDataResponse.class)
                .block();

       for(StockData stock: data.getResults()){
           Stocks stockEntity = new Stocks(
                   stock.getTicker(),
                   stock.getVolume(),
                   stock.getVolumeWeighted(),
                   stock.getOpen(),
                   stock.getClose(),
                   stock.getHigh(),
                   stock.getLow(),
                   stock.getTimestamp(),
                   stock.getTransactions()
           );
        System.out.println(stockEntity.toString());
           repositoryService.save(stockEntity);
       }


//
//         System.out.println("--------------------------------------");
//        System.out.println(data.toString());
//        System.out.println("--------------------------------------");
    }

    @Bean
    private String getBaseUrl(){

        // Get yesterday's date
        LocalDate yesterday = LocalDate.now().minusDays(1);

        // Format the date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = yesterday.format(formatter);

        // Create the base URL with yesterday's date
        return "https://api.polygon.io/v2/aggs/grouped/locale/us/market/stocks/" + formattedDate + "?adjusted=false&apiKey=mjuTBMGVNRgdO10k4_2tbNeNYBRUu8Vb";

    }

}