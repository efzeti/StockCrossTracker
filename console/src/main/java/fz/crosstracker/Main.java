package fz.crosstracker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;


public class Main {

    public static final String STKS_PATH_XTB = "data\\src\\main\\resources\\xtb_stocks_us.txt";
    public static final String STKS_PATH_REVOLUT = "data\\src\\main\\resources\\revolut_stocks.txt";
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException, ParseException {

        SQLConnectorImpl sqlConnector = new SQLConnectorImpl();

        sqlConnector.initializeStockTable("TSLA");

        sqlConnector.initialDataInsertion("TSLA");


    }

}
