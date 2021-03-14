package fz.crosstracker;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class SQLConnectorImpl implements SQLConnector{

    private final SimpleDateFormat DATE_FORMAT = YahooFinanceDataGetterImp.DATE_FORMAT;

    private final String REVOLUT_STOCK_PATH = "data\\src\\main\\resources\\revolut_stocks.txt";
    private final String XTB_US_STOCK_PATH = "data\\src\\main\\resources\\xtb_stocks_us.txt";
    private final String DB_URL = "jdbc:sqlite:data\\src\\main\\resources\\stocksDB.db";
    private final String TABLE_CREATION = "CREATE TABLE %s (symbol TEXT, date TEXT PRIMARY KEY, open DOUBLE, " +
            "low DOUBLE, high DOUBLE, close DOUBLE, adjClose DOUBLE, volume BIGINT);";

    private final String QUOTE_INSERTION = "INSERT INTO %s (symbol, date, open , low, high, close, adjClose, volume)" +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

    private final String SINGLE_QUOTE_EXTRACTION = "SELECT * FROM %s WHERE %s = ?";

    private final String SINGLE_QUOTE_EXTRACTION_LATEST = "SELECT * FROM %s ORDER BY date DESC LIMIT 1";

    private final String SINGLE_QUOTE_EXTRACTION_DATE = "SELECT * FROM %s WHERE date = ?";


    private int INITIAL_DATA_PERIOD = 365;



    private final YahooFinanceDataGetter dataGetter = new YahooFinanceDataGetterImp();

    private String dbURL;

    public SQLConnectorImpl() {
        dbURL = DB_URL;
    }

    public SQLConnectorImpl(String dbURL) {
        this.dbURL = dbURL;
    }

    public String getDbURL() {
        return dbURL;
    }

    public void setDbURL(String dbURL) {
        this.dbURL = dbURL;
    }

    public void setDefaultDbURL(String dbURL) {
        this.dbURL = DB_URL;
    }



    @Override
    public boolean initializeStockTable(String symbol){

        if (!dataGetter.correctSymbol(symbol)){
            System.out.println("Symbol " + symbol + " incorrect.");
            return false;
        }


        try (Connection conn = DriverManager.getConnection(dbURL)){

            System.out.println("Connected to DB!");

            if (tableExists(symbol)){
                System.out.println("Table " + symbol.toUpperCase() + " already exist.");
                return false;
            }

            PreparedStatement tableCreationStatement = conn.prepareStatement(String.format(TABLE_CREATION, symbol.toUpperCase()));

            tableCreationStatement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }


        return true;
    }

    @Override
    public boolean initialDataInsertion(String symbol) {
        return dataInsertion(symbol, INITIAL_DATA_PERIOD);
    }

    @Override
    public boolean dataInsertion(String symbol, int daysPast) {

        if (!tableExists(symbol)){
            System.out.println("Table " + symbol.toUpperCase() + " does not exist.");
            return false;
        }

        Stock stock = dataGetter.getDataFromPeriod(symbol.toUpperCase(), daysPast);

        if (stock == null){
            System.out.println("Stock " + symbol.toUpperCase() + " not initialized properly");
            return false;
        }

        try {
            insertHistoricalQuote(stock.getHistory());
        } catch (IOException e){
            System.out.println("History for " + symbol.toUpperCase() + " couldn't be initialized.");
            return false;
        }

        return true;
    }


    @Override
    public boolean insertHistoricalQuote(HistoricalQuote HQ) {
        return insertHistoricalQuote(Collections.singletonList(HQ));
    }

    @Override
    public boolean insertHistoricalQuote(List<HistoricalQuote> HQs) {

        if  (HQs.size() == 0){
            System.out.println("Empty list, cannot update DB.");
            return false;
        }

        try (Connection conn = DriverManager.getConnection(dbURL)){

            System.out.println("Connected to DB!");

            if (!tableExists(HQs.get(0).getSymbol())){
                System.out.println("Table " + HQs.get(0).getSymbol() + " does not exist, initialize table first.");
                return false;
            }

            PreparedStatement quoteInsertionStatement = conn.prepareStatement(String.format(QUOTE_INSERTION, HQs.get(0).getSymbol()));

            for (HistoricalQuote hq : HQs){


                if (recordExists(conn, HQs.get(0).getSymbol(), "date", DATE_FORMAT.format(hq.getDate().getTime()))){

                    System.out.println("Quote @ " + DATE_FORMAT.format(hq.getDate().getTime()) + " already in DB.");

                    continue;
                }

                try {
                    quoteInsertionStatement.setString(1, hq.getSymbol());
                    quoteInsertionStatement.setString(2, DATE_FORMAT.format(hq.getDate().getTime()));
                    quoteInsertionStatement.setDouble(3, hq.getOpen().doubleValue());
                    quoteInsertionStatement.setDouble(4, hq.getLow().doubleValue());
                    quoteInsertionStatement.setDouble(5, hq.getHigh().doubleValue());
                    quoteInsertionStatement.setDouble(6, hq.getClose().doubleValue());
                    quoteInsertionStatement.setDouble(7, hq.getAdjClose().doubleValue());
                    quoteInsertionStatement.setLong(8, hq.getVolume());

                    quoteInsertionStatement.execute();
                } catch (NullPointerException e){
                    e.printStackTrace();
                    System.out.println();
                    System.out.println(e.getMessage());
                }
            }


        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }


        return true;
    }

    @Override
    public boolean updateStockTable(String symbol) {

        if(!tableExists(symbol)){
            return false;
        }

        long diffDays = 0;

        try (Connection conn = DriverManager.getConnection(dbURL)) {

        PreparedStatement latestQuoteStatement = conn.prepareStatement(String.format(SINGLE_QUOTE_EXTRACTION_LATEST, symbol.toUpperCase()));

        ResultSet results = latestQuoteStatement.executeQuery();



        if (results.isClosed()){
            return false;
        } else {
            String dateString = results.getString("date");
            System.out.println("Date from DB: " + dateString);

            if (dateString.equals(DATE_FORMAT.format(YahooFinance.get(symbol, Calendar.getInstance()).getHistory().get(0).getDate().getTime()))){
                System.out.println("Table is up-to-date");
            } else {

                Date latestDate = DATE_FORMAT.parse(dateString);
                Date todaysDate = Calendar.getInstance().getTime();

                long diffMS = Math.abs(todaysDate.getTime() - latestDate.getTime());
                diffDays = TimeUnit.DAYS.convert(diffMS, TimeUnit.MILLISECONDS);

                System.out.println("difference in days: " + diffDays);



            }

        }


        } catch (SQLException | IOException | ParseException e) {
            e.printStackTrace();
        }

        if (diffDays > 0){
            dataInsertion(symbol, (int) diffDays - 1);
        }


        return true;
    }

    @Override
    public HistoricalQuote getSingleQuote(String symbol) {

        if(!tableExists(symbol)){
            return null;
        }

        try (Connection conn = DriverManager.getConnection(dbURL)) {

            PreparedStatement latestQuoteStatement = conn.prepareStatement(String.format(SINGLE_QUOTE_EXTRACTION_LATEST, symbol.toUpperCase()));

            ResultSet results = latestQuoteStatement.executeQuery();

            if (results.isClosed()){
                return null;
            } else {

                Calendar calendar = Calendar.getInstance();
                Date date = DATE_FORMAT.parse(results.getString("date"));
                calendar.setTime(date);
                return getSingleQuote(symbol, calendar);
            }


        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public HistoricalQuote getSingleQuote(String symbol, Calendar calendar) {


        if(!tableExists(symbol)){
            return null;
        }

        try (Connection conn = DriverManager.getConnection(dbURL)) {

            PreparedStatement dateQuoteStatement = conn.prepareStatement(String.format(SINGLE_QUOTE_EXTRACTION_DATE, symbol.toUpperCase()));

            dateQuoteStatement.setString(1, DATE_FORMAT.format(calendar.getTime()));

            ResultSet results = dateQuoteStatement.executeQuery();

            if (results.isClosed()){
                System.out.println("Null result");
                return null;
            } else {

                String dateString = results.getString("date");
                Date date = DATE_FORMAT.parse(dateString);

                Calendar internalCal = Calendar.getInstance();
                internalCal.setTime(date);
                BigDecimal open = new BigDecimal(results.getDouble("open"));
                BigDecimal low = new BigDecimal(results.getDouble("low"));
                BigDecimal high = new BigDecimal(results.getDouble("high"));
                BigDecimal close = new BigDecimal(results.getDouble("close"));
                BigDecimal adjClose = new BigDecimal(results.getDouble("adjClose"));
                Long volume = results.getLong("volume");


                return new HistoricalQuote(symbol, internalCal, open, low, high, close, adjClose, volume);
            }


        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }

        return null;

    }

    @Override
    public List<HistoricalQuote> getQuotes(String symbol, Calendar fromDate) {
        return getQuotes(symbol, fromDate, Calendar.getInstance());
    }

    @Override
    public List<HistoricalQuote> getQuotes(String symbol, Calendar fromDate, Calendar toDate) {

        if (fromDate.after(toDate)){
            System.out.println("fromDate has to be before toDate.");
            return null;
        }

        if (!tableExists(symbol)){
            System.out.println("Table " + symbol + " does not exist.");
        }

        List<HistoricalQuote> HQs = new ArrayList<>();
        HistoricalQuote hq;


        while(!equalCalendarsDay(fromDate, toDate)){

            hq = getSingleQuote(symbol, fromDate);
            if (hq != null){
                System.out.println(DATE_FORMAT.format(hq.getDate().getTime()));
            }
            fromDate.add(Calendar.DAY_OF_MONTH, 1);
            if (hq != null){
                HQs.add(hq);
            }

        }

        if (equalCalendarsDay(fromDate, toDate)){
            hq = getSingleQuote(symbol, fromDate);
            if (hq != null){
                HQs.add(hq);
            }
        }
        return HQs;
    }

    private boolean tableExists(String symbol) {

        if (!dataGetter.correctSymbol(symbol)){
            System.out.println("Symbol " + symbol + " incorrect.");
            return false;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL)){

            System.out.println("Connected to DB!");

            DatabaseMetaData meta = conn.getMetaData();

            ResultSet results = meta.getTables(null, null, symbol.toUpperCase(), new String[] {"TABLE"});

            if (!results.next()) {
                System.out.println("Table: " + symbol.toUpperCase() + " does not exist.");
                return false;
            }


        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;

    }

    private boolean recordExists(Connection conn, String symbol, String keyColumn, String key) throws SQLException {
        PreparedStatement preparedStatement = conn.prepareStatement(String.format(SINGLE_QUOTE_EXTRACTION, symbol, keyColumn));

        preparedStatement.setString(1, key);

        try (ResultSet results = preparedStatement.executeQuery()) {


            if (results.isClosed()) {
                return false;
            }
        }
        return true;

    }

    private boolean equalCalendarsDay(Calendar c1, Calendar c2){
        return DATE_FORMAT.format(c1.getTime()).equals(DATE_FORMAT.format(c2.getTime()));
    }
}