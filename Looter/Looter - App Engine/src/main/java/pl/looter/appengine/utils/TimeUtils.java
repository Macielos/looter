package pl.looter.appengine.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Arjan on 23.04.2016.
 */

public abstract class TimeUtils {

    private static final long MINUTE = 1000 * 60;
    private static final long HOUR = 60 * MINUTE;
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
        return startOfDay(now()) - days*DAY;
    }

    public static long hour(int hours, int minutes) {
        return HOUR * hours + MINUTE * minutes;
    }

	public static long startOfDay(long time) {
		return time / DAY * DAY;
	}

    public static long startOfDay() {
        return now() / DAY * DAY;
    }

}