package stockMarketApiService;

import netscape.javascript.JSObject;

public class ApiCall {
    private String baseUrl = "https://www.alphavantage.co/query?";

    @Autowired
    JSObject jsObject;

    public void getData(){
        jsObject= fetch(baseUrl);
    }
}
