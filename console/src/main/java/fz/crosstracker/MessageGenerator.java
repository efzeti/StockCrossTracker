package fz.crosstracker;

import java.util.Calendar;

public interface MessageGenerator {


    String CurrentData(String symbol);

    // from file, if file not found then Calculator Cross should start looking for them
    String TodaysCrosses();

    String CrossesFromDate(String date);

    String CrossesFromDate(Calendar date);

    // room for more functions, at this moment we'll focus on crosses


}
