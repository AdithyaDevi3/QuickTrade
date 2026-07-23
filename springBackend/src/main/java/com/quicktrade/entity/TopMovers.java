package com.quicktrade.entity;

import jakarta.persistence.*;

/**
 * TopMovers entity — persists the daily top-mover snapshot for a single ticker.
 * <p>
 * Stores pre-computed change metrics so the REST layer can serve ranked lists
 * without performing expensive aggregations on every request.
 */
@Entity
@Table(name = "top_movers")
public class TopMovers {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "ticker", nullable = false)
    private String ticker;

    @Column(name = "volume")
    private long volume;

    @Column(name = "price")
    private double price;

    @Column(name = "change_amount")
    private double changeAmount;

    @Column(name = "change_percentage")
    private double changePercent;

    /** Date string (yyyy-MM-dd) for which this snapshot was computed. */
    @Column(name = "record_date")
    private String recordDate;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTicker() { return ticker; }
    public void setTicker(String ticker) { this.ticker = ticker; }

    public long getVolume() { return volume; }
    public void setVolume(long volume) { this.volume = volume; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getChangeAmount() { return changeAmount; }
    public void setChangeAmount(double changeAmount) { this.changeAmount = changeAmount; }

    public double getChangePercent() { return changePercent; }
    public void setChangePercent(double changePercent) { this.changePercent = changePercent; }

    public String getRecordDate() { return recordDate; }
    public void setRecordDate(String recordDate) { this.recordDate = recordDate; }
}
