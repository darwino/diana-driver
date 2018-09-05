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

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.jnosql.artemis.Converters;
import org.jnosql.artemis.Repository;
import org.jnosql.artemis.document.DocumentTemplate;
import org.jnosql.artemis.document.query.AbstractDocumentRepository;
import org.jnosql.artemis.document.query.AbstractDocumentRepositoryProxy;
import org.jnosql.artemis.document.query.DocumentQueryDeleteParser;
import org.jnosql.artemis.document.query.DocumentQueryParser;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.artemis.reflection.Reflections;

import com.darwino.commons.json.JsonObject;

class DarwinoDocumentRepositoryProxy<T> extends AbstractDocumentRepositoryProxy<T> {

	private final Class<T> typeClass;
    private final DarwinoTemplate template;

    private final DocumentCrudRepository repository;
    private final ClassRepresentation classRepresentation;
    private final DocumentQueryParser queryParser;
    private final DocumentQueryDeleteParser deleteParser;
    private final Converters converters;


    @SuppressWarnings("unchecked")
	DarwinoDocumentRepositoryProxy(DarwinoTemplate template, ClassRepresentations classRepresentations,
                                    Class<?> repositoryType, Reflections reflections, Converters converters) {
        this.template = template;
        this.typeClass = Class.class.cast(ParameterizedType.class.cast(repositoryType.getGenericInterfaces()[0])
                .getActualTypeArguments()[0]);
        this.classRepresentation = classRepresentations.get(typeClass);
        this.repository = new DocumentCrudRepository(template, classRepresentation, reflections);
        this.queryParser = new DocumentQueryParser();
        this.deleteParser = new DocumentQueryDeleteParser();
        this.converters = converters;
    }


    @SuppressWarnings("rawtypes")
	@Override
    protected Repository getRepository() {
        return repository;
    }

    @Override
    protected DocumentQueryParser getQueryParser() {
        return queryParser;
    }

    @Override
    protected DocumentTemplate getTemplate() {
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

    @Override
    public Object invoke(Object o, Method method, Object[] args) throws Throwable {

    	// JSQL support
        JSQL jsql = method.getAnnotation(JSQL.class);
        if (Objects.nonNull(jsql)) {
            List<T> result = Collections.emptyList();
            JsonObject params = JsonObjectUtil.getParams(args, method);
            if (params.isEmpty()) {
                result = template.jsqlQuery(jsql.value());
            } else {
                result = template.jsqlQuery(jsql.value(), params);
            }
            return ReturnTypeConverterUtil.returnObject(result, typeClass, method);
        }
        
        // FT search support - assume the first parameter
        Search search = method.getAnnotation(Search.class);
        if(Objects.nonNull(search)) {
        	if(args.length != 1 || !(args[0] instanceof String)) {
        		throw new IllegalArgumentException("Methods annotated with @Search require a single String parameter");
        	}
        	String query = (String)args[0];
        	String[] orderBy = search.orderBy();
        	
        	List<T> result;
        	if(orderBy != null && orderBy.length > 0) {
        		result = template.search(query, Arrays.asList(orderBy));
        	} else {
        		result = template.search(query);
        	}
        	
            return ReturnTypeConverterUtil.returnObject(result, typeClass, method);
        }
        
        // Stored cursor support
		StoredCursor storedCursor = method.getAnnotation(StoredCursor.class);
		if (Objects.nonNull(storedCursor)) {
			List<T> result = Collections.emptyList();
			JsonObject params = JsonObjectUtil.getParams(args, method);
			result = template.storedCursor(storedCursor.value(), params);
			return ReturnTypeConverterUtil.returnObject(result, typeClass, method);
		}
        
        return super.invoke(o, method, args);
    }



    @SuppressWarnings("rawtypes")
	class DocumentCrudRepository extends AbstractDocumentRepository implements Repository {

        private final DocumentTemplate template;
        private final ClassRepresentation classRepresentation;
        private final Reflections reflections;

        DocumentCrudRepository(DocumentTemplate template, ClassRepresentation classRepresentation, Reflections reflections) {
            this.template = template;
            this.classRepresentation = classRepresentation;
            this.reflections = reflections;
        }


        @Override
        protected DocumentTemplate getTemplate() {
            return template;
        }

        @Override
        protected ClassRepresentation getClassRepresentation() {
            return classRepresentation;
        }

        @Override
        protected Reflections getReflections() {
            return reflections;
        }
    }

}
