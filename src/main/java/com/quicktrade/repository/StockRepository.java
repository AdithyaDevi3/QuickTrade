package com.quicktrade.repository;

import com.quicktrade.entity.Stocks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRepository extends JpaRepository<Stocks, Long> {

}
