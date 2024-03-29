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

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.enterprise.inject.spi.PassivationCapable;
import jakarta.enterprise.util.AnnotationLiteral;

import org.eclipse.jnosql.mapping.DatabaseQualifier;

import jakarta.nosql.mapping.Repository;
import jakarta.nosql.mapping.document.DocumentRepositoryProducer;

public class DarwinoRepositoryBean implements Bean<DarwinoRepository<?, ?>>, PassivationCapable {

	private final Class<?> type;
	private final BeanManager beanManager;
	private final Set<Type> types;
	
	@SuppressWarnings("serial")
	private final Set<Annotation> qualifiers = Collections.singleton(new AnnotationLiteral<Default>() { });

	DarwinoRepositoryBean(Class<?> type, BeanManager beanManager) {
    		this.type = type;
    		this.beanManager = beanManager;
    		this.types = Collections.singleton(type);
    }

	@Override
	public Class<?> getBeanClass() {
		return type;
	}

	@Override
	public Set<InjectionPoint> getInjectionPoints() {
		return Collections.emptySet();
	}

	@Override
	public boolean isNullable() {
		return false;
	}

	@Override
	public DarwinoRepository<?, ?> create(CreationalContext<DarwinoRepository<?, ?>> creationalContext) {
		DarwinoTemplate template;
		RepositoryProvider producerAnnotation = type.getAnnotation(RepositoryProvider.class);
		if(producerAnnotation != null) {
			template = getInstance(DarwinoTemplate.class, producerAnnotation.value());
		} else {
			template = getInstance(DarwinoTemplate.class);
		}
		DocumentRepositoryProducer producer = getInstance(DocumentRepositoryProducer.class);
		@SuppressWarnings("unchecked")
		Repository<Object, Object> repository = producer.get((Class<Repository<Object, Object>>) type, template);

        DarwinoDocumentRepositoryProxy<DarwinoRepository<?, ?>> handler = new DarwinoDocumentRepositoryProxy<>(template, type, repository);
        return (DarwinoRepository<?, ?>) Proxy.newProxyInstance(type.getClassLoader(),
                new Class[]{type},
                handler);
	}
	
	@SuppressWarnings("unchecked")
	private <T> T getInstance(Class<T> clazz) {
        Bean<T> bean = (Bean<T>) beanManager.getBeans(clazz).iterator().next();
        CreationalContext<T> ctx = beanManager.createCreationalContext(bean);
        return (T) beanManager.getReference(bean, clazz, ctx);
    }
	
	@SuppressWarnings("unchecked")
	private <T> T getInstance(Class<T> clazz, String provider) {
        Bean<T> bean = (Bean<T>) beanManager.getBeans(clazz, DatabaseQualifier.ofDocument(provider)).iterator().next();
        CreationalContext<T> ctx = beanManager.createCreationalContext(bean);
        return (T) beanManager.getReference(bean, clazz, ctx);
    }

	@Override
	public void destroy(DarwinoRepository<?, ?> instance, CreationalContext<DarwinoRepository<?, ?>> creationalContext) {
		
	}

	@Override
	public Set<Type> getTypes() {
		return types;
	}

	@Override
	public Set<Annotation> getQualifiers() {
		return qualifiers;
	}

	@Override
	public Class<? extends Annotation> getScope() {
		return ApplicationScoped.class;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public Set<Class<? extends Annotation>> getStereotypes() {
		return Collections.emptySet();
	}

	@Override
	public boolean isAlternative() {
		return false;
	}

	@Override
	public String getId() {
		return type.getName() + "@darwino"; //$NON-NLS-1$
	}

}
