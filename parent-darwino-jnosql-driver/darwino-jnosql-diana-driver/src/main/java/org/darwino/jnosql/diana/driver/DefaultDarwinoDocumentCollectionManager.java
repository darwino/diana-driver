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

import com.darwino.commons.json.JsonException;
import com.darwino.commons.json.JsonFactory;
import com.darwino.commons.json.JsonUtil;
import com.darwino.commons.util.StringUtil;
import com.darwino.commons.util.io.content.InputStreamContent;
import com.darwino.jsonstore.*;
import com.darwino.platform.DarwinoContext;

import jakarta.nosql.Value;
import jakarta.nosql.document.Document;
import jakarta.nosql.document.DocumentDeleteQuery;
import jakarta.nosql.document.DocumentEntity;
import jakarta.nosql.document.DocumentQuery;
import org.eclipse.jnosql.diana.driver.attachment.EntityAttachment;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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

	@SuppressWarnings("unchecked")
	@Override
	public DocumentEntity insert(DocumentEntity entity) {
		requireNonNull(entity, "entity is required"); //$NON-NLS-1$
		try {
			Object jsonObject = convert(entity, getStore().getSession().getJsonFactory());
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
			com.darwino.jsonstore.Document doc = getStore().newDocument(unid);
			doc.setJson(jsonObject);
			
			Optional<List<EntityAttachment>> attachments = entity.getDocuments().stream()
				.filter(e -> EntityConverter.ATTACHMENT_FIELD.equals(e.getName()))
				.findFirst()
				.map(Document::getValue)
				.map(Value::get)
				.filter(Collection.class::isInstance)
				.map(Collection.class::cast)
				.map(Collection::stream)
				.map(s -> (List<EntityAttachment>)s.filter(EntityAttachment.class::isInstance)
					.map(EntityAttachment.class::cast)
					.collect(Collectors.toList())
				);
			if(attachments.isPresent()) {
				for(EntityAttachment att : attachments.get()) {
					String name = att.getName();
					if(doc.attachmentExists(name)) {
						// Check if it needs updating
						Attachment docAtt = doc.getAttachment(name);
						if(docAtt.getLastModificationDate().before(new Date(att.getLastModified()))) {
							docAtt.update(new InputStreamContent(att.getData(), att.getLength(), att.getContentType()));
						} else {
							// No need to update
						}
					} else {
						doc.createAttachment(name, new InputStreamContent(att.getData(), att.getLength(), att.getContentType()));
					}
				}
			}
			
			doc.save();
			return entity;
		} catch (JsonException | IOException e) {
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
	public Iterable<DocumentEntity> insert(Iterable<DocumentEntity> entities) {
		requireNonNull(entities, "entities is required");
		return StreamSupport.stream(entities.spliterator(), false)
			.map(this::insert)
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
	}

	@Override
	public Iterable<DocumentEntity> insert(Iterable<DocumentEntity> entities, Duration ttl) {
		requireNonNull(entities, "entities is required");
		requireNonNull(ttl, "ttl is required"); //$NON-NLS-1$
		return StreamSupport.stream(entities.spliterator(), false)
			.map(e -> insert(e, ttl))
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
	}

	@Override
	public DocumentEntity update(DocumentEntity entity) {
		try {
			Object jsonObject = convert(entity, getStore().getSession().getJsonFactory());
			Document id = entity.find(EntityConverter.ID_FIELD).orElseThrow(() -> new DarwinoNoKeyFoundException(entity.toString()));

			String unid = StringUtil.toString(id.get());
			com.darwino.jsonstore.Document doc = getStore().loadDocument(unid);
			doc.setJson(jsonObject);
			doc.save();
			return entity;
		} catch (JsonException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Iterable<DocumentEntity> update(Iterable<DocumentEntity> entities) {
		requireNonNull(entities, "entities is required");
		return StreamSupport.stream(entities.spliterator(), false)
			.map(this::update)
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
	}

	@Override
	public void delete(DocumentDeleteQuery query) {
		try {
			QueryConverter.QueryConverterResult delete = QueryConverter.delete(query, getStore().getDatabase().getId(), getStore().getId(), getStore().getSession().getJsonFactory());
			delete.getStatement().deleteAllDocuments(0);
		} catch (JsonException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Stream<DocumentEntity> select(DocumentQuery query) throws NullPointerException {
		try {
			QueryConverter.QueryConverterResult select = QueryConverter.select(query, getStore().getDatabase().getId(), getStore().getId(), getStore().getSession().getJsonFactory());
			if (nonNull(select.getStatement())) {
				return convert(select.getStatement().params(JsonUtil.toJsonObject(select.getParams(), getStore().getSession().getJsonFactory())));
			} else {
				return Stream.empty();
			}
		} catch (JsonException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Stream<DocumentEntity> query(String query, Object params) throws NullPointerException {
		requireNonNull(query, "query is required"); //$NON-NLS-1$
		requireNonNull(params, "params is required"); //$NON-NLS-1$
		try {
			JsonFactory fac = getStore().getSession().getJsonFactory();
			return convert(getStore().openCursor().query(query).params(JsonUtil.toJsonObject(params, fac)));
		} catch (JsonException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Stream<DocumentEntity> query(String query) throws NullPointerException {
		requireNonNull(query, "query is required"); //$NON-NLS-1$
		try {
			return convert(getStore().openCursor().query(query));
		} catch (JsonException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Stream<DocumentEntity> query(Cursor cursor) throws NullPointerException {
		requireNonNull(cursor, "cursor is required"); //$NON-NLS-1$
		try {
			return convert(cursor);
		} catch (JsonException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Stream<DocumentEntity> search(String query) {
		try {
			Cursor cursor = getStore().openCursor().ftSearch(query);
			return convert(cursor);
		} catch (JsonException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public Stream<DocumentEntity> search(String query, Collection<String> orderBy) {
		if(orderBy == null) {
			return search(query);
		}
		
		try {
			Cursor cursor = getStore().openCursor().ftSearch(query).orderBy(orderBy.toArray(new String[0]));
			return convert(cursor);
		} catch (JsonException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Stream<DocumentEntity> jsqlQuery(String jsqlQuery, Object params) throws NullPointerException {
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
	public Stream<DocumentEntity> jsqlQuery(JsqlCursor jsqlQuery, Object params) throws NullPointerException {
		try {
			if(Objects.nonNull(params)) {
				JsonFactory fac = getStore().getSession().getJsonFactory();
				jsqlQuery.params(JsonUtil.toJsonObject(params, fac));

				// Special support for skip and limit params
				int skip = getSkip(fac, params);
				int limit = getLimit(fac, params);
				if(skip != 0 || limit != Integer.MAX_VALUE) {
					jsqlQuery.range(skip, limit);
				}
			}
			return convert(getStore(), jsqlQuery);
		} catch(JsonException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Stream<DocumentEntity> jsqlQuery(String jsqlQuery) throws NullPointerException {
		return jsqlQuery(jsqlQuery, null);
	}
	
	@Override
	public Stream<DocumentEntity> storedCursor(String cursorName, Object params) {
		requireNonNull(cursorName, "query is required"); //$NON-NLS-1$
		try {
			
			Cursor cursor = getStore().openCursor()
					.load(cursorName);
			
			if(Objects.nonNull(params)) {
				JsonFactory fac = getStore().getSession().getJsonFactory();
				// Special support for skip and limit params
				int skip = getSkip(fac, params);
				int limit = getLimit(fac, params);
				if(skip != 0 || limit != Integer.MAX_VALUE) {
					cursor.range(skip, limit);
				}

				cursor.params(JsonUtil.toJsonObject(params, fac));
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

	private static int getSkip(JsonFactory fac, Object params) throws JsonException {
		Object skipObj = fac.getProperty(params, "skip");
		int skip;
		if(fac.isNumber(skipObj)) {
			skip = (int)fac.getNumber(skipObj);
		} else {
			skip = 0;
		}
		return skip;
	}
	private static int getLimit(JsonFactory fac, Object params) throws JsonException {
		Object limitObj = fac.getProperty(params, "limit");
		int limit;
		if(fac.isNumber(limitObj)) {
			limit = (int)fac.getNumber(limitObj);
		} else {
			limit = Integer.MAX_VALUE;
		}
		return limit;
	}

}