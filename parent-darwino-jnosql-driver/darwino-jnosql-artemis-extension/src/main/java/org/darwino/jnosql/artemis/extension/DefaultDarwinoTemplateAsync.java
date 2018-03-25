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
package org.darwino.jnosql.artemis.extension;

import org.darwino.jnosql.diana.driver.DarwinoDocumentCollectionManagerAsync;
import org.jnosql.artemis.Converters;
import org.jnosql.artemis.document.AbstractDocumentTemplateAsync;
import org.jnosql.artemis.document.DocumentEntityConverter;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.diana.api.ExecuteAsyncQueryException;
import org.jnosql.diana.api.document.DocumentCollectionManagerAsync;
import org.jnosql.diana.api.document.DocumentEntity;

import com.darwino.commons.json.JsonObject;
import com.darwino.jsonstore.JsqlCursor;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toList;

/**
 * The default implementation of {@link DarwinoTemplateAsync}
 */
@Typed(DarwinoTemplateAsync.class)
class DefaultDarwinoTemplateAsync extends AbstractDocumentTemplateAsync implements
        DarwinoTemplateAsync {

    private DocumentEntityConverter converter;

    private Instance<DarwinoDocumentCollectionManagerAsync> manager;

    private ClassRepresentations classRepresentations;

    private Converters converters;

    @Inject
    DefaultDarwinoTemplateAsync(DocumentEntityConverter converter,
                                  Instance<DarwinoDocumentCollectionManagerAsync> manager,
                                  ClassRepresentations classRepresentations, Converters converters) {
        this.converter = converter;
        this.manager = manager;
        this.classRepresentations = classRepresentations;
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
    protected ClassRepresentations getClassRepresentations() {
        return classRepresentations;
    }

    @Override
    protected Converters getConverters() {
        return converters;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> void jsqlQuery(String jsqlQuery, JsonObject params, Consumer<List<T>> callback)
            throws NullPointerException, ExecuteAsyncQueryException {

        Objects.requireNonNull(jsqlQuery, "jsqlQuery is required"); //$NON-NLS-1$
        Objects.requireNonNull(params, "params is required"); //$NON-NLS-1$
        Objects.requireNonNull(callback, "callback is required"); //$NON-NLS-1$
        Consumer<List<DocumentEntity>> dianaCallBack = d -> callback.accept(
                d.stream()
                        .map(getConverter()::toEntity)
                        .map(o -> (T) o)
                        .collect(toList()));
        manager.get().jsqlQuery(jsqlQuery, params, dianaCallBack);

    }

    @SuppressWarnings("unchecked")
	@Override
    public <T> void jsqlQuery(JsqlCursor jsqlQuery, JsonObject params, Consumer<List<T>> callback)
            throws NullPointerException, ExecuteAsyncQueryException {
        Objects.requireNonNull(jsqlQuery, "jsqlQuery is required"); //$NON-NLS-1$
        Objects.requireNonNull(params, "params is required"); //$NON-NLS-1$
        Objects.requireNonNull(callback, "callback is required"); //$NON-NLS-1$
        Consumer<List<DocumentEntity>> dianaCallBack = d -> callback.accept(
                d.stream()
                        .map(getConverter()::toEntity)
                        .map(o -> (T) o)
                        .collect(toList()));
        manager.get().jsqlQuery(jsqlQuery, params, dianaCallBack);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> void jsqlQuery(String jsqlQuery, Consumer<List<T>> callback)
            throws NullPointerException, ExecuteAsyncQueryException {

        Objects.requireNonNull(jsqlQuery, "jsqlQuery is required"); //$NON-NLS-1$
        Objects.requireNonNull(callback, "callback is required"); //$NON-NLS-1$

        Consumer<List<DocumentEntity>> dianaCallBack = d -> callback.accept(
                d.stream()
                        .map(getConverter()::toEntity)
                        .map(o -> (T) o)
                        .collect(toList()));
        manager.get().jsqlQuery(jsqlQuery, dianaCallBack);

    }

    @SuppressWarnings("unchecked")
	@Override
	public <T> void search(String query, Consumer<List<T>> callback) throws NullPointerException, ExecuteAsyncQueryException {
		 Objects.requireNonNull(query, "query is required"); //$NON-NLS-1$
	        Objects.requireNonNull(callback, "callback is required"); //$NON-NLS-1$

	        Consumer<List<DocumentEntity>> dianaCallBack = d -> callback.accept(
	                d.stream()
	                        .map(getConverter()::toEntity)
	                        .map(o -> (T) o)
	                        .collect(toList()));
	        manager.get().search(query, dianaCallBack);
	}
}