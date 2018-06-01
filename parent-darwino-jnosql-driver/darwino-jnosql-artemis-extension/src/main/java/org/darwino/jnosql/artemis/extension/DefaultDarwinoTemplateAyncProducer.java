package org.darwino.jnosql.artemis.extension;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import javax.enterprise.inject.Vetoed;
import javax.inject.Inject;

import org.jnosql.artemis.Converters;
import org.jnosql.artemis.document.AbstractDocumentTemplateAsync;
import org.jnosql.artemis.document.DocumentEntityConverter;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.diana.api.ExecuteAsyncQueryException;
import org.jnosql.diana.api.document.DocumentCollectionManagerAsync;

import com.darwino.commons.json.JsonObject;
import com.darwino.jsonstore.JsqlCursor;

public class DefaultDarwinoTemplateAyncProducer implements DarwinoTemplateAsyncProducer {


    @Inject
    private DocumentEntityConverter converter;

    @Inject
    private ClassRepresentations classRepresentations;

    @Inject
    private Converters converters;


    @Override
    public DarwinoTemplateAsync get(DocumentCollectionManagerAsync collectionManager) {
        Objects.requireNonNull(collectionManager, "collectionManager is required");
        return new ProducerAbstractDocumentTemplateAsync(converter, collectionManager, classRepresentations, converters);
    }

    @Vetoed
    static class ProducerAbstractDocumentTemplateAsync extends AbstractDocumentTemplateAsync implements DarwinoTemplateAsync {

        private DocumentEntityConverter converter;

        private DocumentCollectionManagerAsync manager;

        private ClassRepresentations classRepresentations;

        private Converters converters;

        ProducerAbstractDocumentTemplateAsync(DocumentEntityConverter converter,
                                              DocumentCollectionManagerAsync manager,
                                              ClassRepresentations classRepresentations,
                                              Converters converters) {
            this.converter = converter;
            this.manager = manager;
            this.classRepresentations = classRepresentations;
            this.converters = converters;
        }

        ProducerAbstractDocumentTemplateAsync() {
        }

        @Override
        protected DocumentEntityConverter getConverter() {
            return converter;
        }

        @Override
        protected DocumentCollectionManagerAsync getManager() {
            return manager;
        }

        @Override
        protected ClassRepresentations getClassRepresentations() {
            return classRepresentations;
        }

        @Override
        protected Converters getConverters() {
            return converters;
        }

		@Override
		public <T> void jsqlQuery(String jsqlQuery, JsonObject params, Consumer<List<T>> callback)
				throws NullPointerException, ExecuteAsyncQueryException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public <T> void jsqlQuery(JsqlCursor jsqlQuery, JsonObject params, Consumer<List<T>> callback)
				throws NullPointerException, ExecuteAsyncQueryException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public <T> void jsqlQuery(String jsqlQuery, Consumer<List<T>> callback)
				throws NullPointerException, ExecuteAsyncQueryException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public <T> void search(String query, Consumer<List<T>> callback)
				throws NullPointerException, ExecuteAsyncQueryException {
			// TODO Auto-generated method stub
			
		}
    }
}