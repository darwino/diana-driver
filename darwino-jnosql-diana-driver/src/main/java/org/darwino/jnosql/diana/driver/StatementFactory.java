package org.darwino.jnosql.diana.driver;

import com.darwino.commons.json.JsonException;
import com.darwino.commons.json.JsonObject;
import com.darwino.jsonstore.Cursor;
import com.darwino.platform.DarwinoContext;

final class StatementFactory {

	private StatementFactory() {
	}

	static Cursor create(String database, String store, String[] documents, int firstResult, int maxResult, String[] sorts) throws JsonException {
		if (sorts.length == 0) {
			return get(database, store, documents, firstResult, maxResult);
		} else {
			return get(database, store, documents, firstResult, maxResult, sorts);
		}
	}

	static Cursor create(String database, String store, String[] documents, int firstResult, int maxResult, String[] sorts, JsonObject condition) throws JsonException {

		if (sorts.length == 0) {
			return get(database, store, documents, firstResult, maxResult, condition);
		} else {
			return get(database, store, documents, firstResult, maxResult, sorts, condition);
		}
	}

	private static Cursor get(String database, String store, String[] documents, int firstResult, int maxResult, JsonObject condition) throws JsonException {

		boolean hasFistResult = firstResult > 0;
		boolean hasMaxResult = maxResult > 0;
		
		Cursor cursor = DarwinoContext.get().getSession().getDatabase(database).getStore(store).openCursor();

		if (hasFistResult && hasMaxResult) {
			return cursor
				.query(condition)
				.range(firstResult, maxResult);

		} else if (hasFistResult) {
			return cursor.query(condition).range(firstResult, -1);
		} else if (hasMaxResult) {
			return cursor.query(condition).range(0, maxResult);
		} else {
			return cursor.query(condition);
		}
	}

	private static Cursor get(String database, String store, String[] documents, int firstResult, int maxResult, String[] sorts, JsonObject condition) throws JsonException {

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