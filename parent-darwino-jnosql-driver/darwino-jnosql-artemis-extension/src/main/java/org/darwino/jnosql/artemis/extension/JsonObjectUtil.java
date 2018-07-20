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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.stream.Stream;

import org.jnosql.artemis.Param;

import com.darwino.commons.json.JsonObject;

final class JsonObjectUtil {

	private JsonObjectUtil() {
	}

	static JsonObject getParams(Object[] args, Method method) {

		JsonObject jsonObject = new JsonObject.LinkedMap();
		Annotation[][] annotations = method.getParameterAnnotations();

		for (int index = 0; index < annotations.length; index++) {
			final Object arg = args[index];

			
			Optional<Param> param = Stream.of(annotations[index])
					.filter(Param.class::isInstance)
					.map(Param.class::cast)
					.findFirst();
			param.ifPresent(p -> jsonObject.put(p.value(), arg));
		}

		return jsonObject;
	}
}
