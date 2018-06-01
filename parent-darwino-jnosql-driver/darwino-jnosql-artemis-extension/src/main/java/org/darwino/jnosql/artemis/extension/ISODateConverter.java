package org.darwino.jnosql.artemis.extension;

import java.text.ParseException;
import java.util.Date;

import org.jnosql.artemis.AttributeConverter;

import com.darwino.commons.util.DateTimeISO8601;
import com.darwino.commons.util.StringUtil;

/**
 * This converter converts between {@link Date} values and ISO8601-formatted date
 * strings.
 * 
 * @author Jesse Gallagher
 * @since 0.0.6
 */
public class ISODateConverter implements AttributeConverter<Date, String> {

	@Override
	public String convertToDatabaseColumn(Date attribute) {
		if(attribute == null) {
			return null;
		} else {
			return DateTimeISO8601.formatISO8601(attribute.getTime());
		}
	}

	/**
	 * @throws IllegalArgumentException if the provided string cannot be parsed to a date
	 */
	@Override
	public Date convertToEntityAttribute(String dbData) {
		if(StringUtil.isEmpty(dbData)) {
			return null;
		} else {
			try {
				return DateTimeISO8601.parseISO8601Date(dbData);
			} catch (ParseException e) {
				throw new IllegalArgumentException("Unable to parse provided date value \"" + dbData + "\"", e);
			}
		}
	}


}
