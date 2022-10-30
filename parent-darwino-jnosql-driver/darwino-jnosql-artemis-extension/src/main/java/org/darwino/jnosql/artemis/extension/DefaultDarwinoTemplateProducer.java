/**
 * Copyright © 2017-2021 Jesse Gallagher
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

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Vetoed;
import jakarta.inject.Inject;

import org.darwino.jnosql.diana.driver.DarwinoDocumentCollectionManager;
import org.eclipse.jnosql.mapping.reflection.ClassMappings;

import jakarta.nosql.mapping.Converters;
import jakarta.nosql.mapping.document.DocumentEntityConverter;
import jakarta.nosql.mapping.document.DocumentEventPersistManager;
import jakarta.nosql.mapping.document.DocumentWorkflow;

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
