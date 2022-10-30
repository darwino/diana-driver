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
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.enterprise.inject.spi.PassivationCapable;

import org.darwino.jnosql.diana.driver.DarwinoDocumentCollectionManager;
import org.eclipse.jnosql.mapping.DatabaseQualifier;

import jakarta.nosql.mapping.DatabaseType;

public class DarwinoTemplateBean implements Bean<DarwinoTemplate>, PassivationCapable {

    private final BeanManager beanManager;

    private final Set<Type> types;

    private final String provider;

    private final Set<Annotation> qualifiers;

    /**
     * Constructor
     *
     * @param beanManager the beanManager
     * @param provider    the provider name, that must be a
     */
    public DarwinoTemplateBean(BeanManager beanManager, String provider) {
        this.beanManager = beanManager;
        this.types = Collections.singleton(DarwinoTemplate.class);
        this.provider = provider;
        this.qualifiers = Collections.singleton(DatabaseQualifier.ofDocument(provider));
    }

    @Override
    public Class<?> getBeanClass() {
        return DarwinoTemplate.class;
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
    public DarwinoTemplate create(CreationalContext<DarwinoTemplate> creationalContext) {

        DarwinoTemplateProducer producer = getInstance(DarwinoTemplateProducer.class);
        DarwinoDocumentCollectionManager manager = getManager();
        return producer.get(manager);
    }

    private DarwinoDocumentCollectionManager getManager() {
        @SuppressWarnings("unchecked")
		Bean<DarwinoDocumentCollectionManager> bean = (Bean<DarwinoDocumentCollectionManager>) beanManager.getBeans(DarwinoDocumentCollectionManager.class,
                DatabaseQualifier.ofDocument(provider) ).iterator().next();
        CreationalContext<DarwinoDocumentCollectionManager> ctx = beanManager.createCreationalContext(bean);
        return (DarwinoDocumentCollectionManager) beanManager.getReference(bean, DarwinoDocumentCollectionManager.class, ctx);
    }


    @SuppressWarnings("unchecked")
    private <T> T getInstance(Class<T> clazz) {
		Bean<T> bean = (Bean<T>) beanManager.getBeans(clazz).iterator().next();
        CreationalContext<T> ctx = beanManager.createCreationalContext(bean);
        return (T) beanManager.getReference(bean, clazz, ctx);
    }


    @Override
    public void destroy(DarwinoTemplate instance, CreationalContext<DarwinoTemplate> creationalContext) {

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
        return DarwinoTemplate.class.getName() + DatabaseType.DOCUMENT + '-' + provider;
    }

}