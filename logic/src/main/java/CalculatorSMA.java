public interface CalculatorSMA {

    //TODO add fields for default long and short SMA period, today's golds and deaths

    double countSMA(Stock stock, int period, boolean offsetDay);

    double defaultShortSMA(Stock stock, boolean offsetDay);

    double defaultLongSMA(Stock stock, boolean offsetDay);

    void saveCrossesToFile();

}
