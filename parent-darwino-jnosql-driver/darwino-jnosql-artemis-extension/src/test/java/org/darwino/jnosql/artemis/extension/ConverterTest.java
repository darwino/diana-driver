/**
 * Copyright © 2017-2021 Jesse Gallagher
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Includes code derived from the JNoSQL Diana Couchbase driver and Artemis
 * extensions, copyright Otavio Santana and others and available from:
 *
 * https://github.com/eclipse/jnosql-diana-driver/tree/master/couchbase-driver
 * https://github.com/eclipse/jnosql-artemis-extension/tree/master/couchbase-extension
 */
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
