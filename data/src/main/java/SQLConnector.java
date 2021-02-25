import yahoofinance.Stock;

public interface SQLConnector {

    void initialDBCreation();

    void initialDBUpdate();

    void dailyDBUpdate();

    // Stock generated from SQL Data
    Stock getStock();


}
