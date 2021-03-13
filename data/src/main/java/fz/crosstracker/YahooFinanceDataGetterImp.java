package fz.crosstracker;

import com.sun.tools.javac.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.Interval;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class YahooFinanceDataGetterImp implements YahooFinanceDataGetter {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    private final String REVOLUT_STOCK_PATH = "data\\src\\main\\resources\\revolut_stocks.txt";

    private String crossDate = null;

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");



    @Override
    public List<Stock> getDataFromPeriod(List<String> stockSymbols, int daysPast) {


        List<Stock> stocks = new ArrayList<>();


        stockSymbols.parallelStream().forEach( symbol -> {


            Calendar from = Calendar.getInstance();
            Calendar to = Calendar.getInstance();

            from.add(Calendar.DAY_OF_MONTH, ((-1) * daysPast));


            Stock stock = null;
            try {
                stock = YahooFinance.get(symbol, from, to, Interval.DAILY);
            } catch (IOException e) {
                System.out.println(ANSI_RED + symbol + " couldn't be initialized." + ANSI_RESET);
            }

            if (stock != null){
                stocks.add(stock);
            }

            System.out.format("Retrieving online data for %s has finished.\n",symbol);

        });


        return stocks;

    }

    @Override
    public Stock getDataFromPeriod(String stockSymbol, int daysPast) {

        try {
            return getDataFromPeriod(Collections.singletonList(stockSymbol), daysPast).get(0);
        } catch (IndexOutOfBoundsException e){
            return null;
        }

    }

    @Override
    public List<Stock> getTodaysData(List<String> stockSymbols) {
        List<Stock> stocks = new ArrayList<>();


        stockSymbols.parallelStream().forEach( symbol -> {


            Stock stock = null;
            try {
                stock = YahooFinance.get(symbol);
            } catch (IOException e) {
                System.out.println(ANSI_RED + symbol + " couldn't be initialized." + ANSI_RESET);
            }

            if (stock != null){
                stocks.add(stock);
            }

            System.out.format("Retrieving online data for %s has finished.\n",symbol);

        });


        return stocks;
    }


    @Override
    public Stock getTodaysData(String stockSymbol) {

        try {
            return getTodaysData(Collections.singletonList(stockSymbol)).get(0);
        } catch (IndexOutOfBoundsException e){
            return null;
        }

    }

    @Override
    public boolean correctSymbol(String symbol) {

        return symbol.matches("[a-zA-Z]+") && (symbol.length() < 6 && symbol.length() > 0);
    }

}
