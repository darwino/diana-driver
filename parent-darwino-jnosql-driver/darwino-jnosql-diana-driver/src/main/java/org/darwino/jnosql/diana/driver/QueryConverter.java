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

import org.jnosql.diana.api.Condition;
import org.jnosql.diana.api.Sort.SortType;
import org.jnosql.diana.api.TypeReference;
import org.jnosql.diana.api.document.Document;
import org.jnosql.diana.api.document.DocumentCondition;
import org.jnosql.diana.api.document.DocumentDeleteQuery;
import org.jnosql.diana.api.document.DocumentQuery;

import com.darwino.commons.json.JsonArray;
import com.darwino.commons.json.JsonException;
import com.darwino.commons.json.JsonObject;
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
import static org.jnosql.diana.api.Condition.IN;
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

	static QueryConverterResult select(DocumentQuery query, String database, String store) throws JsonException {
		JsonObject params = new JsonObject();
		String[] documents = query.getDocuments().stream().toArray(size -> new String[size]);
		if (documents.length == 0) {
			documents = ALL_SELECT;
		}

		Cursor statement = null;
		int firstResult = (int) query.getFirstResult();
		int maxResult = (int) query.getMaxResults();

		String[] sorts = query.getSorts().stream().map(s -> s.getName() + (s.getType() == SortType.DESC ? " d" : "")).toArray(String[]::new); //$NON-NLS-1$ //$NON-NLS-2$

		if (query.getCondition().isPresent()) {
			JsonObject condition = getCondition(query.getCondition().get(), params);
			// Add in the form property if needed
			condition = applyCollectionName(condition, query.getDocumentCollection());
			if (nonNull(condition)) {
				statement = create(database, store, documents, firstResult, maxResult, sorts, condition);
			} else {
				statement = null;
			}
		} else {
			JsonObject condition = applyCollectionName(null, query.getDocumentCollection());
			if(condition != null) {
				statement = create(database, store, documents, firstResult, maxResult, sorts, condition);
			} else {
				statement = create(database, store, documents, firstResult, maxResult, sorts);
			}
		}
		return new QueryConverterResult(params, statement);
	}

	static QueryConverterResult delete(DocumentDeleteQuery query, String database, String store) throws JsonException {
		JsonObject params = new JsonObject();
		JsonObject condition = getCondition(query.getCondition().orElseThrow(() -> new IllegalArgumentException("Condition is required")), params); //$NON-NLS-1$
		
		// Add in the form property if needed
		condition = applyCollectionName(condition, query.getDocumentCollection());
		
		Cursor cursor = DarwinoContext.get().getSession().getDatabase(database).getStore(store).openCursor();
		if (nonNull(condition)) {
			cursor.query(condition);
		}

		return new QueryConverterResult(params, cursor);
	}

	private static JsonObject getCondition(DocumentCondition condition, JsonObject params) {
		Document document = condition.getDocument();

		if (!NOT_APPENDABLE.contains(condition.getCondition())) {
			params.put(document.getName(), document.get());
		}

		// Convert special names
		String name = document.getName();
		if (StringUtil.equals(name, EntityConverter.ID_FIELD)) {
			name = SpecialFieldNode.UNID;
		}

		Object placeholder = document.get();
		switch (condition.getCondition()) {
		case EQUALS:
			return JsonObject.of(name, JsonObject.of(BaseParser.Op.EQ.getValue(), placeholder));
		case LESSER_THAN:
			return JsonObject.of(name, JsonObject.of(BaseParser.Op.LT.getValue(), placeholder));
		case LESSER_EQUALS_THAN:
			return JsonObject.of(name, JsonObject.of(BaseParser.Op.LTE.getValue(), placeholder));
		case GREATER_THAN:
			return JsonObject.of(name, JsonObject.of(BaseParser.Op.GT.getValue(), placeholder));
		case GREATER_EQUALS_THAN:
			return JsonObject.of(name, JsonObject.of(BaseParser.Op.GTE.getValue(), placeholder));
		case LIKE:
			return JsonObject.of(name, JsonObject.of(BaseParser.Op.LIKE.getValue(), placeholder));
		case IN:
			return JsonObject.of(name, JsonObject.of(BaseParser.Op.IN.getValue(), placeholder));
		case AND:
			return JsonObject.of(BaseParser.Op.AND.getValue(), JsonArray.of(document.get(new TypeReference<List<DocumentCondition>>() {
			}).stream().map(d -> getCondition(d, params)).filter(Objects::nonNull).toArray()));
		case OR:
			return JsonObject.of(BaseParser.Op.OR.getValue(), JsonArray.of(document.get(new TypeReference<List<DocumentCondition>>() {
			}).stream().map(d -> getCondition(d, params)).filter(Objects::nonNull).toArray()));
		case NOT:
			DocumentCondition dc = document.get(DocumentCondition.class);
			return JsonObject.of(BaseParser.Op.NOT.getValue(), getCondition(dc, params));
		default:
			throw new IllegalStateException("This condition is not supported in Darwino: " + condition.getCondition()); //$NON-NLS-1$
		}
	}

	static class QueryConverterResult {

		private final JsonObject params;

		private final Cursor cursor;

		QueryConverterResult(JsonObject params, Cursor cursor) {
			this.params = params;
			this.cursor = cursor;
		}

		JsonObject getParams() {
			return params;
		}

		Cursor getStatement() {
			return cursor;
		}
	}

	
	private static JsonObject applyCollectionName(JsonObject condition, String collection) {
		if(StringUtil.isEmpty(collection)) {
			return condition;
		} else {
			if(condition == null) {
				return JsonObject.of(EntityConverter.NAME_FIELD, collection);
			} else {
				return JsonObject.of(BaseParser.Op.AND.getValue(), JsonArray.of(
					JsonObject.of(EntityConverter.NAME_FIELD, collection),
					condition
				));
			}
		}
	}
}
