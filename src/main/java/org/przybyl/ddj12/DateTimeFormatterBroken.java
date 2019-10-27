package org.przybyl.ddj12;

import java.time.*;
import java.time.format.*;
import java.time.temporal.*;
import java.util.*;

/**
 * This is demonstration of fix described in https://bugs.java.com/bugdatabase/view_bug.do?bug_id=JDK-8223773
 */
public class DateTimeFormatterBroken {

	public static void main(String[] args) {
		DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder()
			.parseStrict()
			.appendValue(ChronoField.HOUR_OF_AMPM)
			.appendLiteral(':')
			.appendValue(ChronoField.MINUTE_OF_HOUR)
			.appendLiteral(' ')
			.appendText(ChronoField.AMPM_OF_DAY);
		DateTimeFormatter formatter = builder.toFormatter(Locale.US);
		TemporalAccessor accessor = formatter.parse("12:00 PM");
		LocalTime localDateTime = LocalTime.from(accessor);
		System.out.println(localDateTime.toString());
	}
}
