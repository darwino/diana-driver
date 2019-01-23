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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.darwino.commons.json.JsonFactory;
import com.darwino.platform.DarwinoContext;
import org.jnosql.artemis.Repository;

class DarwinoDocumentRepositoryProxy<T> implements InvocationHandler {

	private final Class<T> typeClass;
    private final DarwinoTemplate template;
    private final Repository<?, ?> repository;


    @SuppressWarnings("unchecked")
	DarwinoDocumentRepositoryProxy(DarwinoTemplate template, Class<?> repositoryType, Repository<?, ?> repository) {
        this.template = template;
        this.typeClass = (Class) ((ParameterizedType) repositoryType.getGenericInterfaces()[0])
                .getActualTypeArguments()[0];
        this.repository = repository;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] args) throws Throwable {

    	// JSQL support
        JSQL jsql = method.getAnnotation(JSQL.class);
        if (Objects.nonNull(jsql)) {
            List<T> result;
            JsonFactory fac = DarwinoContext.get().getSession().getJsonFactory();
            Object params = JsonObjectUtil.getParams(args, method, fac);
            if (fac.getPropertyCount(params) == 0) {
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
        	if(orderBy.length > 0) {
        		result = template.search(query, Arrays.asList(orderBy));
        	} else {
        		result = template.search(query);
        	}
        	
            return ReturnTypeConverterUtil.returnObject(result, typeClass, method);
        }
        
        // Stored cursor support
		StoredCursor storedCursor = method.getAnnotation(StoredCursor.class);
		if (Objects.nonNull(storedCursor)) {
			List<T> result;
            JsonFactory fac = DarwinoContext.get().getSession().getJsonFactory();
			Object params = JsonObjectUtil.getParams(args, method, fac);
			result = template.storedCursor(storedCursor.value(), params);
			return ReturnTypeConverterUtil.returnObject(result, typeClass, method);
		}
        
        return method.invoke(repository, args);
    }

}
