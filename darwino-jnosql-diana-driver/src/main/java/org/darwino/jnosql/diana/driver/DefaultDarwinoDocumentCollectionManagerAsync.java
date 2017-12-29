package org.darwino.jnosql.diana.driver;

import org.jnosql.diana.api.ExecuteAsyncQueryException;
import org.jnosql.diana.api.document.DocumentDeleteQuery;
import org.jnosql.diana.api.document.DocumentEntity;
import org.jnosql.diana.api.document.DocumentQuery;

import com.darwino.commons.json.JsonObject;
import com.darwino.jsonstore.Cursor;

import rx.functions.Action1;

import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;
import static rx.Observable.just;

class DefaultDarwinoDocumentCollectionManagerAsync implements DarwinoDocumentCollectionManagerAsync {

	private static final Consumer<DocumentEntity> NOOP = d -> {
	};
	private static final Action1<Throwable> ERROR_SAVE = a -> new ExecuteAsyncQueryException("On error when try to execute Darwino save method");
	private static final Action1<Throwable> ERROR_FIND = a -> new ExecuteAsyncQueryException("On error when try to execute Darwino find method");
	private static final Action1<Throwable> ERROR_DELETE = a -> new ExecuteAsyncQueryException("On error when try to execute Darwino delete method");
	private static final Action1<Throwable> ERROR_QUERY = a -> new ExecuteAsyncQueryException("On error when try to execute Darwino query method");

	private final DarwinoDocumentCollectionManager manager;

	DefaultDarwinoDocumentCollectionManagerAsync(DarwinoDocumentCollectionManager manager) {
		this.manager = manager;
	}

	@Override
	public void insert(DocumentEntity entity) throws ExecuteAsyncQueryException, UnsupportedOperationException {
		insert(entity, NOOP);
	}

	@Override
	public void insert(DocumentEntity entity, Duration ttl) throws ExecuteAsyncQueryException, UnsupportedOperationException {
		insert(entity, ttl, NOOP);
	}

	@Override
	public void insert(DocumentEntity entity, Consumer<DocumentEntity> callBack) throws ExecuteAsyncQueryException, UnsupportedOperationException {
		requireNonNull(callBack, "callBack is required");
		just(entity).map(manager::insert).subscribe(callBack::accept, ERROR_SAVE);
	}

	@Override
	public void insert(DocumentEntity entity, Duration ttl, Consumer<DocumentEntity> callBack)
			throws ExecuteAsyncQueryException, UnsupportedOperationException {
		requireNonNull(callBack, "callBack is required");
		just(entity).map(e -> manager.insert(e, ttl)).subscribe(callBack::accept, ERROR_SAVE);
	}

	@Override
	public void update(DocumentEntity entity) throws ExecuteAsyncQueryException, UnsupportedOperationException {
		insert(entity);
	}

	@Override
	public void update(DocumentEntity entity, Consumer<DocumentEntity> callBack) throws ExecuteAsyncQueryException, UnsupportedOperationException {
		insert(entity, callBack);
	}

	@Override
	public void delete(DocumentDeleteQuery query) throws ExecuteAsyncQueryException, UnsupportedOperationException {
		delete(query, v -> {
		});
	}

	@Override
	public void delete(DocumentDeleteQuery query, Consumer<Void> callBack) throws ExecuteAsyncQueryException, UnsupportedOperationException {
		requireNonNull(query, "query is required");
		requireNonNull(callBack, "callBack is required");
		just(query).map(q -> {
			manager.delete(q);
			return true;
		}).subscribe(a -> callBack.accept(null), ERROR_DELETE);
	}

	@Override
	public void select(DocumentQuery query, Consumer<List<DocumentEntity>> callBack) throws ExecuteAsyncQueryException, UnsupportedOperationException {
		just(query).map(manager::select).subscribe(callBack::accept, ERROR_FIND);
	}

	@Override
	public void query(String query, JsonObject params, Consumer<List<DocumentEntity>> callback)
			throws NullPointerException, ExecuteAsyncQueryException {
		requireNonNull(callback, "callback is required");
		just(query).map(n -> manager.query(n, params)).subscribe(callback::accept, ERROR_QUERY);
	}

	@Override
	public void query(String query, Consumer<List<DocumentEntity>> callback) throws NullPointerException, ExecuteAsyncQueryException {
		requireNonNull(callback, "callback is required");
		just(query).map(manager::query).subscribe(callback::accept, ERROR_QUERY);
	}

	@Override
	public void query(Cursor cursor, Consumer<List<DocumentEntity>> callback) throws NullPointerException, ExecuteAsyncQueryException {
		requireNonNull(callback, "callback is required");
		just(cursor).map(manager::query).subscribe(callback::accept, ERROR_QUERY);
	}

	@Override
	public void close() {
		manager.close();
	}
}