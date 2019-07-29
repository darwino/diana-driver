/**
 * Copyright © 2017-2019 Jesse Gallagher
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
package org.darwino.jnosql.artemis.extension.converter;

import com.darwino.commons.util.StringUtil;
import jakarta.nosql.mapping.AttributeConverter;

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
