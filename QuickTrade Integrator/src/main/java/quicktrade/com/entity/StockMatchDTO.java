package quicktrade.com.entity;

public class StockMatchDTO {
    private String symbol;
    private String name;
    private String logoUrl;
    private double matchPercentage;

    public StockMatchDTO(String symbol, String name, String logoUrl, double matchPercentage) {
        this.symbol = symbol;
        this.name = name;
        this.logoUrl = logoUrl;
        this.matchPercentage = matchPercentage;
    }

    // Getters and Setters
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public double getMatchPercentage() { return matchPercentage; }
    public void setMatchPercentage(double matchPercentage) { this.matchPercentage = matchPercentage; }
}
