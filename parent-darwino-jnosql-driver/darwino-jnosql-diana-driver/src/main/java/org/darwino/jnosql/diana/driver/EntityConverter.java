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

import org.jnosql.diana.api.document.Document;
import org.jnosql.diana.api.document.DocumentEntity;
import org.jnosql.diana.driver.ValueUtil;

import com.darwino.commons.json.JsonArray;
import com.darwino.commons.json.JsonException;
import com.darwino.commons.json.JsonObject;
import com.darwino.commons.util.StringUtil;
import com.darwino.jsonstore.Cursor;
import com.darwino.jsonstore.JsqlCursor;
import com.darwino.jsonstore.Store;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;
import static java.util.stream.StreamSupport.stream;

/**
 * Utility class for dealing with Darwino entities.
 *  
 * @author Jesse Gallagher
 * @since 0.0.4
 */
public final class EntityConverter {
	/**
	 * The field used to store the UNID of the document during JSON
	 * serialization, currently "_id"
	 */
	public static final String ID_FIELD = "_id"; //$NON-NLS-1$
	/**
	 * The expected field containing the collection name of the document in
	 * Darwino, currently "form"
	 */
	// TODO consider making this the store ID
	public static final String NAME_FIELD = "form"; //$NON-NLS-1$

	private EntityConverter() {
	}

	static List<DocumentEntity> convert(Collection<String> keys, Store store) {
		return keys.stream().map(t -> {
			try {
				return store.loadDocument(t, Cursor.JSON_METADATA);
			} catch (JsonException e) {
				throw new RuntimeException(e);
			}
		}).filter(Objects::nonNull).map(doc -> {
			try {
				List<Document> documents = toDocuments(doc);
				String name = StringUtil.toString(doc.get(NAME_FIELD));
				return DocumentEntity.of(name, documents);
			} catch (NullPointerException | JsonException e) {
				throw new RuntimeException(e);
			}
		}).collect(Collectors.toList());
	}

	static List<DocumentEntity> convert(Cursor cursor) throws JsonException {
		List<DocumentEntity> result = new ArrayList<>();
		cursor.find((entry) -> {
			com.darwino.jsonstore.Document doc = entry.loadDocument();
			String name = StringUtil.toString(doc.get(NAME_FIELD));
			List<Document> documents = toDocuments(doc);
			result.add(DocumentEntity.of(name, documents));
			return true;
		});
		return result;
	}
	
	static List<DocumentEntity> convert(Store store, JsqlCursor cursor) throws JsonException {
		List<DocumentEntity> result = new ArrayList<>();
		cursor.find(e -> {
			Object id = e.getColumn("_unid"); //$NON-NLS-1$
			if(id == null) {
				id = e.getColumn("unid"); //$NON-NLS-1$
			}
			if(id == null || !(id instanceof CharSequence)) {
				throw new RuntimeException("query must contain a unid column"); //$NON-NLS-1$
			}

			com.darwino.jsonstore.Document doc = store.loadDocument((String)id);
			String name = StringUtil.toString(doc.get(NAME_FIELD));
			List<Document> documents = toDocuments(doc);
			result.add(DocumentEntity.of(name, documents));
			return true;
		});
		return result;
	}

	public static List<Document> toDocuments(com.darwino.jsonstore.Document doc) throws JsonException {
		List<Document> result = new ArrayList<>();
		result.add(Document.of(ID_FIELD, doc.getUnid()));
		JsonObject json = (JsonObject)doc.getJson();
		json.remove(NAME_FIELD);
		result.addAll(toDocuments(json));
		return result;
	}

