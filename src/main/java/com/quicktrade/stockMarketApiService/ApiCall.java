package com.quicktrade.stockMarketApiService;//package stockMarketApiService;
//

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quicktrade.entity.StockData;
import com.quicktrade.entity.StockDataResponse;
//import entity.StockDataResponse;
//import netscape.javascript.JSObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class ApiCall {


    private final WebClient webClient;

    @Autowired
    public ApiCall(WebClient webClient) {
        this.webClient = webClient;
    }
    @Scheduled(fixedRate = 20000)
    public void getData() {
        StockDataResponse data = webClient.get()
                .uri(getBaseUrl())
                .retrieve()
                .bodyToMono(StockDataResponse.class)
                .block();

       System.out.println( data.getResults().get(1).getTicker());
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