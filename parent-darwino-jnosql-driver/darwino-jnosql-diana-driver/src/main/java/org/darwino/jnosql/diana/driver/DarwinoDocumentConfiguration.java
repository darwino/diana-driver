/**
 * Copyright © 2017-2018 Jesse Gallagher
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

import org.jnosql.diana.api.Settings;
import org.jnosql.diana.api.document.UnaryDocumentConfiguration;

import com.darwino.commons.json.JsonException;
import com.darwino.commons.util.StringUtil;
import com.darwino.platform.DarwinoApplication;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class DarwinoDocumentConfiguration implements UnaryDocumentConfiguration<DarwinoDocumentCollectionManagerFactory> {
	
	public static final String DATABASE_ID = "databaseId"; //$NON-NLS-1$

	@Override
	public DarwinoDocumentCollectionManagerFactory get() throws UnsupportedOperationException {
		try {
			return new DarwinoDocumentCollectionManagerFactory(DarwinoApplication.get().getManifest().getDatabases()[0]);
		} catch (JsonException e) {
			throw new UnsupportedOperationException(e);
		}
	}

	@Override
	public DarwinoDocumentCollectionManagerFactory get(Settings settings) throws NullPointerException {
		requireNonNull(settings, "settings is required"); //$NON-NLS-1$

		Map<String, String> configurations = new HashMap<>();
		settings.entrySet().forEach(e -> configurations.put(e.getKey(), e.getValue().toString()));

		String databaseId = configurations.get(DATABASE_ID);
		if(StringUtil.isEmpty(databaseId)) {
			databaseId = DarwinoApplication.get().getManifest().getDatabases()[0];
		}
		try {
			return new DarwinoDocumentCollectionManagerFactory(databaseId);
		} catch (JsonException e) {
			throw new UnsupportedOperationException(e);
		}
	}

	@Override
	public DarwinoDocumentCollectionManagerFactory getAsync() throws UnsupportedOperationException {
		try {
			return new DarwinoDocumentCollectionManagerFactory(DarwinoApplication.get().getManifest().getDatabases()[0]);
		} catch (JsonException e) {
			throw new UnsupportedOperationException(e);
		}
	}

	@Override
	public DarwinoDocumentCollectionManagerFactory getAsync(Settings settings) throws NullPointerException {
		return get(settings);
	}
}