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

import com.darwino.commons.Platform;
import com.darwino.commons.tasks.Task;
import com.darwino.commons.tasks.TaskExecutor;
import com.darwino.commons.tasks.TaskExecutorService;
import com.darwino.commons.util.Callback;
import org.jnosql.diana.api.ExecuteAsyncQueryException;
import org.jnosql.diana.api.document.DocumentDeleteQuery;
import org.jnosql.diana.api.document.DocumentEntity;
import org.jnosql.diana.api.document.DocumentQuery;

import com.darwino.commons.json.JsonObject;
import com.darwino.jsonstore.Cursor;
import com.darwino.jsonstore.JsqlCursor;

import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;
import static java.util.Objects.requireNonNull;

class DefaultDarwinoDocumentCollectionManagerAsync implements DarwinoDocumentCollectionManagerAsync {

	private static final Consumer<DocumentEntity> NOOP = d -> {
	};

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
	public void insert(DocumentEntity entity, Consumer<DocumentEntity> callback) throws ExecuteAsyncQueryException, UnsupportedOperationException {
		requireNonNull(callback, "callback is required"); //$NON-NLS-1$

		Task<DocumentEntity> t = Task.from(context -> manager.insert(entity));
		TaskExecutor<DocumentEntity> exec = Platform.getService(TaskExecutorService.class).createExecutor(false);
		exec.exec(t, new Callback<DocumentEntity>() {
			@Override
			public Void success(DocumentEntity value) {
				callback.accept(value);
				return null;
			}
			@Override
			public void failure(Throwable t) {
				throw new ExecuteAsyncQueryException("On error when try to execute Darwino save method", t);
			}
		});
	}

	@Override
	public void insert(DocumentEntity entity, Duration ttl, Consumer<DocumentEntity> callback)
			throws ExecuteAsyncQueryException, UnsupportedOperationException {
		requireNonNull(callback, "callback is required"); //$NON-NLS-1$

		Task<DocumentEntity> t = Task.from(context -> manager.insert(entity, ttl));
		TaskExecutor<DocumentEntity> exec = Platform.getService(TaskExecutorService.class).createExecutor(false);
		exec.exec(t, new Callback<DocumentEntity>() {
			@Override
			public Void success(DocumentEntity value) {
				callback.accept(value);
				return null;
			}
			@Override
			public void failure(Throwable t) {
				throw new ExecuteAsyncQueryException("On error when try to execute Darwino save method", t);
			}
		});
	}

	@Override
	public void update(DocumentEntity entity) throws ExecuteAsyncQueryException, UnsupportedOperationException {
		update(entity, NOOP);
	}

	@Override
	public void update(DocumentEntity entity, Consumer<DocumentEntity> callback) throws ExecuteAsyncQueryException, UnsupportedOperationException {

		Task<DocumentEntity> t = Task.from(context -> manager.update(entity));
		TaskExecutor<DocumentEntity> exec = Platform.getService(TaskExecutorService.class).createExecutor(false);
		exec.exec(t, new Callback<DocumentEntity>() {
			@Override
			public Void success(DocumentEntity value) {
				callback.accept(entity);
				return null;
			}
			@Override
			public void failure(Throwable t) {
				throw new ExecuteAsyncQueryException("On error when try to execute Darwino save method", t);
			}
		});
	}

	@Override
	public void delete(DocumentDeleteQuery query) throws ExecuteAsyncQueryException, UnsupportedOperationException {
		delete(query, v -> {
		});
	}

	@Override
	public void delete(DocumentDeleteQuery query, Consumer<Void> callback) throws ExecuteAsyncQueryException, UnsupportedOperationException {
		requireNonNull(query, "query is required"); //$NON-NLS-1$
		requireNonNull(callback, "callback is required"); //$NON-NLS-1$

		Task<Void> t = Task.from(context -> { manager.delete(query); return null; });
		TaskExecutor<Void> exec = Platform.getService(TaskExecutorService.class).createExecutor(false);
		exec.exec(t, new Callback<Void>() {
			@Override
			public Void success(Void value) {
				callback.accept(null);
				return null;
			}
			@Override
			public void failure(Throwable t) {
				throw new ExecuteAsyncQueryException("On error when try to execute Darwino delete method", t);
			}
		});
	}

