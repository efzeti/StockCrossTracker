package fz.yahoodata;

import com.sun.tools.javac.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.Interval;

import java.io.BufferedReader;
import java.io.FileReader;
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

    private static String crossDate = null;

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");



    @Override
    public List<Stock> getInitialData() {
        List<String> stockList = makeStkListFromFile(REVOLUT_STOCK_PATH);
        return getInitialData(stockList);
    }

    @Override
    public List<Stock> getInitialData(List<String> stockSymbols) {

        List<Stock> stocks = new ArrayList<>();


        stockSymbols.parallelStream().forEach( symbol -> {

            System.out.format("STK Symbol: %-4s has started.\n",symbol);

            Calendar from = Calendar.getInstance();
            Calendar to = Calendar.getInstance();

            from.add(Calendar.DAY_OF_MONTH, -201);


            Stock stock = null;
            try {
                stock = YahooFinance.get(symbol, from, to, Interval.DAILY);
            } catch (IOException e) {
                System.out.println(ANSI_RED + symbol + " couldn't be initialized." + ANSI_RESET);
            }

            if (stock != null){
                stocks.add(stock);
            }

            System.out.format("STK Symbol: %-4s has finished.\n",symbol);

        });


        return stocks;

    }

    @Override
    public Stock getInitialData(String stockSymbol) {

        try {
            return getInitialData(Collections.singletonList(stockSymbol)).get(0);
        } catch (IndexOutOfBoundsException e){
            return null;
        }

    }

    @Override
    public List<Stock> getTodaysData(List<String> stockSymbols) {
        List<Stock> stocks = new ArrayList<>();


        stockSymbols.parallelStream().forEach( symbol -> {

            System.out.format("STK Symbol: %-4s has started.\n",symbol);


            Stock stock = null;
            try {
                stock = YahooFinance.get(symbol);
            } catch (IOException e) {
                System.out.println(ANSI_RED + symbol + " couldn't be initialized." + ANSI_RESET);
            }

            if (stock != null){
                stocks.add(stock);
            }

            System.out.format("STK Symbol: %-4s has finished.\n",symbol);

        });


        return stocks;
    }

    @Override
    public List<Stock> getTodaysData() {
        List<String> stockList = makeStkListFromFile(REVOLUT_STOCK_PATH);
        return getTodaysData(stockList);
    }

    @Override
    public Stock getTodaysData(String stockSymbol) {

        try {
            return getTodaysData(Collections.singletonList(stockSymbol)).get(0);
        } catch (IndexOutOfBoundsException e){
            return null;
        }

    }

    private static List<String> makeStkListFromFile(String filePath){

        List<String> stkList = new ArrayList<>();

        try(BufferedReader br = new BufferedReader(new FileReader(filePath))){

            String stkLine = br.readLine();
            while(stkLine != null){
                stkList.add(stkLine);
                stkLine = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stkList;

    }


}
