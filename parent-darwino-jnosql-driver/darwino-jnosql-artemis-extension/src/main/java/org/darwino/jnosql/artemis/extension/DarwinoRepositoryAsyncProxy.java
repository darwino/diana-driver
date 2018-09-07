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

import org.jnosql.artemis.RepositoryAsync;

import com.darwino.commons.json.JsonObject;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Consumer;

public class DarwinoRepositoryAsyncProxy<T> implements InvocationHandler {

	@SuppressWarnings("rawtypes")
	private static final Consumer NOOP = t -> {
    };

    private final DarwinoTemplateAsync template;
    private final RepositoryAsync<?, ?> repositoryAsync;


	DarwinoRepositoryAsyncProxy(DarwinoTemplateAsync template, RepositoryAsync<?, ?> repositoryAsync) {
        this.template = template;
        this.repositoryAsync = repositoryAsync;
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
        return method.invoke(repositoryAsync, args);
    }
}
