package org.darwino.jnosql.artemis.extension;

import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Vetoed;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;

import org.darwino.jnosql.diana.driver.DarwinoDocumentCollectionManager;
import org.jnosql.artemis.Converters;
import org.jnosql.artemis.document.DocumentEntityConverter;
import org.jnosql.artemis.document.DocumentEventPersistManager;
import org.jnosql.artemis.document.DocumentWorkflow;
import org.jnosql.artemis.reflection.ClassRepresentations;

@ApplicationScoped
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

    @Vetoed
    static class ProducerDocumentTemplate extends DefaultDarwinoTemplate {

        private DocumentEntityConverter converter;

        private DarwinoDocumentCollectionManager manager;

        private DocumentWorkflow workflow;

        private DocumentEventPersistManager persistManager;

        private Converters converters;

        private ClassRepresentations classRepresentations;
        ProducerDocumentTemplate(DocumentEntityConverter converter, DarwinoDocumentCollectionManager manager,
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
        protected DarwinoDocumentCollectionManager getManager() {
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
    }
}
