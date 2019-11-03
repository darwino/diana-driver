package org.darwino.jnosql.artemis.extension;

import static org.junit.Assert.assertEquals;

import java.time.OffsetDateTime;
import java.util.Calendar;
import java.util.TimeZone;

import org.darwino.jnosql.artemis.extension.converter.ISOOffsetDateTimeConverter;
import org.junit.Test;

import com.darwino.commons.util.DateTimeISO8601;

@SuppressWarnings("nls")
public class ConverterTest {
	@Test
	public void testOffsetDateTimeConverter() {
		ISOOffsetDateTimeConverter converter = new ISOOffsetDateTimeConverter();
		
		TimeZone tz = TimeZone.getTimeZone("America/New_York");
		Calendar cal = Calendar.getInstance(tz);
		
		{
			OffsetDateTime source = OffsetDateTime.ofInstant(cal.toInstant(), tz.toZoneId());
			String expected = DateTimeISO8601.formatISO8601(cal, true);
			String val = converter.convertToDatabaseColumn(source);
			assertEquals(expected, val);
		}
		{
			String source = DateTimeISO8601.formatISO8601(cal, true);
			OffsetDateTime expected = OffsetDateTime.ofInstant(cal.toInstant(), tz.toZoneId());
			OffsetDateTime val = converter.convertToEntityAttribute(source);
			assertEquals(expected, val);
		}
	}
	
	@Test
	public void testOffsetDateTimeConverterCompressedTime() {
		ISOOffsetDateTimeConverter converter = new ISOOffsetDateTimeConverter();
		
		TimeZone tz = TimeZone.getTimeZone("America/New_York");
		Calendar cal = Calendar.getInstance(tz);
		cal.set(Calendar.MILLISECOND, 0);
		
		{
			OffsetDateTime source = OffsetDateTime.ofInstant(cal.toInstant(), tz.toZoneId());
			String expected = DateTimeISO8601.formatISO8601(cal, true);
			String val = converter.convertToDatabaseColumn(source);
			assertEquals(expected, val);
		}
		{
			String source = DateTimeISO8601.formatISO8601(cal, true);
			OffsetDateTime expected = OffsetDateTime.ofInstant(cal.toInstant(), tz.toZoneId());
			OffsetDateTime val = converter.convertToEntityAttribute(source);
			assertEquals(expected, val);
		}
	}
}
