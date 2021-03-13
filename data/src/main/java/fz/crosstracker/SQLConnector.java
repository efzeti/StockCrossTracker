package fz.crosstracker;

import yahoofinance.histquotes.HistoricalQuote;

import java.util.Calendar;
import java.util.List;

public interface SQLConnector {

    // symbol refers to Stock Symbol

    boolean initializeStockTable(String symbol);

    boolean initialDataInsertion(String symbol); // method to be called after initializeStockTable, fills table with historical
//  quotes from last 365 days

    boolean dataInsertion(String symbol, int daysPast);

    boolean updateStockTable(String symbol); // this method will be updating missing data. i.e. DB records end at
//  2021-01-01 but it is 2021-03-13 today, the function will fill missing record from 2021-01-01 to 2021-03-12 (last day
//  the market was open)

    boolean insertHistoricalQuote(HistoricalQuote HQ);

    boolean insertHistoricalQuote(List<HistoricalQuote> HQs);

    // Stock generated from SQL Data, this is faster than connecting to YahooFinance.
    HistoricalQuote getSingleQuote(String symbol); // for latest only

    HistoricalQuote getSingleQuote(String symbol, Calendar date);


    List<HistoricalQuote> getQuotes(String symbol, Calendar fromDate); // for some period


    List<HistoricalQuote> getQuotes(String symbol, Calendar fromDate, Calendar toDate); // for some period




    //TODO Development
    // -> function to check data integrity (delete rows with missing values)
    // -> function to check data continuity (missing records

}
