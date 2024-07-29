package stockMarketApiService;//package stockMarketApiService;
//
////import entity.Stocks;
////import entity.StockDataResponse;
//import netscape.javascript.JSObject;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import org.springframework.web.reactive.function.client.WebClient;
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//
//@Component
//public class ApiCall {
//
//
//
//    WebClient.Builder builder = WebClient.builder();
//
//    @Scheduled(fixedRate = 1000)
//    public void getData(){
//        String data  = builder.build()
//                .get()
//                .uri(getBaseUrl())
//                .retrieve()
//                .bodyToMono(String.class)
//                .block();
//        System.out.println("--------------------------------------");
//        System.out.println(data);
//        System.out.println("--------------------------------------");
//
//    }
//
//    @Bean
//    private String getBaseUrl(){
//
//        // Get yesterday's date
//        LocalDate yesterday = LocalDate.now().minusDays(1);
//
//        // Format the date
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        String formattedDate = yesterday.format(formatter);
//
//        // Create the base URL with yesterday's date
//        return "https://api.polygon.io/v2/aggs/grouped/locale/us/market/stocks/" + formattedDate + "?adjusted=false&apiKey=mjuTBMGVNRgdO10k4_2tbNeNYBRUu8Vb";
//
//    }
//
//}
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ApiCall {

    @Scheduled(fixedRate = 1000) // Executes every 1000 milliseconds (1 second)
    public void reportCurrentTime() {
        System.out.println("The time is now " + System.currentTimeMillis());
    }
}
