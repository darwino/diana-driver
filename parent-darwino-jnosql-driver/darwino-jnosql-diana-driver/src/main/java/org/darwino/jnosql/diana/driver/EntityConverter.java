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
import com.darwino.jsonstore.Cursor;
import com.darwino.jsonstore.JsqlCursor;
import com.darwino.jsonstore.Store;

import org.darwino.jnosql.diana.attachment.DarwinoDocumentAttachment;
import jakarta.nosql.document.Document;
import jakarta.nosql.document.DocumentEntity;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
	
	public static final String ATTACHMENT_FIELD = com.darwino.jsonstore.Document.SYSTEM_PREFIX + "attachments"; //$NON-NLS-1$

	private EntityConverter() {
	}

	static Stream<DocumentEntity> convert(Collection<String> keys, Store store) {
		// TODO create a lazy-loading list
		return keys.stream().map(t -> {
			try {
				return store.loadDocument(t, Cursor.JSON_METADATA);
			} catch (JsonException e) {
				throw new RuntimeException(e);
			}
		}).filter(Objects::nonNull).map(doc -> {
			try {
				List<Document> documents = toDocuments(doc);
				String name = StringUtil.toString(doc.getString(NAME_FIELD));
				return DocumentEntity.of(name, documents);
			} catch (NullPointerException | JsonException e) {
				throw new RuntimeException(e);
			}
		});
	}

	static Stream<DocumentEntity> convert(Cursor cursor) throws JsonException {
		// TODO create a lazy-loading list
		List<DocumentEntity> result = new ArrayList<>();
		cursor.find((entry) -> {
			com.darwino.jsonstore.Document doc = entry.loadDocument();
			String name = StringUtil.toString(doc.getString(NAME_FIELD));
			List<Document> documents = toDocuments(doc);
			result.add(DocumentEntity.of(name, documents));
			return true;
		});
		return result.stream();
	}
	
	static Stream<DocumentEntity> convert(Store store, JsqlCursor cursor) throws JsonException {
		// TODO create a lazy-loading list
		List<DocumentEntity> result = new ArrayList<>();
		cursor.find(e -> {
			Object id = e.getColumn("_unid"); //$NON-NLS-1$
			if(id == null) {
				id = e.getColumn("unid"); //$NON-NLS-1$
			}
			if(!(id instanceof CharSequence)) {
				throw new RuntimeException("query must contain a unid column"); //$NON-NLS-1$
			}

			com.darwino.jsonstore.Document doc = store.loadDocument((String)id);
			String name = StringUtil.toString(doc.getString(NAME_FIELD));
			List<Document> documents = toDocuments(doc);
			result.add(DocumentEntity.of(name, documents));
			return true;
		});
		return result.stream();
	}

	public static List<Document> toDocuments(com.darwino.jsonstore.Document doc) throws JsonException {
		List<Document> result = new ArrayList<>();
		result.add(Document.of(ID_FIELD, doc.getUnid()));
		Object json = doc.getJson();
		JsonFactory fac = doc.getSession().getJsonFactory();
		fac.removeProperty(json, NAME_FIELD);
		result.addAll(toDocuments(JsonUtil.toJsonObject(json, fac)));

		result.add(Document.of(com.darwino.jsonstore.Document.SYSTEM_META_CDATE, doc.getCreationDate()));
		result.add(Document.of(com.darwino.jsonstore.Document.SYSTEM_META_CUSER, doc.getCreationUser()));
		result.add(Document.of(com.darwino.jsonstore.Document.SYSTEM_META_MDATE, doc.getLastModificationDate()));
		result.add(Document.of(com.darwino.jsonstore.Document.SYSTEM_META_MUSER, doc.getLastModificationUser()));
		
		result.add(Document.of(ATTACHMENT_FIELD,
			Stream.of(doc.getAttachments())
				.map(t -> {
					try {
						return new DarwinoDocumentAttachment(t);
					} catch (JsonException e) {
						throw new RuntimeException(e);
					}
				})
				.collect(Collectors.toList())
		));
		
		return result;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<Document> toDocuments(Map<String, Object> map) {
		List<Document> documents = new ArrayList<>();
		for (String key : map.keySet()) {
			if(StringUtil.isEmpty(key)) {
				continue;
			}
			
			Object value = map.get(key);
			if (value instanceof Map) {
				documents.add(Document.of(key, toDocuments((Map) value)));
			} else if (isADocumentIterable(value)) {
				List<List<Document>> subDocuments = new ArrayList<>();
				stream(((Iterable) value).spliterator(), false)
					.map(m -> toDocuments((Map) m))
					.forEach(e -> subDocuments.add((List<Document>)e));
				documents.add(Document.of(key, subDocuments));
			} else if(value != null) {
				documents.add(Document.of(key, value));
			}
		}
		return documents;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static boolean isADocumentIterable(Object value) {
		return value instanceof Iterable && stream(((Iterable) value).spliterator(), false).allMatch(Map.class::isInstance);
	}

	/**
	 * Converts the provided {@link DocumentEntity} instance into a Darwino
	 * JSON object.
	 * 
	 * <p>This is equivalent to calling {@link #convert(DocumentEntity, JsonFactory, boolean)} with
	 * <code>false</code> as the second parameter.</p>
	 * 
	 * @param entity the entity instance to convert
	 * @return the converted JSON object
	 */
	public static Object convert(DocumentEntity entity, JsonFactory fac) throws JsonException {
		return convert(entity, fac, false);
	}
	
	/**
	 * Converts the provided {@link DocumentEntity} instance into a Darwino
	 * JSON object.
	 * 
	 * @param entity the entity instance to convert
	 * @param retainId whether or not to remove the {@link #ID_FIELD} field during conversion
	 * @return the converted JSON object
	 */
	public static Object convert(DocumentEntity entity, JsonFactory fac, boolean retainId) throws JsonException {
		requireNonNull(entity, "entity is required"); //$NON-NLS-1$

		Object jsonObject = fac.createObject();
		for(Document doc : entity.getDocuments()) {
			if(!ATTACHMENT_FIELD.equals(doc.getName())) {
				jsonObject = toJsonObject(doc, jsonObject, fac);
			}
		}
		fac.setProperty(jsonObject, NAME_FIELD, entity.getName());
		if(!retainId) {
			fac.removeProperty(jsonObject, ID_FIELD);
		}
		return jsonObject;
	}

	private static Object toJsonObject(Document d, Object json, JsonFactory fac) {
		// Swap out sensitive names
		Object value = ValueUtil.convert(d.getValue());
		Object jsonObject = json;

		try {
			if (value instanceof Document) {
				jsonObject = convertDocument(jsonObject, fac, d, value);
			} else if (value instanceof Iterable) {
				jsonObject = convertIterable(jsonObject, fac, d, value);
			} else {
				fac.setProperty(jsonObject, d.getName(), value);
			}
		} catch (JsonException e) {
			throw new RuntimeException(e);
		}
		return jsonObject;
    }

	private static Object convertDocument(Object jsonObject, JsonFactory fac, Document d, Object value) throws JsonException {
		Document document = (Document) value;
		fac.setProperty(jsonObject, d.getName(), Collections.singletonMap(document.getName(), document.get()));
		return jsonObject;
	}

	private static Object convertIterable(Object jsonObject, JsonFactory fac, Document document, Object value) throws JsonException {
		Object map = fac.createObject();
		Object array = fac.createArray();
		for(Object element : (Iterable<?>)value) {
			try {
				if (element instanceof Document) {
					Document subDocument = (Document) element;
					fac.setProperty(map, subDocument.getName(), subDocument.get());
				} else if (isSubDocument(element)) {
					Object subJson = fac.createObject();
					for(Object e : (Iterable<?>)element) {
						subJson = getSubDocument((Document)e, subJson, fac);
					}
					fac.addArrayItem(array, subJson);
				} else {
					fac.addArrayItem(array, element);
				}
			} catch(JsonException e) {
				throw new RuntimeException(e);
			}
		}
		if(fac.getArrayCount(array) == 0) {
			fac.setProperty(jsonObject, document.getName(), map);
		} else {
			fac.setProperty(jsonObject, document.getName(), array);
		}
		return jsonObject;
	}
	
	private static Object getSubDocument(Document d, Object subJson, JsonFactory fac) {
		return toJsonObject(d, subJson, fac);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static boolean isSubDocument(Object value) {
		return value instanceof Iterable && stream(((Iterable) value).spliterator(), false)
				.allMatch(Document.class::isInstance);
	}
}