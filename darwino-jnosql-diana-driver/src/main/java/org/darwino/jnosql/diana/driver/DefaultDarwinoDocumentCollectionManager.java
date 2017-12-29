package org.darwino.jnosql.diana.driver;

import com.darwino.commons.json.JsonException;
import com.darwino.commons.json.JsonObject;
import com.darwino.commons.util.StringUtil;
import com.darwino.jsonstore.Cursor;
import com.darwino.jsonstore.Store;

import org.jnosql.diana.api.document.Document;
import org.jnosql.diana.api.document.DocumentDeleteQuery;
import org.jnosql.diana.api.document.DocumentEntity;
import org.jnosql.diana.api.document.DocumentQuery;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static org.darwino.jnosql.diana.driver.EntityConverter.convert;

/**
 * The default implementation of {@link CouchbaseDocumentCollectionManager}
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
		Document id = entity.find(EntityConverter.ID_FIELD).orElseThrow(() -> new DarwinoNoKeyFoundException(entity.toString()));

		String unid = StringUtil.toString(id.get());
		try {
			com.darwino.jsonstore.Document doc = store.newDocument(unid);
			doc.setJson(jsonObject);
			doc.save();
			entity.add(Document.of(EntityConverter.ID_FIELD, unid));
			return entity;
		} catch (JsonException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public DocumentEntity insert(DocumentEntity entity, Duration ttl) {
		requireNonNull(entity, "entity is required");
		requireNonNull(ttl, "ttl is required");
		JsonObject jsonObject = convert(entity);
		Document id = entity.find(EntityConverter.ID_FIELD).orElseThrow(() -> new DarwinoNoKeyFoundException(entity.toString()));
		String unid = StringUtil.toString(id.get());
		

		try {
			com.darwino.jsonstore.Document doc = store.newDocument(unid);
			doc.setJson(jsonObject);
			doc.save();
		} catch (JsonException e) {
			throw new RuntimeException(e);
		}
		return entity;
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
			entity.add(Document.of(EntityConverter.ID_FIELD, unid));
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
	public void close() {

	}
}