package fz.yahoodata;

import com.sun.tools.javac.Main;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yahoofinance.Stock;

import java.util.Arrays;
import java.util.List;

class YahooFinanceDataGetterImpTest {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    YahooFinanceDataGetter stockGetter;
    String testSymbol;
    String wrongSymbol;
    List<String> testSymbolList;

    @BeforeEach
    void setUp() {
        stockGetter = new YahooFinanceDataGetterImp();
        testSymbol = "TSLA";
        wrongSymbol = "TSLA@";
        testSymbolList = Arrays.asList("TSLA", "AAPL", "KO");
    }

    //    No point testing this one
//    @Test
//    void testGetInitialDataEmpty() {
//    }

    @Test
    void testGetInitialDataString() {
        Stock stock = stockGetter.getInitialData(testSymbol);

        Assertions.assertNotNull(stock.getQuote().getAsk());
    }

    @Test
    void testGetInitialDataWrongString() {
        Stock stock = stockGetter.getInitialData(wrongSymbol);

        Assertions.assertNull(stock);
    }

    @Test
    void testGetInitialDataList() {
        List<Stock> stocks = stockGetter.getInitialData(testSymbolList);

        Assertions.assertEquals(stocks.size(),3);
    }

//    No point testing this
//    @Test
//    void testGetTodaysDataEmpty() {
//    }

    @Test
    void testGetTodaysDataString() {
        Stock stock = stockGetter.getTodaysData(testSymbol);

        Assertions.assertNotNull(stock.getQuote().getAsk());
    }

    @Test
    void testGetTodaysDataWrongString() {
        Stock stock = stockGetter.getInitialData(wrongSymbol);

        Assertions.assertNull(stock);
    }

    @Test
    void testGetTodaysDataList() {
        List<Stock> stocks = stockGetter.getTodaysData(testSymbolList);

        Assertions.assertEquals(stocks.size(),3);
    }
}