package org.darwino.jnosql.artemis.extension;

import org.darwino.jnosql.diana.driver.DarwinoDocumentCollectionManager;
import org.jnosql.artemis.Converters;
import org.jnosql.artemis.document.AbstractDocumentTemplate;
import org.jnosql.artemis.document.DocumentEntityConverter;
import org.jnosql.artemis.document.DocumentEventPersistManager;
import org.jnosql.artemis.document.DocumentWorkflow;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.diana.api.document.DocumentCollectionManager;

import com.darwino.commons.json.JsonObject;
import com.darwino.jsonstore.JsqlCursor;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * The Default implementation of {@link CouchbaseTemplate}
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
        return converter;
    }

    @Override
    protected DocumentCollectionManager getManager() {
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
        requireNonNull(jsqlQuery, "jsqlQuery is required");
        requireNonNull(params, "params is required");
        return manager.get().jsqlQuery(jsqlQuery, params).stream()
                .map(converter::toEntity)
                .map(d -> (T) d)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> jsqlQuery(JsqlCursor jsqlQuery, JsonObject params) throws NullPointerException {
        requireNonNull(jsqlQuery, "jsqlQuery is required");
        requireNonNull(params, "params is required");
        return manager.get().jsqlQuery(jsqlQuery, params).stream()
                .map(converter::toEntity)
                .map(d -> (T) d)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
	@Override
    public <T> List<T> jsqlQuery(String jsqlQuery) throws NullPointerException {
        requireNonNull(jsqlQuery, "jsqlQuery is required");
        return manager.get().jsqlQuery(jsqlQuery).stream()
                .map(converter::toEntity)
                .map(d -> (T) d)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> search(String query) throws NullPointerException {
        requireNonNull(query, "query is required");
        return manager.get().search(query).stream()
                .map(converter::toEntity)
                .map(d -> (T) d)
                .collect(Collectors.toList());
    }
}