	@Override
	public void select(DocumentQuery query, Consumer<List<DocumentEntity>> callback) throws ExecuteAsyncQueryException, UnsupportedOperationException {
		requireNonNull(query, "query is required"); //$NON-NLS-1$
		requireNonNull(callback, "callback is required"); //$NON-NLS-1$

		Task<List<DocumentEntity>> t = Task.from(context -> manager.select(query));
		TaskExecutor<List<DocumentEntity>> exec = Platform.getService(TaskExecutorService.class).createExecutor(false);
		exec.exec(t, new Callback<List<DocumentEntity>>() {
			@Override
			public Void success(List<DocumentEntity> value) {
				callback.accept(value);
				return null;
			}
			@Override
			public void failure(Throwable t) {
				throw new ExecuteAsyncQueryException("On error when try to execute Darwino find method", t);
			}
		});
	}

	@Override
	public void query(String query, Object params, Consumer<List<DocumentEntity>> callback)
			throws NullPointerException, ExecuteAsyncQueryException {
		requireNonNull(callback, "callback is required"); //$NON-NLS-1$

		Task<List<DocumentEntity>> t = Task.from(context -> manager.query(query, params));
		TaskExecutor<List<DocumentEntity>> exec = Platform.getService(TaskExecutorService.class).createExecutor(false);
		exec.exec(t, new Callback<List<DocumentEntity>>() {
			@Override
			public Void success(List<DocumentEntity> value) {
				callback.accept(value);
				return null;
			}
			@Override
			public void failure(Throwable t) {
				throw new ExecuteAsyncQueryException("On error when try to execute Darwino query method", t);
			}
		});
	}

	@Override
	public void query(String query, Consumer<List<DocumentEntity>> callback) throws NullPointerException, ExecuteAsyncQueryException {
		requireNonNull(callback, "callback is required"); //$NON-NLS-1$

		Task<List<DocumentEntity>> t = Task.from(context -> manager.query(query));
		TaskExecutor<List<DocumentEntity>> exec = Platform.getService(TaskExecutorService.class).createExecutor(false);
		exec.exec(t, new Callback<List<DocumentEntity>>() {
			@Override
			public Void success(List<DocumentEntity> value) {
				callback.accept(value);
				return null;
			}
			@Override
			public void failure(Throwable t) {
				throw new ExecuteAsyncQueryException("On error when try to execute Darwino query method", t);
			}
		});
	}

	@Override
	public void query(Cursor cursor, Consumer<List<DocumentEntity>> callback) throws NullPointerException, ExecuteAsyncQueryException {
		requireNonNull(callback, "callback is required"); //$NON-NLS-1$

		Task<List<DocumentEntity>> t = Task.from(context -> manager.query(cursor));
		TaskExecutor<List<DocumentEntity>> exec = Platform.getService(TaskExecutorService.class).createExecutor(false);
		exec.exec(t, new Callback<List<DocumentEntity>>() {
			@Override
			public Void success(List<DocumentEntity> value) {
				callback.accept(value);
				return null;
			}
			@Override
			public void failure(Throwable t) {
				throw new ExecuteAsyncQueryException("On error when try to execute Darwino query method", t);
			}
		});
	}

	@Override
	public void close() {
		manager.close();
	}

