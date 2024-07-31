package com.quicktrade.databaseservice;

import com.quicktrade.entity.Stocks;
import com.quicktrade.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RepositoryService {


    @Autowired
    StockRepository stockRepository;

    public void save(Stocks stocks){
        stockRepository.save(stocks);
    }


}
