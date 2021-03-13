package fz.crosstracker;

import yahoofinance.Stock;

import java.util.List;

public interface YahooFinanceDataGetter {

    // for intitial SQL Tables Creation

    List<Stock> getDataFromPeriod(List<String> stockSymbols, int daysPast);

    Stock getDataFromPeriod(String stockSymbol, int daysPast);

    // for Updates

    List<Stock> getTodaysData(List<String> stockSymbols);


    Stock getTodaysData(String stockSymbol);

    boolean correctSymbol(String symbol);


}
