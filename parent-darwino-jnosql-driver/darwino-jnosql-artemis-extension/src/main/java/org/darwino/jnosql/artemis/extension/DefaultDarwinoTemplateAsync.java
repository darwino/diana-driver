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
package org.darwino.jnosql.artemis.extension;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;

import org.darwino.jnosql.diana.driver.DarwinoDocumentCollectionManagerAsync;
import org.eclipse.jnosql.artemis.document.AbstractDocumentTemplateAsync;

import com.darwino.jsonstore.JsqlCursor;

import jakarta.nosql.ExecuteAsyncQueryException;
import jakarta.nosql.document.DocumentCollectionManagerAsync;
import jakarta.nosql.document.DocumentEntity;
import jakarta.nosql.mapping.Converters;
import jakarta.nosql.mapping.document.DocumentEntityConverter;
import jakarta.nosql.mapping.reflection.ClassMappings;

/**
 * The default implementation of {@link DarwinoTemplateAsync}
 */
@Typed(DarwinoTemplateAsync.class)
class DefaultDarwinoTemplateAsync extends AbstractDocumentTemplateAsync implements
        DarwinoTemplateAsync {

    private DocumentEntityConverter converter;

    private Instance<DarwinoDocumentCollectionManagerAsync> manager;

    private ClassMappings mappings;

    private Converters converters;

    @Inject
    DefaultDarwinoTemplateAsync(DocumentEntityConverter converter,
                                  Instance<DarwinoDocumentCollectionManagerAsync> manager,
                                  ClassMappings mappings, Converters converters) {
        this.converter = converter;
        this.manager = manager;
        this.mappings = mappings;
        this.converters = converters;
    }

    DefaultDarwinoTemplateAsync() {
    }

    @Override
    protected DocumentEntityConverter getConverter() {
        return converter;
    }

    @Override
    protected DocumentCollectionManagerAsync getManager() {
        return manager.get();
    }

    @Override
    protected ClassMappings getClassMappings() {
    	return mappings;
    }

    @Override
    protected Converters getConverters() {
        return converters;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> void jsqlQuery(String jsqlQuery, Object params, Consumer<Stream<T>> callback)
            throws NullPointerException, ExecuteAsyncQueryException {

        Objects.requireNonNull(jsqlQuery, "jsqlQuery is required"); //$NON-NLS-1$
        Objects.requireNonNull(params, "params is required"); //$NON-NLS-1$
        Objects.requireNonNull(callback, "callback is required"); //$NON-NLS-1$
        Consumer<Stream<DocumentEntity>> dianaCallBack = d -> callback.accept(
                d
                        .map(getConverter()::toEntity)
                        .map(o -> (T) o));
        manager.get().jsqlQuery(jsqlQuery, params, dianaCallBack);

    }

	@SuppressWarnings("unchecked")
	@Override
    public <T> void jsqlQuery(JsqlCursor jsqlQuery, Object params, Consumer<Stream<T>> callback)
            throws NullPointerException, ExecuteAsyncQueryException {
        Objects.requireNonNull(jsqlQuery, "jsqlQuery is required"); //$NON-NLS-1$
        Objects.requireNonNull(params, "params is required"); //$NON-NLS-1$
        Objects.requireNonNull(callback, "callback is required"); //$NON-NLS-1$
        Consumer<Stream<DocumentEntity>> dianaCallBack = d -> callback.accept(
                d
                        .map(getConverter()::toEntity)
                        .map(o -> (T) o));
        manager.get().jsqlQuery(jsqlQuery, params, dianaCallBack);
    }

	@SuppressWarnings("unchecked")
    @Override
    public <T> void jsqlQuery(String jsqlQuery, Consumer<Stream<T>> callback)
            throws NullPointerException, ExecuteAsyncQueryException {

        Objects.requireNonNull(jsqlQuery, "jsqlQuery is required"); //$NON-NLS-1$
        Objects.requireNonNull(callback, "callback is required"); //$NON-NLS-1$

        Consumer<Stream<DocumentEntity>> dianaCallBack = d -> callback.accept(
                d
                	.map(getConverter()::toEntity)
                	.map(o -> (T) o));
        manager.get().jsqlQuery(jsqlQuery, dianaCallBack);

    }

    @SuppressWarnings("unchecked")
	@Override
	public <T> void search(String query, Consumer<Stream<T>> callback) throws NullPointerException, ExecuteAsyncQueryException {
		 Objects.requireNonNull(query, "query is required"); //$NON-NLS-1$
	        Objects.requireNonNull(callback, "callback is required"); //$NON-NLS-1$

	        Consumer<Stream<DocumentEntity>> dianaCallBack = d -> callback.accept(
	                d
						.map(getConverter()::toEntity)
						.map(o -> (T) o));
	        manager.get().search(query, dianaCallBack);
	}
    

    
    @SuppressWarnings("unchecked")
	@Override
    public <T> void storedCursor(String cursorName, Object params, Consumer<Stream<T>> callback) {
    	Objects.requireNonNull(cursorName, "query is required"); //$NON-NLS-1$
        Objects.requireNonNull(callback, "callback is required"); //$NON-NLS-1$

        Consumer<Stream<DocumentEntity>> dianaCallBack = d -> callback.accept(
                d
                        .map(getConverter()::toEntity)
                        .map(o -> (T) o));
        manager.get().storedCursor(cursorName, params, dianaCallBack);
    }
}