import yahoofinance.Stock;

import java.util.List;

public interface YahooFinanceDataGetter {

    List<Stock> initialDataGet();

    List<Stock> dailyDataGet();

    Stock getTodaysData(String symbol);


}
