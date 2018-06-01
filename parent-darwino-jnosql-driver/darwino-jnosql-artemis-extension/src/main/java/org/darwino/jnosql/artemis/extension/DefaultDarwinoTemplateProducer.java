package org.darwino.jnosql.artemis.extension;

import java.util.List;
import java.util.Objects;

import javax.enterprise.inject.Vetoed;
import javax.inject.Inject;

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

public class DefaultDarwinoTemplateProducer implements DarwinoTemplateProducer {


    @Inject
    private DocumentEntityConverter converter;

    @Inject
    private DocumentWorkflow workflow;

    @Inject
    private DocumentEventPersistManager persistManager;

    @Inject
    private ClassRepresentations classRepresentations;

    @Inject
    private Converters converters;


    @Override
    public DarwinoTemplate get(DarwinoDocumentCollectionManager collectionManager) {
        Objects.requireNonNull(collectionManager, "collectionManager is required");
        return new ProducerDocumentTemplate(converter, collectionManager, workflow,
                persistManager, classRepresentations, converters);
    }

	@Override
	public DarwinoTemplate get(DocumentCollectionManager collectionManager) {
		return get((DarwinoDocumentCollectionManager)collectionManager);
	}

    @Vetoed
    static class ProducerDocumentTemplate extends AbstractDocumentTemplate implements DarwinoTemplate {

        private DocumentEntityConverter converter;

        private DocumentCollectionManager manager;

        private DocumentWorkflow workflow;

        private DocumentEventPersistManager persistManager;

        private Converters converters;

        private ClassRepresentations classRepresentations;
        ProducerDocumentTemplate(DocumentEntityConverter converter, DocumentCollectionManager manager,
                                 DocumentWorkflow workflow,
                                 DocumentEventPersistManager persistManager,
                                 ClassRepresentations classRepresentations, Converters converters) {
            this.converter = converter;
            this.manager = manager;
            this.workflow = workflow;
            this.persistManager = persistManager;
            this.classRepresentations = classRepresentations;
            this.converters = converters;
        }

        ProducerDocumentTemplate() {
        }

        @Override
        protected DocumentEntityConverter getConverter() {
            return converter;
        }

        @Override
        protected DocumentCollectionManager getManager() {
            return manager;
        }

        @Override
        protected DocumentWorkflow getWorkflow() {
            return workflow;
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

		@Override
		public <T> List<T> jsqlQuery(String jsqlQuery, JsonObject params) throws NullPointerException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <T> List<T> jsqlQuery(JsqlCursor jsqlQuery, JsonObject params) throws NullPointerException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <T> List<T> jsqlQuery(String jsqlQuery) throws NullPointerException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <T> List<T> search(String query) throws NullPointerException {
			// TODO Auto-generated method stub
			return null;
		}
    }
}