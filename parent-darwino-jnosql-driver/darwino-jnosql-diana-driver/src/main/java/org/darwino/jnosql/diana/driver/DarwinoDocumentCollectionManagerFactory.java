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

import org.jnosql.diana.api.document.DocumentCollectionManagerAsyncFactory;
import org.jnosql.diana.api.document.DocumentCollectionManagerFactory;

import com.darwino.commons.json.JsonException;
import com.darwino.jsonstore.Database;
import com.darwino.jsonstore.Session;
import com.darwino.jsonstore.Store;
import com.darwino.platform.DarwinoContext;

import java.io.IOException;
import java.util.Objects;

public class DarwinoDocumentCollectionManagerFactory implements DocumentCollectionManagerFactory<DarwinoDocumentCollectionManager>,
		DocumentCollectionManagerAsyncFactory<DarwinoDocumentCollectionManagerAsync> {

	private final String database;
	private final Session session;
	
	DarwinoDocumentCollectionManagerFactory(String database) throws JsonException {
		this.database = database;
		this.session = DarwinoContext.get().getSession();
	}

	@Override
	public DarwinoDocumentCollectionManagerAsync getAsync(String store) throws UnsupportedOperationException, NullPointerException {
		return new DefaultDarwinoDocumentCollectionManagerAsync(get(store));
	}

	@Override
	public DarwinoDocumentCollectionManager get(String store) throws UnsupportedOperationException, NullPointerException {
		Objects.requireNonNull(store, "store is required"); //$NON-NLS-1$

		try {
			Database db = session.getDatabase(database);
			Store st = db.getStore(store);
			return new DefaultDarwinoDocumentCollectionManager(st);
		} catch(JsonException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void close() {
		try {
			session.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}