/**
 * Copyright © 2017 Jesse Gallagher
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
import com.darwino.jsonstore.JsqlCursor;
import com.darwino.jsonstore.Store;

import org.jnosql.diana.api.document.Document;
import org.jnosql.diana.api.document.DocumentDeleteQuery;
import org.jnosql.diana.api.document.DocumentEntity;
import org.jnosql.diana.api.document.DocumentQuery;

import java.time.Duration;
import java.util.ArrayList;
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
	private final Store store;

	DefaultDarwinoDocumentCollectionManager(Store store) {
		this.store = store;
	}

	@Override
	public DocumentEntity insert(DocumentEntity entity) {
		requireNonNull(entity, "entity is required");
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
			com.darwino.jsonstore.Document doc = store.newDocument(unid);
			doc.setJson(jsonObject);
			doc.save();
			return entity;
		} catch (JsonException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public DocumentEntity insert(DocumentEntity entity, Duration ttl) {
		requireNonNull(entity, "entity is required");
		requireNonNull(ttl, "ttl is required");
		return insert(entity);
	}

	@Override
	public DocumentEntity update(DocumentEntity entity) {
		JsonObject jsonObject = convert(entity);
		Document id = entity.find(EntityConverter.ID_FIELD).orElseThrow(() -> new DarwinoNoKeyFoundException(entity.toString()));

		String unid = StringUtil.toString(id.get());
		try {
			com.darwino.jsonstore.Document doc = store.loadDocument(unid);
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
			QueryConverter.QueryConverterResult delete = QueryConverter.delete(query, store.getDatabase().getId(), store.getId());
			delete.getStatement().deleteAllDocuments(0);
		} catch (JsonException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<DocumentEntity> select(DocumentQuery query) throws NullPointerException {
		try {
			QueryConverter.QueryConverterResult select = QueryConverter.select(query, store.getDatabase().getId(), store.getId());
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
		requireNonNull(query, "query is required");
		requireNonNull(params, "params is required");
		try {
			return convert(store.openCursor().query(query).params(params));
		} catch (JsonException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<DocumentEntity> query(String query) throws NullPointerException {
		requireNonNull(query, "query is required");
		try {
			return convert(store.openCursor().query(query));
		} catch (JsonException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<DocumentEntity> query(Cursor cursor) throws NullPointerException {
		requireNonNull(cursor, "cursor is required");
		try {
			return convert(cursor);
		} catch (JsonException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<DocumentEntity> search(String query) {
		try {
			Cursor cursor = store.openCursor().ftSearch(query);
			return convert(cursor);
		} catch (JsonException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<DocumentEntity> jsqlQuery(String jsqlQuery, JsonObject params) throws NullPointerException {
		try {
			JsqlCursor cursor = store.getDatabase().getSession().openJsqlCursor()
				.database(store.getDatabase().getId())
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
			}
			return convert(store, jsqlQuery);
		} catch(JsonException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<DocumentEntity> jsqlQuery(String jsqlQuery) throws NullPointerException {
		return jsqlQuery(jsqlQuery, null);
	}

	@Override
	public void close() {

	}

}