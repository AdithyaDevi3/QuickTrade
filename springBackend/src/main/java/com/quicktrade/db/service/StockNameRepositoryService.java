package com.quicktrade.db.service;

import com.quicktrade.entity.StockNames;
import com.quicktrade.entity.Stocks;
import com.quicktrade.repository.StockNameRepository;
import com.quicktrade.repository.StockRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StockNameRepositoryService {


    @Autowired
    private StockNameRepository stockRepository;

    @Transactional
    public void save(StockNames stock){
        stockRepository.save(stock);
    }


}
