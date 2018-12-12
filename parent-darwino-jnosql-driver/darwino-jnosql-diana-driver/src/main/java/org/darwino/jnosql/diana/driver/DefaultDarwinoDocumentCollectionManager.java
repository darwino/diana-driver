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

import com.darwino.commons.json.JsonException;
import com.darwino.commons.json.JsonObject;
import com.darwino.commons.util.StringUtil;
import com.darwino.jsonstore.Cursor;
import com.darwino.jsonstore.Database;
import com.darwino.jsonstore.JsqlCursor;
import com.darwino.jsonstore.Session;
import com.darwino.jsonstore.Store;
import com.darwino.platform.DarwinoContext;

import org.jnosql.diana.api.document.Document;
import org.jnosql.diana.api.document.DocumentDeleteQuery;
import org.jnosql.diana.api.document.DocumentEntity;
import org.jnosql.diana.api.document.DocumentQuery;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static org.darwino.jnosql.diana.driver.EntityConverter.convert;

/**
 * The default implementation of {@link DarwinoDocumentCollectionManager}
 */
class DefaultDarwinoDocumentCollectionManager implements DarwinoDocumentCollectionManager {
	private final String databaseName;
	private final String storeId;

	DefaultDarwinoDocumentCollectionManager(String databaseName, String storeId) {
		this.databaseName = databaseName;
		this.storeId = storeId;
	}

	@Override
	public DocumentEntity insert(DocumentEntity entity) {
		requireNonNull(entity, "entity is required"); //$NON-NLS-1$
		JsonObject jsonObject = convert(entity);
		Optional<Document> maybeId = entity.find(EntityConverter.ID_FIELD);
		Document id;
		if(maybeId.isPresent()) {
			id = maybeId.get();
		} else {
			// Auto-insert a UNID
			id = Document.of(EntityConverter.ID_FIELD, UUID.randomUUID().toString());
			entity.add(id);
		}

		String unid = StringUtil.toString(id.get());
		try {
			com.darwino.jsonstore.Document doc = getStore().newDocument(unid);
			doc.setJson(jsonObject);
			doc.save();
			return entity;
		} catch (JsonException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public DocumentEntity insert(DocumentEntity entity, Duration ttl) {
		requireNonNull(entity, "entity is required"); //$NON-NLS-1$
		requireNonNull(ttl, "ttl is required"); //$NON-NLS-1$
		return insert(entity);
	}

	@Override
	public DocumentEntity update(DocumentEntity entity) {
		JsonObject jsonObject = convert(entity);
		Document id = entity.find(EntityConverter.ID_FIELD).orElseThrow(() -> new DarwinoNoKeyFoundException(entity.toString()));

		String unid = StringUtil.toString(id.get());
		try {
			com.darwino.jsonstore.Document doc = getStore().loadDocument(unid);
			doc.setJson(jsonObject);
			doc.save();
			return entity;
		} catch (JsonException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void delete(DocumentDeleteQuery query) {
		try {
			QueryConverter.QueryConverterResult delete = QueryConverter.delete(query, getStore().getDatabase().getId(), getStore().getId());
			delete.getStatement().deleteAllDocuments(0);
		} catch (JsonException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<DocumentEntity> select(DocumentQuery query) throws NullPointerException {
		try {
			QueryConverter.QueryConverterResult select = QueryConverter.select(query, getStore().getDatabase().getId(), getStore().getId());
			List<DocumentEntity> entities = new ArrayList<>();
			if (nonNull(select.getStatement())) {
				entities.addAll(convert(select.getStatement().params(select.getParams())));
			}

			return entities;
		} catch (JsonException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<DocumentEntity> query(String query, JsonObject params) throws NullPointerException {
		requireNonNull(query, "query is required"); //$NON-NLS-1$
		requireNonNull(params, "params is required"); //$NON-NLS-1$
		try {
			return convert(getStore().openCursor().query(query).params(params));
		} catch (JsonException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<DocumentEntity> query(String query) throws NullPointerException {
		requireNonNull(query, "query is required"); //$NON-NLS-1$
		try {
			return convert(getStore().openCursor().query(query));
		} catch (JsonException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<DocumentEntity> query(Cursor cursor) throws NullPointerException {
		requireNonNull(cursor, "cursor is required"); //$NON-NLS-1$
		try {
			return convert(cursor);
		} catch (JsonException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<DocumentEntity> search(String query) {
		try {
			Cursor cursor = getStore().openCursor().ftSearch(query);
			return convert(cursor);
		} catch (JsonException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public List<DocumentEntity> search(String query, Collection<String> orderBy) {
		if(orderBy == null) {
			return search(query);
		}
		
		try {
			Cursor cursor = getStore().openCursor().ftSearch(query).orderBy(orderBy.toArray(new String[orderBy.size()]));
			return convert(cursor);
		} catch (JsonException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<DocumentEntity> jsqlQuery(String jsqlQuery, JsonObject params) throws NullPointerException {
		try {
			JsqlCursor cursor = getStore().getDatabase().getSession().openJsqlCursor()
				.database(getStore().getDatabase().getId())
				.query(jsqlQuery);
			return jsqlQuery(cursor, params);
		} catch(JsonException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<DocumentEntity> jsqlQuery(JsqlCursor jsqlQuery, JsonObject params) throws NullPointerException {
		try {
			if(Objects.nonNull(params)) {
				jsqlQuery.params(params);
				// Special support for skip and limit params
				int skip = params.getAsInt("skip"); //$NON-NLS-1$
				int limit = params.getAsInt("limit"); //$NON-NLS-1$
				if(skip != 0 || limit != 0) {
					jsqlQuery.range(skip, limit);
				}
			}
			return convert(getStore(), jsqlQuery);
		} catch(JsonException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<DocumentEntity> jsqlQuery(String jsqlQuery) throws NullPointerException {
		return jsqlQuery(jsqlQuery, null);
	}
	
	@Override
	public List<DocumentEntity> storedCursor(String cursorName, JsonObject params) {
		requireNonNull(cursorName, "query is required"); //$NON-NLS-1$
		try {
			
			Cursor cursor = getStore().openCursor()
					.load(cursorName);
			
			if(Objects.nonNull(params)) {
				// Special support for skip and limit params
				int skip = params.getAsInt("skip"); //$NON-NLS-1$
				int limit = params.getAsInt("limit"); //$NON-NLS-1$
				if(skip != 0 || limit != 0) {
					cursor.range(skip, limit);
				}

				cursor.params(params);
			}
			
			return convert(cursor);
		} catch (JsonException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void close() {

	}

	@Override
	public long count(String documentCollection) {
		// TODO determine how to map "documentCollection"
		try {
			return getStore().documentCount();
		} catch (JsonException e) {
			throw new RuntimeException(e);
		}
	}
	
	private Store getStore() throws JsonException {
		Session session = DarwinoContext.get().getSession();
		Database database = session.getDatabase(databaseName);
		return database.getStore(storeId);
	}

}