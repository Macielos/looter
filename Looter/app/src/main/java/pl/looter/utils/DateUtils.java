package pl.looter.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Arjan on 23.04.2016.
 */
public abstract class DateUtils {

	private DateUtils() {

	}

	public static final String DATE_FORMAT = "dd MMM yyyy hh:mm";

	public static String format(long time) {
		return new SimpleDateFormat(DATE_FORMAT).format(new Date(time));
	}

}
