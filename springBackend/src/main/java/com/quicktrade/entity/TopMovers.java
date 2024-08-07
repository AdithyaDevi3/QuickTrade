package com.quicktrade.entity;

import jakarta.persistence.*;

public class TopMovers {

    @Entity
    @Table(name = "stocks_data")
    public class Stocks {

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long id; // Changed to Long for autogenerated primary keys

        @Column(name = "ticker")
        private String ticker;

        @Column(name = "volume")
        private int volume;

        @Column(name = "price")
        private double price;

        @Column(name = "change_amount")
        private double changeAmount;

        @Column(name = "change_percentage")
        private double changePercent;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getTicker() {
            return ticker;
        }

        public void setTicker(String ticker) {
            this.ticker = ticker;
        }

        public int getVolume() {
            return volume;
        }

        public void setVolume(int volume) {
            this.volume = volume;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public double getChangeAmount() {
            return changeAmount;
        }

        public void setChangeAmount(double changeAmount) {
            this.changeAmount = changeAmount;
        }

        public double getChangePercent() {
            return changePercent;
        }

        public void setChangePercent(double changePercent) {
            this.changePercent = changePercent;
        }
    }
}