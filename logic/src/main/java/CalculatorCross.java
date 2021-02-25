import java.util.List;

public interface CalculatorCross {

    boolean checkCross(Stock stock);

    boolean checkCross(Stock stock, int shortPeriod, int longPeriod);

    List<Stock> allStocksCrossCheck();

    List<Stock> allStocksCrossCheck(int shortPeriod, int longPeriod);

}
