package fz.yahoodata;

import yahoofinance.Stock;

public interface SQLConnector {

    void initialDBCreation();

    void initialDBUpdate();

    void dailyDBUpdate();

    // Stock generated from SQL Data, this is faster than connecting to YahooFinance.
    Stock getStock();


}
