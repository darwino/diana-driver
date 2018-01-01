package org.darwino.jnosql.artemis.extension;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.stream.Stream;

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
