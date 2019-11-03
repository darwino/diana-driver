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
package org.darwino.jnosql.diana.driver;

import org.darwino.jnosql.diana.attachment.DarwinoDocumentAttachment;
import jakarta.nosql.ValueReader;
import org.eclipse.jnosql.diana.driver.attachment.EntityAttachment;

import com.darwino.commons.json.JsonException;
import com.darwino.jsonstore.Attachment;

public class DarwinoAttachmentValueReader implements ValueReader {

	@Override
	public <T> boolean isCompatible(Class<T> clazz) {
		return EntityAttachment.class.isAssignableFrom(clazz);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T read(Class<T> clazz, Object value) {
		Attachment att = (Attachment)value;
		try {
			return (T) new DarwinoDocumentAttachment(att);
		} catch (JsonException e) {
			throw new RuntimeException(e);
		}
	}

}
