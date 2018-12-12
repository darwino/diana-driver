package org.darwino.jnosql.artemis.extension;

import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Vetoed;
import javax.inject.Inject;

import org.darwino.jnosql.diana.driver.DarwinoDocumentCollectionManager;
import org.jnosql.artemis.Converters;
import org.jnosql.artemis.document.DocumentEntityConverter;
import org.jnosql.artemis.document.DocumentEventPersistManager;
import org.jnosql.artemis.document.DocumentWorkflow;
import org.jnosql.artemis.reflection.ClassMappings;

@ApplicationScoped
public class DefaultDarwinoTemplateProducer implements DarwinoTemplateProducer {
    @Inject
    private DocumentEntityConverter converter;

    @Inject
    private DocumentWorkflow workflow;

    @Inject
    private DocumentEventPersistManager persistManager;

    @Inject
    private ClassMappings mappings;

    @Inject
    private Converters converters;

	@Override
	public DarwinoTemplate get(DarwinoDocumentCollectionManager collectionManager) {
		Objects.requireNonNull(collectionManager, "collectionManager is required");
		
        return new ProducerDocumentTemplate(converter, collectionManager, workflow,
                persistManager, mappings, converters);
	}

    @Vetoed
    static class ProducerDocumentTemplate extends DefaultDarwinoTemplate {

        private DocumentEntityConverter converter;

        private DarwinoDocumentCollectionManager manager;

        private DocumentWorkflow workflow;

        private DocumentEventPersistManager persistManager;

        private Converters converters;

        private ClassMappings mappings;
        ProducerDocumentTemplate(DocumentEntityConverter converter, DarwinoDocumentCollectionManager manager,
                                 DocumentWorkflow workflow,
                                 DocumentEventPersistManager persistManager,
                                 ClassMappings mappings, Converters converters) {
            this.converter = converter;
            this.manager = manager;
            this.workflow = workflow;
            this.persistManager = persistManager;
            this.mappings = mappings;
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
		protected ClassMappings getClassMappings() {
			return mappings;
		}

        @Override
        protected Converters getConverters() {
            return converters;
        }
    }
}
