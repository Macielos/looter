package pl.looter.appengine.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Arjan on 23.04.2016.
 */

public abstract class TimeUtils {

    private static final long HOUR = 1000 * 3600;
    private static final long DAY = HOUR * 24;

    private TimeUtils() {

    }

    public static long now() {
        return System.currentTimeMillis();
    }

    public static long timeElapsed(long time) {
        return now() - time;
    }

    public static long daysAgo(int days) {
        return now() - days*DAY;
    }

    public static Date startOfDay() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        calendar.set(year, month, day, 0, 0, 0);
        return calendar.getTime();
    }

}