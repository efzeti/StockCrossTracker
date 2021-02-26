package fz.yahoodata;

import yahoofinance.Stock;

import java.util.List;

public interface YahooFinanceDataGetter {

    // for intiial SQL Table Creation

    List<Stock> getInitialData();

    List<Stock> getInitialData(List<String> stockSymbols);

    Stock getInitialData(String stockSymbol);

    // for Updates

    List<Stock> getTodaysData(List<String> stockSymbols);

    List<Stock> getTodaysData();

    Stock getTodaysData(String stockSymbol);




}
