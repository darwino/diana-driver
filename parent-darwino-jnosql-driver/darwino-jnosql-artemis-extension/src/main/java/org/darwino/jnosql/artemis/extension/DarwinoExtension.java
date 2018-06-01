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

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessProducer;

import org.darwino.jnosql.diana.driver.DarwinoDocumentCollectionManager;
import org.darwino.jnosql.diana.driver.DarwinoDocumentCollectionManagerAsync;
import org.jnosql.artemis.DatabaseMetadata;
import org.jnosql.artemis.Databases;

import static org.jnosql.artemis.DatabaseType.DOCUMENT;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class DarwinoExtension implements Extension {

	private static final Logger LOGGER = Logger.getLogger(DarwinoExtension.class.getName());
	
    private final Set<DatabaseMetadata> databases = new HashSet<>();

    private final Set<DatabaseMetadata> databasesAsync = new HashSet<>();

	private final Collection<Class<?>> crudTypes = new HashSet<>();

	private final Collection<Class<?>> crudAsyncTypes = new HashSet<>();

	@SuppressWarnings("rawtypes")
	<T extends DarwinoRepository> void onProcessAnnotatedType(@Observes final ProcessAnnotatedType<T> repo) {
		Class<T> javaClass = repo.getAnnotatedType().getJavaClass();

		if (DarwinoRepository.class.equals(javaClass)) {
			return;
		}

		if (Stream.of(javaClass.getInterfaces()).anyMatch(DarwinoRepository.class::equals) && Modifier.isInterface(javaClass.getModifiers())) {
			crudTypes.add(javaClass);
		}
	}

	@SuppressWarnings("rawtypes")
	<T extends DarwinoRepositoryAsync> void onProcessAnnotatedTypeAsync(@Observes final ProcessAnnotatedType<T> repo) {
		Class<T> javaClass = repo.getAnnotatedType().getJavaClass();

		if (DarwinoRepositoryAsync.class.equals(javaClass)) {
			return;
		}

		if (Stream.of(javaClass.getInterfaces()).anyMatch(DarwinoRepositoryAsync.class::equals) && Modifier.isInterface(javaClass.getModifiers())) {
			crudAsyncTypes.add(javaClass);
		}
	}
	
	<T, X extends DarwinoDocumentCollectionManager> void processProducer(@Observes final ProcessProducer<T, X> pp) {
        Databases.addDatabase(pp, DOCUMENT, databases);
    }

    <T, X extends DarwinoDocumentCollectionManagerAsync> void processProducerAsync(@Observes final ProcessProducer<T, X> pp) {
        Databases.addDatabase(pp, DOCUMENT, databasesAsync);
    }

	void onAfterBeanDiscovery(@Observes final AfterBeanDiscovery afterBeanDiscovery, final BeanManager beanManager) {
		LOGGER.info("Starting the onAfterBeanDiscovery with elements number: " + crudTypes.size()); //$NON-NLS-1$
		
		databases.forEach(type -> {
            final DarwinoTemplateBean bean = new DarwinoTemplateBean(beanManager, type.getProvider());
            afterBeanDiscovery.addBean(bean);
        });

        databasesAsync.forEach(type -> {
            final DarwinoTemplateAsyncBean bean = new DarwinoTemplateAsyncBean(beanManager, type.getProvider());
            afterBeanDiscovery.addBean(bean);
        });

		crudTypes.forEach(type -> afterBeanDiscovery.addBean(new DarwinoRepositoryBean(type, beanManager)));

		crudAsyncTypes.forEach(type -> afterBeanDiscovery.addBean(new DarwinoRepositoryAsyncBean(type, beanManager)));

		LOGGER.info("Finished the onAfterBeanDiscovery"); //$NON-NLS-1$
	}
}
