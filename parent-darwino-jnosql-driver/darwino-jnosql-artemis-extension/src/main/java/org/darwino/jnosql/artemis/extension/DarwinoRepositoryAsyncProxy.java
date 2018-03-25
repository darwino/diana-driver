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

import org.jnosql.artemis.Converters;
import org.jnosql.artemis.RepositoryAsync;
import org.jnosql.artemis.document.DocumentTemplateAsync;
import org.jnosql.artemis.document.query.AbstractDocumentRepositoryAsync;
import org.jnosql.artemis.document.query.AbstractDocumentRepositoryAsyncProxy;
import org.jnosql.artemis.document.query.DocumentQueryDeleteParser;
import org.jnosql.artemis.document.query.DocumentQueryParser;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.artemis.reflection.Reflections;

import com.darwino.commons.json.JsonObject;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Objects;
import java.util.function.Consumer;

public class DarwinoRepositoryAsyncProxy<T> extends AbstractDocumentRepositoryAsyncProxy<T> {

	@SuppressWarnings("rawtypes")
	private static final Consumer NOOP = t -> {
    };

    private final Class<T> typeClass;

    private final DarwinoTemplateAsync template;


    private final DocumentCrudRepositoryAsync repository;

    private final ClassRepresentation classRepresentation;

    private final DocumentQueryParser queryParser;

    private final DocumentQueryDeleteParser deleteParser;

    private  final Converters converters;


    @SuppressWarnings("unchecked")
	DarwinoRepositoryAsyncProxy(DarwinoTemplateAsync template, ClassRepresentations classRepresentations,
                                  Class<?> repositoryType, Reflections reflections, Converters converters) {
        this.template = template;
        this.typeClass = Class.class.cast(ParameterizedType.class.cast(repositoryType.getGenericInterfaces()[0])
                .getActualTypeArguments()[0]);
        this.classRepresentation = classRepresentations.get(typeClass);
        this.queryParser = new DocumentQueryParser();
        this.repository = new DocumentCrudRepositoryAsync(template, classRepresentation, reflections);
        this.deleteParser = new DocumentQueryDeleteParser();
        this.converters = converters;
    }


    @SuppressWarnings("rawtypes")
	@Override
    protected RepositoryAsync getRepository() {
        return repository;
    }

    @Override
    protected DocumentQueryParser getQueryParser() {
        return queryParser;
    }

    @Override
    protected DocumentTemplateAsync getTemplate() {
        return template;
    }

    @Override
    protected DocumentQueryDeleteParser getDeleteParser() {
        return deleteParser;
    }

    @Override
    protected ClassRepresentation getClassRepresentation() {
        return classRepresentation;
    }

    @Override
    protected Converters getConverters() {
        return converters;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public Object invoke(Object instance, Method method, Object[] args) throws Throwable {

        JSQL jsql = method.getAnnotation(JSQL.class);
        if (Objects.nonNull(jsql)) {
            Consumer callBack = NOOP;
            if (Consumer.class.isInstance(args[args.length - 1])) {
                callBack = Consumer.class.cast(args[args.length - 1]);
            }

            JsonObject params = JsonObjectUtil.getParams(args, method);
            if (params.isEmpty()) {
                template.jsqlQuery(jsql.value(), callBack);
                return Void.class;
            } else {
                template.jsqlQuery(jsql.value(), params, callBack);
                return Void.class;
            }
        }
        return super.invoke(instance, method, args);
    }


    @SuppressWarnings("rawtypes")
	class DocumentCrudRepositoryAsync extends AbstractDocumentRepositoryAsync implements RepositoryAsync {

        private final DocumentTemplateAsync template;
        private final ClassRepresentation classRepresentation;
        private final Reflections reflections;

        DocumentCrudRepositoryAsync(DocumentTemplateAsync template, ClassRepresentation classRepresentation, Reflections reflections) {
            this.template = template;
            this.classRepresentation = classRepresentation;
            this.reflections = reflections;
        }

        @Override
        protected DocumentTemplateAsync getTemplate() {
            return template;
        }

        @Override
        protected Reflections getReflections() {
            return reflections;
        }

        @Override
        protected ClassRepresentation getClassRepresentation() {
            return classRepresentation;
        }
    }

}
