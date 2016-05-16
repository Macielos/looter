package pl.looter.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Arjan on 23.04.2016.
 */
public abstract class TimeUtils {

	private TimeUtils() {

	}

	public static final long HOUR = 1000 * 3600;
	public static final long DAY = HOUR * 24;

	public static final String DATE_FORMAT = "dd MMM yyyy";
	public static final String TIME_FORMAT = "kk:mm";

	public static String formatDate(long time) {
		return new SimpleDateFormat(DATE_FORMAT).format(new Date(time));
	}

	public static String formatTime(long time) {
		return new SimpleDateFormat(TIME_FORMAT).format(new Date(time));
	}

	public static long now() {
		return System.currentTimeMillis();
	}

	public static Date startOfDay() {
		return new Date(now() / DAY * DAY);
	}

	public static Date startOfDay(long time) {
		return new Date(time / DAY * DAY);
	}

	public static long textToTime(String time) throws ParseException {
		return new SimpleDateFormat(TIME_FORMAT).parse(time).getTime();
	}
}
