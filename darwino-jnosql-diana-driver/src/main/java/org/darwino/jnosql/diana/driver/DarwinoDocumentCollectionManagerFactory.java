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
		Objects.requireNonNull(store, "store is required");

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