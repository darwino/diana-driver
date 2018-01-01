/**
 * Copyright © 2017 Jesse Gallagher
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

import org.jnosql.artemis.document.DocumentTemplate;
import org.jnosql.diana.api.document.DocumentQuery;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utilitarian class to return on dynamic query
 */
final class ReturnTypeConverterUtil {

    private ReturnTypeConverterUtil() {
    }


    @SuppressWarnings("rawtypes")
	static Object returnObject(DocumentQuery query, DocumentTemplate repository, Class typeClass, Method method) {
        Class<?> returnType = method.getReturnType();

        if (typeClass.equals(returnType)) {
            Optional<Object> optional = repository.singleResult(query);
            return optional.orElse(null);

        } else if (Optional.class.equals(returnType)) {
            return repository.singleResult(query);
        } else if (List.class.equals(returnType)
                || Iterable.class.equals(returnType)
                || Collection.class.equals(returnType)) {
            return repository.select(query);
        } else if (Set.class.equals(returnType)) {
            return repository.select(query).stream().collect(Collectors.toSet());
        } else if (Queue.class.equals(returnType)) {
            return repository.select(query).stream().collect(Collectors.toCollection(PriorityQueue::new));
        } else if (Stream.class.equals(returnType)) {
            return repository.select(query).stream();
        }

        return repository.select(query);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	static Object returnObject(List result, Class typeClass, Method method) {
        Class<?> returnType = method.getReturnType();

        if (typeClass.equals(returnType)) {
            if (!result.isEmpty()) {
                return result.get(0);
            } else {
                return null;
            }

        } else if (Optional.class.equals(returnType)) {
            return result.stream().findFirst();
        } else if (List.class.equals(returnType)
                || Iterable.class.equals(returnType)
                || Collection.class.equals(returnType)) {
            return result;
        } else if (Set.class.equals(returnType)) {
            return result.stream().collect(Collectors.toSet());
        } else if (Queue.class.equals(returnType)) {
            return result.stream().collect(Collectors.toCollection(PriorityQueue::new));
        } else if (Stream.class.equals(returnType)) {
            return result.stream();
        }

        return result;
    }
}