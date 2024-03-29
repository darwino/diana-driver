/**
 * Copyright © 2017-2021 Jesse Gallagher
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

import com.darwino.commons.json.JsonFactory;
import jakarta.nosql.Condition;
import jakarta.nosql.SortType;
import jakarta.nosql.TypeReference;
import jakarta.nosql.document.Document;
import jakarta.nosql.document.DocumentCondition;
import jakarta.nosql.document.DocumentDeleteQuery;
import jakarta.nosql.document.DocumentQuery;

import com.darwino.commons.json.JsonException;
import com.darwino.commons.json.query.parser.BaseParser;
import com.darwino.commons.util.StringUtil;
import com.darwino.jsonstore.Cursor;
import com.darwino.jsonstore.query.nodes.SpecialFieldNode;
import com.darwino.platform.DarwinoContext;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static java.util.Objects.nonNull;
import static jakarta.nosql.Condition.IN;
import static org.darwino.jnosql.diana.driver.StatementFactory.create;

/**
 * Assistant class to convert queries from Diana internal structures to Darwino
 * cursors.
 * 
 * @author Jesse Gallagher
 * @since 0.0.4
 */
final class QueryConverter {

	private static final Set<Condition> NOT_APPENDABLE = EnumSet.of(IN, Condition.AND, Condition.OR);

	private static final String[] ALL_SELECT = { "*" }; //$NON-NLS-1$

	private QueryConverter() {
	}

	static QueryConverterResult select(DocumentQuery query, String database, String store, JsonFactory fac) throws JsonException {
		Object params = fac.createObject();
		String[] documents = query.getDocuments().toArray(new String[0]);
		if (documents.length == 0) {
			documents = ALL_SELECT;
		}

		Cursor statement;
		int skip = (int)query.getSkip();
		int limit = (int)query.getLimit();

		String[] sorts = query.getSorts().stream().map(s -> s.getName() + (s.getType() == SortType.DESC ? " d" : "")).toArray(String[]::new); //$NON-NLS-1$ //$NON-NLS-2$

		if (query.getCondition().isPresent()) {
			Object condition = getCondition(query.getCondition().get(), params, fac);
			// Add in the form property if needed
			condition = applyCollectionName(condition, query.getDocumentCollection(), fac);
			if (nonNull(condition)) {
				statement = create(database, store, documents, skip, limit, sorts, condition);
			} else {
				statement = null;
			}
		} else {
			Object condition = applyCollectionName(null, query.getDocumentCollection(), fac);
			if(condition != null) {
				statement = create(database, store, documents, skip, limit, sorts, condition);
			} else {
				statement = create(database, store, documents, skip, limit, sorts);
			}
		}
		return new QueryConverterResult(params, statement);
	}

	static QueryConverterResult delete(DocumentDeleteQuery query, String database, String store, JsonFactory fac) throws JsonException {
		Object params = fac.createObject();
		Object condition = getCondition(query.getCondition().orElseThrow(() -> new IllegalArgumentException("Condition is required")), params, fac); //$NON-NLS-1$
		
		// Add in the form property if needed
		condition = applyCollectionName(condition, query.getDocumentCollection(), fac);
		
		Cursor cursor = DarwinoContext.get().getSession().getDatabase(database).getStore(store).openCursor();
		if (nonNull(condition)) {
			cursor.query(condition);
		}

		return new QueryConverterResult(params, cursor);
	}

	private static Object getCondition(DocumentCondition condition, Object params, JsonFactory fac) {
		try {
			Document document = condition.getDocument();

			if (!NOT_APPENDABLE.contains(condition.getCondition())) {
				fac.setProperty(params, document.getName(), document.get());
			}

			// Convert special names
			String name = document.getName();
			if (StringUtil.equals(name, EntityConverter.ID_FIELD)) {
				name = SpecialFieldNode.UNID;
			}

			Object placeholder = document.get();
			if(placeholder != null && placeholder.getClass().isEnum()) {
				placeholder = placeholder.toString();
			}
			Object p = params;
			switch (condition.getCondition()) {
				case EQUALS:
					return objOf(fac, name, objOf(fac, BaseParser.Op.EQ.getValue(), placeholder));
				case LESSER_THAN:
					return objOf(fac, name, objOf(fac, BaseParser.Op.LT.getValue(), placeholder));
				case LESSER_EQUALS_THAN:
					return objOf(fac, name, objOf(fac, BaseParser.Op.LTE.getValue(), placeholder));
				case GREATER_THAN:
					return objOf(fac, name, objOf(fac, BaseParser.Op.GT.getValue(), placeholder));
				case GREATER_EQUALS_THAN:
					return objOf(fac, name, objOf(fac, BaseParser.Op.GTE.getValue(), placeholder));
				case LIKE:
					return objOf(fac, name, objOf(fac, BaseParser.Op.LIKE.getValue(), placeholder));
				case IN:
					return objOf(fac, name, objOf(fac, BaseParser.Op.IN.getValue(), placeholder));
				case AND:
					return objOf(fac, BaseParser.Op.AND.getValue(), arrOf(fac, document.get(new TypeReference<List<DocumentCondition>>() {
					}).stream().map(d -> getCondition(d, p, fac)).filter(Objects::nonNull).toArray()));
				case OR:
					return objOf(fac, BaseParser.Op.OR.getValue(), arrOf(fac, document.get(new TypeReference<List<DocumentCondition>>() {
					}).stream().map(d -> getCondition(d, p, fac)).filter(Objects::nonNull).toArray()));
				case NOT:
					DocumentCondition dc = document.get(DocumentCondition.class);
					return objOf(fac, BaseParser.Op.NOT.getValue(), getCondition(dc, params, fac));
				default:
					throw new IllegalStateException("This condition is not supported in Darwino: " + condition.getCondition()); //$NON-NLS-1$
			}
		} catch(JsonException e) {
			throw new RuntimeException(e);
		}
	}

	static class QueryConverterResult {

		private final Object params;

		private final Cursor cursor;

		QueryConverterResult(Object params, Cursor cursor) {
			this.params = params;
			this.cursor = cursor;
		}

		Object getParams() {
			return params;
		}

		Cursor getStatement() {
			return cursor;
		}
	}

	
	private static Object applyCollectionName(Object condition, String collection, JsonFactory fac) throws JsonException {
		if(StringUtil.isEmpty(collection)) {
			return condition;
		} else {
			if(condition == null) {
				return objOf(fac, EntityConverter.NAME_FIELD, collection);
			} else {
				return objOf(fac, BaseParser.Op.AND.getValue(), arrOf(fac,
					objOf(fac, EntityConverter.NAME_FIELD, collection),
					condition
				));
			}
		}
	}

	private static Object objOf(JsonFactory fac, String prop, Object value) throws JsonException {
		Object json = fac.createObject();
		fac.setProperty(json, prop, value);
		return json;
	}

	private static Object arrOf(JsonFactory fac, Object... elements) throws JsonException {
		Object arr = fac.createArray();
		for(Object e : elements) {
			fac.addArrayItem(arr, e);
		}
		return arr;
	}
}
