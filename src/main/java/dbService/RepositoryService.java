package dbService;

import repository.StockRepository;
import entity.Stocks;
import stockMarketApiService.ApiCall;

public class RepositoryService {


    @Autowired
    StockRepository stockRepository;

    public void save(Stocks stocks){
        stockRepository.save(stocks);
    }


}
