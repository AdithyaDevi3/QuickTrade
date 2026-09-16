package quicktrade.com.stockMarketApiService;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import quicktrade.com.entity.StockNames;
import quicktrade.com.entity.Stocks;
import quicktrade.com.repository.StockNameRepository;
import quicktrade.com.repository.StockRepository;
import quicktrade.com.stockMarketApiService.apiResponse.stockDataJson.StockData;
import quicktrade.com.stockMarketApiService.apiResponse.stockDataJson.StockDataResponse;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * ApiCall — scheduled data-ingestion component responsible for:
 * <ol>
 *   <li>Fetching daily aggregate stock data from the Polygon.io API.</li>
 *   <li>Reloading the {@code nasdaq.json} stock-names file from the classpath
 *       on a daily schedule so the {@code stock_names} table stays current.</li>
 * </ol>
 *
 * <p>The Polygon.io API key is externalised to {@code application.yml} under
 * the property {@code polygon.api-key} so it never lives in source code.</p>
 */
@Component
public class ApiCall {

    private static final Logger log = LoggerFactory.getLogger(ApiCall.class);

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private StockNameRepository stockNameRepository;

    private final WebClient webClient;
    private LocalDate targetDate = LocalDate.of(2023, 7, 1);
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /** Polygon.io API key — injected from application.yml (polygon.api-key). */
    @Value("${polygon.api-key}")
    private String apiKey;

    @Autowired
    public ApiCall(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Fetches one day's worth of grouped-daily aggregate data from Polygon.io
     * for each weekday between July 2023 and the present.  The method advances
     * {@code targetDate} by one day per invocation so each run ingests exactly
     * one new trading day.
     *
     * <p>Scheduled every weekday morning at 06:30 AM after the stock-names
     * reload has already run.</p>
     */
    @Scheduled(cron = "0 30 6 * * MON-FRI")
    public void getData() {
        if (targetDate.isAfter(LocalDate.now().minusDays(1))) {
            log.info("Polygon.io ingestion is up to date (targetDate={}).", targetDate);
            return;
        }

        String requestUrl = getBaseUrl(targetDate);
        log.info("Fetching Polygon.io data for {}…", targetDate);

        webClient.get()
                .uri(requestUrl)
                .retrieve()
                .bodyToMono(StockDataResponse.class)
                .subscribe(data -> {
                    List<StockData> stockList = data.getResults();
                    if (stockList == null) {
                        log.warn("No data returned for {}. Advancing to next day.", targetDate);
                        targetDate = targetDate.plusDays(1);
                        return;
                    }
                    String formattedDate = targetDate.format(dateFormatter);
                    for (StockData stock : stockList) {
                        stockRepository.save(getStocks(stock, formattedDate));
                    }
                    log.info("Saved {} records for {}.", stockList.size(), formattedDate);
                    targetDate = targetDate.plusDays(1);
                }, error -> {
                    log.error("Error fetching data for {}: {}", targetDate, error.getMessage());
                    targetDate = targetDate.plusDays(1);
                });
    }

    /**
     * Builds the Polygon.io grouped-daily aggregate endpoint URL for the given date.
     *
     * @param date trading date to fetch
     * @return fully-qualified URL string
     */
    private String getBaseUrl(LocalDate date) {
        return "https://api.polygon.io/v2/aggs/grouped/locale/us/market/stocks/"
                + date.format(dateFormatter)
                + "?adjusted=false&apiKey=" + apiKey;
    }

    /**
     * Maps a Polygon.io {@link StockData} DTO to a {@link Stocks} JPA entity.
     *
     * @param stock raw API response object
     * @param date  formatted date string (yyyy-MM-dd) for the trading day
     * @return populated {@link Stocks} entity ready for persistence
     */
    private static Stocks getStocks(StockData stock, String date) {
        Stocks entity = new Stocks();
        entity.setTicker(stock.getTicker());
        entity.setVolume(stock.getVolume());
        entity.setVolumeWeighted(stock.getVolumeWeighted());
        entity.setOpen(stock.getOpen());
        entity.setClose(stock.getClose());
        entity.setHigh(stock.getHigh());
        entity.setLow(stock.getLow());
        entity.setTimestamp(stock.getTimestamp());
        entity.setTransactions(stock.getTransactions());
        entity.setDate(date);
        return entity;
    }

    /**
     * Reloads stock names (symbol + company name + logo URL) from the bundled
     * {@code nasdaq.json} classpath resource into the {@code stock_names} table.
     *
     * <p>Runs once per day at 06:00 AM so new listings from the JSON file are
     * picked up without a full restart. The JSON is loaded from the classpath
     * (not a hardcoded absolute path) so the app works in any environment.</p>
     *
     * @throws IOException if the classpath resource cannot be read
     */
    @Scheduled(cron = "0 0 6 * * *")
    public void saveStocksFromJsonFile() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ClassPathResource resource = new ClassPathResource("nasdaq.json");
        try (InputStream is = resource.getInputStream()) {
            List<StockNames> stockNames = objectMapper.readValue(is,
                    new TypeReference<List<StockNames>>() {});
            stockNameRepository.saveAll(stockNames);
            log.info("Reloaded {} stock names from nasdaq.json.", stockNames.size());
        }
    }
}
