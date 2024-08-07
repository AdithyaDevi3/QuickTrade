package com.quicktrade.entity;


import jakarta.persistence.*;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;


@Entity
@Table(name = "stocks_data")public class Stocks {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id; // Changed to Long for autogenerated primary keys

    @Column(name = "ticker")
    private String ticker;

    @Column(name = "volume")
    private int volume;

    @Column(name = "volume_weighted")
    private double volumeWeighted;

    @Column(name = "open")
    private double open;

    @Column(name = "close")
    private double close;

    @Column(name = "high")
    private double high;

    @Column(name = "low")
    private double low;

    @Column(name = "timestamp")
    private long timestamp;

    @Column(name = "transactions")
    private int transactions;

    @Column(name = "date")
    private String date;

    // Default constructor
    public Stocks() {
    }

    // Parameterized constructor
    public Stocks(String ticker, int volume, double volumeWeighted, double open, double close, double high, double low, long timestamp, int transactions, String date) {
        this.ticker = ticker;
        this.volume = volume;
        this.volumeWeighted = volumeWeighted;
        this.open = open;
        this.close = close;
        this.high = high;
        this.low = low;
        this.timestamp = timestamp;
        this.transactions = transactions;
        this.date = date;
    }

    // Getters and setters
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

    public double getVolumeWeighted() {
        return volumeWeighted;
    }

    public void setVolumeWeighted(double volumeWeighted) {
        this.volumeWeighted = volumeWeighted;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getTransactions() {
        return transactions;
    }

    public void setTransactions(int transactions) {
        this.transactions = transactions;
    }

    @Override
    public String toString() {
        return "Stocks{" +
                "id=" + id +
                ", ticker='" + ticker + '\'' +
                ", volume=" + volume +
                ", volumeWeighted=" + volumeWeighted +
                ", open=" + open +
                ", close=" + close +
                ", high=" + high +
                ", low=" + low +
                ", timestamp=" + timestamp +
                ", transactions=" + transactions +
                '}';
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
