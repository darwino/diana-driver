package org.darwino.jnosql.artemis.extension.converter;

import com.darwino.commons.util.StringUtil;
import org.jnosql.artemis.AttributeConverter;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

/**
 * This converter converts between {@link OffsetDateTime} values and ISO8601-formatted date
 * strings.
 * 
 * @author Jesse Gallagher
 * @since 0.0.8
 */
public class ISOOffsetDateTimeConverter implements AttributeConverter<OffsetDateTime, String> {

	@Override
	public String convertToDatabaseColumn(OffsetDateTime attribute) {
		if(attribute == null) {
			return null;
		} else {
			return attribute.toString();
		}
	}

	/**
	 * @throws IllegalArgumentException if the provided string cannot be parsed to a date
	 */
	@Override
	public OffsetDateTime convertToEntityAttribute(String dbData) {
		if(StringUtil.isEmpty(dbData)) {
			return null;
		} else {
			try {
				return OffsetDateTime.parse(dbData);
			} catch (DateTimeParseException e) {
				throw new IllegalArgumentException("Unable to parse provided date value \"" + dbData + "\"", e);
			}
		}
	}


}
