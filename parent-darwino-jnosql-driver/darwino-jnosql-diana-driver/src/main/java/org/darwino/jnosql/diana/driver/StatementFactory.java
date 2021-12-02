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

import com.darwino.commons.json.JsonException;
import com.darwino.jsonstore.Cursor;
import com.darwino.platform.DarwinoContext;

final class StatementFactory {

	private StatementFactory() {
	}

	static Cursor create(String database, String store, String[] documents, int skip, int limit, String[] sorts) throws JsonException {
		if (sorts.length == 0) {
			return get(database, store, documents, skip, limit);
		} else {
			return get(database, store, documents, skip, limit, sorts);
		}
	}

	static Cursor create(String database, String store, String[] documents, int skip, int limit, String[] sorts, Object condition) throws JsonException {

		if (sorts.length == 0) {
			return get(database, store, documents, skip, limit, condition);
		} else {
			return get(database, store, documents, skip, limit, sorts, condition);
		}
	}

	private static Cursor get(String database, String store, String[] documents, int skip, int limit, Object condition) throws JsonException {

		boolean hasFistResult = skip > 0;
		boolean hasMaxResult = limit > 0;
		
		Cursor cursor = DarwinoContext.get().getSession().getDatabase(database).getStore(store).openCursor();

		if (hasFistResult && hasMaxResult) {
			return cursor
				.query(condition)
				.range(skip, limit);

		} else if (hasFistResult) {
			return cursor.query(condition).range(skip, -1);
		} else if (hasMaxResult) {
			return cursor.query(condition).range(0, limit);
		} else {
			return cursor.query(condition);
		}
	}

	private static Cursor get(String database, String store, String[] documents, int firstResult, int maxResult, String[] sorts, Object condition) throws JsonException {

		boolean hasFistResult = firstResult > 0;
		boolean hasMaxResult = maxResult > 0;

		Cursor cursor = DarwinoContext.get().getSession().getDatabase(database).getStore(store).openCursor();

		if (hasFistResult && hasMaxResult) {
			return cursor.query(condition).orderBy(sorts).range(firstResult, maxResult);
		} else if (hasFistResult) {
			return cursor.query(condition).orderBy(sorts).range(firstResult, -1);
		} else if (hasMaxResult) {
			return cursor.query(condition).orderBy(sorts).range(0, maxResult);
		} else {
			return cursor.query(condition).orderBy(sorts);
		}
	}

	private static Cursor get(String database, String store, String[] documents, int firstResult, int maxResult, String[] sorts) throws JsonException {

		boolean hasFistResult = firstResult > 0;
		boolean hasMaxResult = maxResult > 0;

		Cursor cursor = DarwinoContext.get().getSession().getDatabase(database).getStore(store).openCursor();

		if (hasFistResult && hasMaxResult) {
			return cursor.orderBy(sorts).range(firstResult, maxResult);
		} else if (hasFistResult) {
			return cursor.orderBy(sorts).range(firstResult, -1);
		} else if (hasMaxResult) {
			return cursor.orderBy(sorts).range(0, maxResult);
		} else {
			return cursor.orderBy(sorts);
		}
	}

	private static Cursor get(String database, String store, String[] documents, int firstResult, int maxResult) throws JsonException {

		boolean hasFistResult = firstResult > 0;
		boolean hasMaxResult = maxResult > 0;

		Cursor cursor = DarwinoContext.get().getSession().getDatabase(database).getStore(store).openCursor();

		if (hasFistResult && hasMaxResult) {
			return cursor.range(firstResult, maxResult);
		} else if (hasFistResult) {
			return cursor.range(firstResult, -1);
		} else if (hasMaxResult) {
			return cursor.range(0, maxResult);
		} else {
			return cursor;
		}
	}

}