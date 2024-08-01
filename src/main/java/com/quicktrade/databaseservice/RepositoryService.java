package com.quicktrade.databaseservice;

import com.quicktrade.entity.Stocks;
import com.quicktrade.repository.StockRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RepositoryService {


    @Autowired
    StockRepository stockRepository;

    @Transactional
    public void save(Stocks stock){
        stockRepository.save(stock);
    }


}
