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

import org.darwino.jnosql.diana.driver.DarwinoDocumentCollectionManager;
import org.jnosql.artemis.Converters;
import org.jnosql.artemis.document.AbstractDocumentTemplate;
import org.jnosql.artemis.document.DocumentEntityConverter;
import org.jnosql.artemis.document.DocumentEventPersistManager;
import org.jnosql.artemis.document.DocumentWorkflow;
import org.jnosql.artemis.reflection.ClassRepresentations;

import com.darwino.commons.json.JsonObject;
import com.darwino.jsonstore.JsqlCursor;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Typed;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

import java.util.Collection;

/**
 * The Default implementation of {@link DarwinoTemplate}
 */
@Typed(DarwinoTemplate.class)
class DefaultDarwinoTemplate extends AbstractDocumentTemplate
        implements DarwinoTemplate {

    private Instance<DarwinoDocumentCollectionManager> manager;

    private DocumentEntityConverter converter;

    private DocumentWorkflow flow;

    private DocumentEventPersistManager persistManager;

    private ClassRepresentations classRepresentations;

    private Converters converters;

    @Inject
    DefaultDarwinoTemplate(Instance<DarwinoDocumentCollectionManager> manager,
                             DocumentEntityConverter converter, DocumentWorkflow flow,
                             DocumentEventPersistManager persistManager,
                             ClassRepresentations classRepresentations,
                             Converters converters) {
        this.manager = manager;
        this.converter = converter;
        this.flow = flow;
        this.persistManager = persistManager;
        this.classRepresentations = classRepresentations;
        this.converters = converters;
    }

    DefaultDarwinoTemplate() {
    }

    @Override
    protected DocumentEntityConverter getConverter() {
		DocumentEntityConverter converter = this.converter == null ? CDI.current().select(DocumentEntityConverter.class).get() : this.converter;
		Objects.requireNonNull(converter, "Unable to acquire DocumentEntityConverter");
        return converter;
    }

    @Override
    protected DarwinoDocumentCollectionManager getManager() {
        return manager.get();
    }

    @Override
    protected DocumentWorkflow getWorkflow() {
        return flow;
    }

    @Override
    protected DocumentEventPersistManager getPersistManager() {
        return persistManager;
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
    public <T> List<T> jsqlQuery(String jsqlQuery, JsonObject params) throws NullPointerException {
        requireNonNull(jsqlQuery, "jsqlQuery is required"); //$NON-NLS-1$
        requireNonNull(params, "params is required"); //$NON-NLS-1$
        return getManager().jsqlQuery(jsqlQuery, params).stream()
                .map(getConverter()::toEntity)
                .map(d -> (T) d)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> jsqlQuery(JsqlCursor jsqlQuery, JsonObject params) throws NullPointerException {
        requireNonNull(jsqlQuery, "jsqlQuery is required"); //$NON-NLS-1$
        requireNonNull(params, "params is required"); //$NON-NLS-1$
        return getManager().jsqlQuery(jsqlQuery, params).stream()
                .map(getConverter()::toEntity)
                .map(d -> (T) d)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
	@Override
    public <T> List<T> jsqlQuery(String jsqlQuery) throws NullPointerException {
        requireNonNull(jsqlQuery, "jsqlQuery is required"); //$NON-NLS-1$
        return getManager().jsqlQuery(jsqlQuery).stream()
                .map(getConverter()::toEntity)
                .map(d -> (T) d)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> search(String query) throws NullPointerException {
        requireNonNull(query, "query is required"); //$NON-NLS-1$
        return getManager().search(query).stream()
                .map(getConverter()::toEntity)
                .map(d -> (T) d)
                .collect(Collectors.toList());
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> search(String query, Collection<String> orderBy) throws NullPointerException {
        requireNonNull(query, "query is required"); //$NON-NLS-1$
        return getManager().search(query, orderBy).stream()
                .map(getConverter()::toEntity)
                .map(d -> (T) d)
                .collect(Collectors.toList());
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public <T> List<T> storedCursor(String cursorName, JsonObject params) {
    	requireNonNull(cursorName, "cursorName is required"); //$NON-NLS-1$
        return getManager().storedCursor(cursorName, params).stream()
                .map(getConverter()::toEntity)
                .map(d -> (T) d)
                .collect(Collectors.toList());
    }
}