	@SuppressWarnings("unchecked")
	public static List<Document> toDocuments(Map<String, Object> map) {
		List<Document> documents = new ArrayList<>();
		for (String key : map.keySet()) {
			Object value = map.get(key);
			if (Map.class.isInstance(value)) {
				documents.add(Document.of(key, toDocuments(Map.class.cast(value))));
			} else if (isADocumentIterable(value)) {
				List<List<Document>> subDocuments = new ArrayList<>();
				stream(Iterable.class.cast(value).spliterator(), false)
					.map(m -> toDocuments(Map.class.cast(m)))
					.forEach(e -> subDocuments.add((List<Document>)e));
				documents.add(Document.of(key, subDocuments));
			} else {
				documents.add(Document.of(key, value));
			}
		}
		return documents;
	}

	@SuppressWarnings("unchecked")
	private static boolean isADocumentIterable(Object value) {
		return Iterable.class.isInstance(value) && stream(Iterable.class.cast(value).spliterator(), false).allMatch(d -> Map.class.isInstance(d));
	}

	/**
	 * Converts the provided {@link DocumentEntity} instance into a Darwino
	 * {@link JsonObject}.
	 * 
	 * <p>This is equivalent to calling {@link #convert(DocumentEntity, boolean)} with
	 * <code>false</code> as the second parameter.</p>
	 * 
	 * @param entity the entity instance to convert
	 * @return the converted JSON object
	 */
	public static JsonObject convert(DocumentEntity entity) {
		return convert(entity, false);
	}
	
	/**
	 * Converts the provided {@link DocumentEntity} instance into a Darwino
	 * {@link JsonObject}.
	 * 
	 * @param entity the entity instance to convert
	 * @param retainId whether or not to remove the {@link #ID_FIELD} field during conversion
	 * @return the converted JSON object
	 */
	public static JsonObject convert(DocumentEntity entity, boolean retainId) {
		requireNonNull(entity, "entity is required"); //$NON-NLS-1$

		JsonObject jsonObject = new JsonObject.LinkedMap();
		entity.getDocuments().stream().forEach(toJsonObject(jsonObject));
		jsonObject.put(NAME_FIELD, entity.getName());
		if(!retainId) {
			jsonObject.remove(ID_FIELD);
		}
		return jsonObject;
	}

	private static Consumer<Document> toJsonObject(JsonObject jsonObject) {
        return d -> {
        		// Swap out sensitive names
            Object value = ValueUtil.convert(d.getValue());
            
            if (Document.class.isInstance(value)) {
                convertDocument(jsonObject, d, value);
            } else if (Iterable.class.isInstance(value)) {
                convertIterable(jsonObject, d, value);
            } else {
                jsonObject.put(d.getName(), value);
            }
        };
    }

	private static void convertDocument(JsonObject jsonObject, Document d, Object value) {
		Document document = Document.class.cast(value);
		jsonObject.put(d.getName(), Collections.singletonMap(document.getName(), document.get()));
	}

	@SuppressWarnings("unchecked")
	private static void convertIterable(JsonObject jsonObject, Document document, Object value) {
		JsonObject map = new JsonObject.LinkedMap();
		JsonArray array = new JsonArray();
		Iterable.class.cast(value).forEach(element -> {
			if(Document.class.isInstance(element)) {
				Document subDocument = Document.class.cast(element);
				map.put(subDocument.getName(), subDocument.get());
			} else if(isSubDocument(element)) {
				JsonObject subJson = new JsonObject.LinkedMap();
				stream(Iterable.class.cast(element).spliterator(), false)
					.forEach(getSubDocument(subJson));
				array.add(subJson);
			} else {
				array.add(element);
			}
		});
		if(array.isEmpty()) {
			jsonObject.put(document.getName(), map);
		} else {
			jsonObject.put(document.getName(), array);
		}
	}
	
	@SuppressWarnings("rawtypes")
	private static Consumer getSubDocument(JsonObject subJson) {
		return e -> toJsonObject(subJson).accept((Document)e);
	}
	
	@SuppressWarnings("unchecked")
	private static boolean isSubDocument(Object value) {
		return value instanceof Iterable && stream(Iterable.class.cast(value).spliterator(), false)
				.allMatch(d -> org.jnosql.diana.api.document.Document.class.isInstance(d));
	}
}