	@Override
	public void search(String query, Consumer<List<DocumentEntity>> callback) throws ExecuteAsyncQueryException {
		requireNonNull(callback, "callback is required"); //$NON-NLS-1$

		Task<List<DocumentEntity>> t = Task.from(context -> manager.search(query));
		TaskExecutor<List<DocumentEntity>> exec = Platform.getService(TaskExecutorService.class).createExecutor(false);
		exec.exec(t, new Callback<List<DocumentEntity>>() {
			@Override
			public Void success(List<DocumentEntity> value) {
				callback.accept(value);
				return null;
			}
			@Override
			public void failure(Throwable t) {
				throw new ExecuteAsyncQueryException("On error when try to execute Darwino query method", t);
			}
		});
	}

	@Override
	public void jsqlQuery(String jsqlQuery, Object params, Consumer<List<DocumentEntity>> callback)
			throws NullPointerException, ExecuteAsyncQueryException {
		requireNonNull(callback, "callback is required"); //$NON-NLS-1$

		Task<List<DocumentEntity>> t = Task.from(context -> manager.jsqlQuery(jsqlQuery, params));
		TaskExecutor<List<DocumentEntity>> exec = Platform.getService(TaskExecutorService.class).createExecutor(false);
		exec.exec(t, new Callback<List<DocumentEntity>>() {
			@Override
			public Void success(List<DocumentEntity> value) {
				callback.accept(value);
				return null;
			}
			@Override
			public void failure(Throwable t) {
				throw new ExecuteAsyncQueryException("On error when try to execute Darwino query method", t);
			}
		});
	}

	@Override
	public void jsqlQuery(JsqlCursor jsqlQuery, Object params, Consumer<List<DocumentEntity>> callback)
			throws NullPointerException, ExecuteAsyncQueryException {
		requireNonNull(callback, "callback is required"); //$NON-NLS-1$

		Task<List<DocumentEntity>> t = Task.from(context -> manager.jsqlQuery(jsqlQuery, params));
		TaskExecutor<List<DocumentEntity>> exec = Platform.getService(TaskExecutorService.class).createExecutor(false);
		exec.exec(t, new Callback<List<DocumentEntity>>() {
			@Override
			public Void success(List<DocumentEntity> value) {
				callback.accept(value);
				return null;
			}
			@Override
			public void failure(Throwable t) {
				throw new ExecuteAsyncQueryException("On error when try to execute Darwino query method", t);
			}
		});
	}

	@Override
	public void jsqlQuery(String jsqlQuery, Consumer<List<DocumentEntity>> callback) throws NullPointerException, ExecuteAsyncQueryException {
		jsqlQuery(jsqlQuery, new JsonObject(), callback);
	}
	
	@Override
	public void storedCursor(String cursorName, Object params, Consumer<List<DocumentEntity>> callback) {
		requireNonNull(callback, "callback is required"); //$NON-NLS-1$

		Task<List<DocumentEntity>> t = Task.from(context -> manager.storedCursor(cursorName, params));
		TaskExecutor<List<DocumentEntity>> exec = Platform.getService(TaskExecutorService.class).createExecutor(false);
		exec.exec(t, new Callback<List<DocumentEntity>>() {
			@Override
			public Void success(List<DocumentEntity> value) {
				callback.accept(value);
				return null;
			}
			@Override
			public void failure(Throwable t) {
				throw new ExecuteAsyncQueryException("On error when try to execute Darwino query method", t);
			}
		});
	}

	@Override
	public void count(String documentCollection, Consumer<Long> callback) {
		requireNonNull(callback, "callback is required"); //$NON-NLS-1$

		Task<Long> t = Task.from(context -> manager.count(documentCollection));
		TaskExecutor<Long> exec = Platform.getService(TaskExecutorService.class).createExecutor(false);
		exec.exec(t, new Callback<Long>() {
			@Override
			public Void success(Long value) {
				callback.accept(value);
				return null;
			}
			@Override
			public void failure(Throwable t) {
				throw new ExecuteAsyncQueryException("On error when try to execute Darwino query method", t);
			}
		});
	}
	
	
}