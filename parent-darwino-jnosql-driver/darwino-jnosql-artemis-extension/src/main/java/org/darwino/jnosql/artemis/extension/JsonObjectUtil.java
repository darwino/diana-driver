/**
 * Copyright © 2017-2019 Jesse Gallagher
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

import com.darwino.commons.json.JsonException;
import com.darwino.commons.json.JsonFactory;
import org.jnosql.artemis.Param;

enum JsonObjectUtil {
	;

	static Object getParams(Object[] args, Method method, JsonFactory fac) throws JsonException {

		Object jsonObject = fac.createObject();
		Annotation[][] annotations = method.getParameterAnnotations();

		for (int index = 0; index < annotations.length; index++) {
			final Object arg = args[index];

			
			Optional<Param> param = Stream.of(annotations[index])
					.filter(Param.class::isInstance)
					.map(Param.class::cast)
					.findFirst();
			if(param.isPresent()) {
				jsonObject = fac.setProperty(jsonObject, param.get().value(), arg);
			}
		}

		return jsonObject;
	}
}
