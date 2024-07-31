package com.quicktrade.entity;//package com.quicktrade.entity;
//
//    public class StockData {
//        private String T;
//        private double c;
//        private double h;
//        private double l;
//        private int n;
//        private double o;
//        private long t;
//        private int v;
//        private double vw;
//
//    public String getT(){
//        return T;
//    }
//
//        public void setT(String T) {
//            this.T = T;
//        }
//
//        public double getC() {
//            return c;
//        }
//
//        public void setC(double c) {
//            this.c = c;
//        }
//
//        public double getH() {
//            return h;
//        }
//
//        public void setH(double h) {
//            this.h = h;
//        }
//
//        public double getL() {
//            return l;
//        }
//
//        public void setL(double l) {
//            this.l = l;
//        }
//
//        public int getN() {
//            return n;
//        }
//
//        public void setN(int n) {
//            this.n = n;
//        }
//
//        public double getO() {
//            return o;
//        }
//
//        public void setO(double o) {
//            this.o = o;
//        }
//
//        public long getSmallT() {
//            return t;
//        }
//
//        public void setSmallT(long t) {
//            this.t = t;
//        }
//
//        public int getV() {
//            return v;
//        }
//
//        public void setV(int v) {
//            this.v = v;
//        }
//
//        public double getVw() {
//            return vw;
//        }
//
//        public void setVw(double vw) {
//            this.vw = vw;
//        }
//    }
//
//

import com.fasterxml.jackson.annotation.JsonProperty;

public class StockData {
    @JsonProperty("T")
    private String ticker;
    @JsonProperty("v")
    private int volume;
    @JsonProperty("vw")
    private double volumeWeighted;
    @JsonProperty("o")
    private double open;
    @JsonProperty("c")
    private double close;
    @JsonProperty("h")
    private double high;
    @JsonProperty("l")
    private double low;
    @JsonProperty("t")
    private long timestamp;
    @JsonProperty("n")
    private int transactions;

    // Getters and Setters
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
}
